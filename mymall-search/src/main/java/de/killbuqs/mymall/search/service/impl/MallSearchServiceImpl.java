package de.killbuqs.mymall.search.service.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import de.killbuqs.common.to.es.SkuEsModel;
import de.killbuqs.common.utils.R;
import de.killbuqs.mymall.search.config.MyMallElasticSearchConfig;
import de.killbuqs.mymall.search.constant.EsConstant;
import de.killbuqs.mymall.search.feign.ProductFeignService;
import de.killbuqs.mymall.search.service.MallSearchService;
import de.killbuqs.mymall.search.vo.AttrResponseVo;
import de.killbuqs.mymall.search.vo.BrandVo;
import de.killbuqs.mymall.search.vo.SearchParam;
import de.killbuqs.mymall.search.vo.SearchResult;
import de.killbuqs.mymall.search.vo.SearchResult.AttrVo;
import de.killbuqs.mymall.search.vo.SearchResult.NavVo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MallSearchServiceImpl implements MallSearchService {

	@Autowired
	private RestHighLevelClient client;

	@Autowired
	private ProductFeignService productFeignService;

	@Override
	public SearchResult search(SearchParam param) {

		SearchRequest searchRequest = buildSearchRequest(param);

		SearchResult result = null;

		try {
			SearchResponse response = client.search(searchRequest, MyMallElasticSearchConfig.COMMON_OPTIONS);
			result = buildSearchResult(response, param);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * ?????????????????? ?????????????????????(??????????????????????????????????????????????????????)?????????????????????????????????????????? // *
	 * 
	 * @param param
	 * @return
	 */
	private SearchRequest buildSearchRequest(SearchParam param) {

		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

		// ?????????????????????(??????????????????????????????????????????????????????)
		// 1. ??????boolQuery
		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
		// 1.1 must keyword ????????????
		if (!StringUtils.isEmpty(param.getKeyword())) {
			boolQuery.must(QueryBuilders.matchQuery("skuTitle", param.getKeyword()));
		}
		// 1.2 filter termQuery ??????????????????
		if (param.getCatalog3Id() != null) {
			boolQuery.filter(QueryBuilders.termQuery("catalogId", param.getCatalog3Id()));
		}
		// 1.3 filter termQuery ????????????
		if (param.getBrandId() != null) {
			if(param.getBrandId().size() == 1) {
				boolQuery.filter(QueryBuilders.termQuery("brandId", param.getBrandId().get(0)));
			} else {
				
				BoolQueryBuilder nestedBoolQuery = QueryBuilders.boolQuery();
				nestedBoolQuery.must(QueryBuilders.termsQuery("brandId", param.getBrandId()));
				boolQuery.filter(nestedBoolQuery);
			}
		}
		// 1.4 filter termQuery ????????????
		if (param.getAttrs() != null && !param.getAttrs().isEmpty()) {
			// attrs=1_3G&attrs=1_3G:4G:5G&attrs=2_??????&attrs=4_?????????
			for (String attr : param.getAttrs()) {
				BoolQueryBuilder nestedBoolQuery = QueryBuilders.boolQuery();
				String[] s = attr.split("_");
				String attrId = s[0];
				String[] attrValues = s[1].split(":");
				nestedBoolQuery.must(QueryBuilders.termQuery("attrs.attrId", attrId));
				nestedBoolQuery.must(QueryBuilders.termsQuery("attrs.attrValue", attrValues));
				NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery("attrs", nestedBoolQuery, ScoreMode.None);
				boolQuery.filter(nestedQuery);
			}
		}
		// 1.5 filter termQuery ????????????
		if (!StringUtils.isEmpty(param.getHasStock())) {
			boolQuery.filter(QueryBuilders.termQuery("hasStock", param.getHasStock() == 1));
		}

		// 1.5 filter termQuery ?????????????????? 1_500 / _500 / 500_
		if (!StringUtils.isEmpty(param.getSkuPrice())) {

			RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("skuPrice");

			String[] split = param.getSkuPrice().split("_");
			if (split.length == 2) {
				if (!split[0].isEmpty()) {
					rangeQuery.gte(Double.parseDouble(split[0]));
				}
				if (!split[1].isEmpty()) {
					rangeQuery.lte(Double.parseDouble(split[1]));
				}

			}
			boolQuery.filter(rangeQuery);
		}

		sourceBuilder.query(boolQuery);

		// 2. ???????????????????????????????????????
		// sort=saleCount_desc/asc
		// sort=skuPrice_desc/asc
		// sort=hotScore_desc/asc
		if (!StringUtils.isEmpty(param.getSort())) {
			String[] split = param.getSort().split("_");
			sourceBuilder.sort(split[0], split[1].equalsIgnoreCase("asc") ? SortOrder.ASC : SortOrder.DESC);
		}

		sourceBuilder.from((param.getPageNum() - 1) * EsConstant.PRODUCT_PAGESIZE);
		sourceBuilder.size(EsConstant.PRODUCT_PAGESIZE);

		// ??????keyword???????????????????????????????????????
		if (!StringUtils.isEmpty(param.getKeyword())) {
			HighlightBuilder highlightBuilder = new HighlightBuilder();
			highlightBuilder.field("skuTitle");
			highlightBuilder.preTags("<b style='color:red'>");
			highlightBuilder.postTags("</b>");
			sourceBuilder.highlighter(highlightBuilder);
		}

		// ????????????
		// ????????????
		TermsAggregationBuilder brandAggregation = AggregationBuilders.terms("brand_agg");
		brandAggregation.field("brandId").size(50);
		// ????????????????????????
		brandAggregation.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName").size(1));
		brandAggregation.subAggregation(AggregationBuilders.terms("brand_img_agg").field("brandImg").size(1));
		sourceBuilder.aggregation(brandAggregation);

		// ????????????
		TermsAggregationBuilder catalogAggregation = AggregationBuilders.terms("catalog_agg");
		catalogAggregation.field("catalogId").size(20);
		catalogAggregation.subAggregation(AggregationBuilders.terms("catalog_name_agg").field("catalogName").size(1));
		sourceBuilder.aggregation(catalogAggregation);

		// ?????????????????????attrId
		NestedAggregationBuilder nestedAttrAggAggregation = AggregationBuilders.nested("attr_agg", "attrs");

		TermsAggregationBuilder attrIdAggregation = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId");
		attrIdAggregation.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(1));
		attrIdAggregation.subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue").size(50));
		nestedAttrAggAggregation.subAggregation(attrIdAggregation);
		sourceBuilder.aggregation(nestedAttrAggAggregation);

		log.info("?????????DSL: " + sourceBuilder.toString());

		SearchRequest searchRequest = new SearchRequest(new String[] { EsConstant.PRODUCT_INDEX }, sourceBuilder);
		return searchRequest;
	}

	private SearchResult buildSearchResult(SearchResponse response, SearchParam param) {

		SearchHits hits = response.getHits();

		SearchResult result = new SearchResult();

		// 1. ??????????????????????????????
		List<SkuEsModel> products = new ArrayList<>();
		SearchHit[] searchHits = hits.getHits();
		if (searchHits != null && searchHits.length > 0) {
			for (SearchHit hit : searchHits) {
				String sourceAsString = hit.getSourceAsString();
				SkuEsModel esModel = JSON.parseObject(sourceAsString, SkuEsModel.class);
				// ????????????
				if (!StringUtils.isEmpty(param.getKeyword())) {
					HighlightField skuTitle = hit.getHighlightFields().get("skuTitle");
					esModel.setSkuTitle(skuTitle.getFragments()[0].string());
				}

				products.add(esModel);
			}
		}
		result.setProducts(products);

		// 2. ??????????????????????????????????????????
		List<SearchResult.AttrVo> attrVos = new ArrayList<>();
		ParsedNested attrAgg = response.getAggregations().get("attr_agg");
		ParsedLongTerms attrIdAgg = attrAgg.getAggregations().get("attr_id_agg");
		for (Bucket bucket : attrIdAgg.getBuckets()) {
			AttrVo attrVo = new SearchResult.AttrVo();
			// ??????id
			long attrId = bucket.getKeyAsNumber().longValue();
			attrVo.setAttrId(attrId);
			// ????????????
			ParsedStringTerms attrNameAgg = bucket.getAggregations().get("attr_name_agg");
			String attrName = attrNameAgg.getBuckets().get(0).getKeyAsString();
			attrVo.setAttrName(attrName);
			// ?????????
			ParsedStringTerms attrValueAgg = bucket.getAggregations().get("attr_value_agg");
			List<String> attrValues = attrValueAgg.getBuckets().stream().map(item -> {
				return item.getKeyAsString();
			}).collect(Collectors.toList());
			attrVo.setAttrValue(attrValues);
			attrVos.add(attrVo);
		}
		result.setAttrs(attrVos);

		// 3. ??????????????????????????????????????????
		List<SearchResult.BrandVo> brandVos = new ArrayList<>();
		ParsedLongTerms brandAgg = response.getAggregations().get("brand_agg");
		for (Bucket bucket : brandAgg.getBuckets()) {
			SearchResult.BrandVo brandVo = new SearchResult.BrandVo();
			// ??????id
			long brandId = bucket.getKeyAsNumber().longValue();
			brandVo.setBrandId(brandId);
			// ????????????
			ParsedStringTerms brandImgAgg = bucket.getAggregations().get("brand_img_agg");
			String brandImg = brandImgAgg.getBuckets().get(0).getKeyAsString();
			brandVo.setBrandImg(brandImg);
			// ????????????
			ParsedStringTerms brandNameAgg = bucket.getAggregations().get("brand_name_agg");
			String brandName = brandNameAgg.getBuckets().get(0).getKeyAsString();
			brandVo.setBrandName(brandName);
			brandVos.add(brandVo);
		}
		result.setBrands(brandVos);

		// 4. ??????????????????????????????????????????
		ParsedLongTerms catalogAgg = response.getAggregations().get("catalog_agg");

		List<SearchResult.CatalogVo> catalogVos = new ArrayList<>();
		for (Bucket bucket : catalogAgg.getBuckets()) {
			SearchResult.CatalogVo catalogVo = new SearchResult.CatalogVo();
			// ??????id
			catalogVo.setCatalogId(Long.parseLong(bucket.getKeyAsString()));
			ParsedStringTerms catalogNameAgg = bucket.getAggregations().get("catalog_name_agg");
			String catalogName = catalogNameAgg.getBuckets().get(0).getKeyAsString();
			// ????????????
			catalogVo.setCatalogName(catalogName);
			catalogVos.add(catalogVo);
		}
		result.setCatalogs(catalogVos);

		// 5. ????????????
		long total = hits.getTotalHits().value;
		int totalPages = total % EsConstant.PRODUCT_PAGESIZE == 0 ? (int) total / EsConstant.PRODUCT_PAGESIZE
				: (int) total / EsConstant.PRODUCT_PAGESIZE + 1;
		result.setTotal(total);
		result.setTotalPages(totalPages);
		result.setPageNum(param.getPageNum());

		List<Integer> pagerNavs = new ArrayList<>();
		for (int i = 1; i <= totalPages; i++) {
			pagerNavs.add(i);
		}
		result.setPageNavs(pagerNavs);

		// 6. ?????????????????????
		if(param.getAttrs() != null && param.getAttrs().size() > 0) {
			List<NavVo> navVos = param.getAttrs().stream().map(attr -> {
				NavVo navVo = new NavVo();
				String[] split = attr.split("_");
				navVo.setNavValue(split[1]);
				R r = productFeignService.attrInfo(Long.parseLong(split[0]));
				result.getAttrIds().add(Long.parseLong(split[0]));
				if (r.getCode() == 0) {
					AttrResponseVo data = r.getData("attr", new TypeReference<AttrResponseVo>() {
					});
					navVo.setNavName(data.getAttrName());
				} else {
					navVo.setNavName(split[0]);
				}
				
				//?????????????????????????????????
				// ??????????????????url??????????????????????????????
				// ????????????????????????????????????????????????
				String replace = replaceQueryString(param, attr, "attrs");
				navVo.setLink("http://search.mymall.com/list.html?"+replace);
				return navVo;
			}).collect(Collectors.toList());
			result.setNavs(navVos);
			
		}
		
		if(param.getBrandId() != null && param.getBrandId().size() > 0) {
			List<NavVo> navs = result.getNavs();
			NavVo navVo = new NavVo();
			navVo.setNavName("??????");
			R r = productFeignService.brandsInfo(param.getBrandId());
			StringBuffer buffer = new StringBuffer();
			if(r.getCode() == 0) {
				List<BrandVo> brands = r.getData("brands", new TypeReference<List<BrandVo>>() {});
				String replace = "";
				for (BrandVo brandVo : brands) {
					brandVo.getBrandName();
					buffer.append(brandVo.getBrandName()+ ";");
					replace += replaceQueryString(param, brandVo.getBrandId()+"", "brandId");
				}
				navVo.setNavValue(buffer.toString());
				navVo.setLink("http://search.mymall.com/list.html?"+replace);
			}
			navs.add(navVo);
		}
		
		// TODO ?????? ????????? ???????????????????????????

		return result;
	}

	private String replaceQueryString(SearchParam param, String attr, String key) {
		String encode = null;
		try {
			encode = URLEncoder.encode(attr, "UTF-8");
			encode = encode.replace("+", "20"); // ??????????????????????????????
		} catch(UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String replace = param.getQueryString().replace(key+"=" + encode, "").replace("&&", "&");
		return replace;
	}

}
