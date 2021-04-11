package de.killbuqs.mymall.search.service.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

import de.killbuqs.common.to.es.SkuEsModel;
import de.killbuqs.mymall.search.config.MyMallElasticSearchConfig;
import de.killbuqs.mymall.search.constant.EsConstant;
import de.killbuqs.mymall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProductSaveServiceImpl implements ProductSaveService {

	@Autowired
	private RestHighLevelClient client;

	@Override
	public boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException {

		BulkRequest bulkRequest = new BulkRequest();

		for (SkuEsModel skuEsModel : skuEsModels) {
			IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
			indexRequest.id(skuEsModel.getSkuId().toString());
			String jsonString = JSON.toJSONString(skuEsModel);
			indexRequest.source(jsonString, XContentType.JSON);

			bulkRequest.add(indexRequest);
		}

		BulkResponse bulk = client.bulk(bulkRequest, MyMallElasticSearchConfig.COMMON_OPTIONS);

		// TODO: 如果有批量错误
		boolean hasFailures = bulk.hasFailures();
		List<String> collect = Arrays.stream(bulk.getItems()).map(BulkItemResponse::getId).collect(Collectors.toList());
		log.info("商品上架完成“ {}, 返回数据： {}", collect, bulk.toString());
		return !hasFailures;
	}

}
