package de.killbuqs.mall.product.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import de.killbuqs.common.constant.ProductConstant;
import de.killbuqs.common.to.SkuHasStockVo;
import de.killbuqs.common.to.SkuReductionTo;
import de.killbuqs.common.to.SpuBoundTo;
import de.killbuqs.common.to.es.SkuEsModel;
import de.killbuqs.common.to.es.SkuEsModel.Attrs;
import de.killbuqs.common.utils.PageUtils;
import de.killbuqs.common.utils.Query;
import de.killbuqs.common.utils.R;
import de.killbuqs.mall.product.dao.SpuInfoDao;
import de.killbuqs.mall.product.entity.AttrEntity;
import de.killbuqs.mall.product.entity.BrandEntity;
import de.killbuqs.mall.product.entity.CategoryEntity;
import de.killbuqs.mall.product.entity.ProductAttrValueEntity;
import de.killbuqs.mall.product.entity.SkuImagesEntity;
import de.killbuqs.mall.product.entity.SkuInfoEntity;
import de.killbuqs.mall.product.entity.SkuSaleAttrValueEntity;
import de.killbuqs.mall.product.entity.SpuInfoDescEntity;
import de.killbuqs.mall.product.entity.SpuInfoEntity;
import de.killbuqs.mall.product.feign.CouponFeignService;
import de.killbuqs.mall.product.feign.SearchFeignService;
import de.killbuqs.mall.product.feign.WareFeignService;
import de.killbuqs.mall.product.service.AttrService;
import de.killbuqs.mall.product.service.BrandService;
import de.killbuqs.mall.product.service.CategoryService;
import de.killbuqs.mall.product.service.ProductAttrValueService;
import de.killbuqs.mall.product.service.SkuImagesService;
import de.killbuqs.mall.product.service.SkuInfoService;
import de.killbuqs.mall.product.service.SkuSaleAttrValueService;
import de.killbuqs.mall.product.service.SpuImagesService;
import de.killbuqs.mall.product.service.SpuInfoDescService;
import de.killbuqs.mall.product.service.SpuInfoService;
import de.killbuqs.mall.product.vo.Attr;
import de.killbuqs.mall.product.vo.BaseAttrs;
import de.killbuqs.mall.product.vo.Bounds;
import de.killbuqs.mall.product.vo.Images;
import de.killbuqs.mall.product.vo.Skus;
import de.killbuqs.mall.product.vo.SpuSaveVo;
import feign.RequestTemplate;
import feign.RetryableException;

@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

	@Autowired
	private SpuInfoDescService spuInfoDescService;

	@Autowired
	private SpuImagesService spuImagesService;

	@Autowired
	private AttrService attrService;

	@Autowired
	private ProductAttrValueService productAttrValueService;

	@Autowired
	private SkuInfoService skuInfoService;

	@Autowired
	private SkuImagesService skuImagesService;

	@Autowired
	private SkuSaleAttrValueService skuSaleAttrValueService;

	@Autowired
	private CouponFeignService couponFeignService;

	@Autowired
	private BrandService brandService;

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private WareFeignService wareFeignService;

	@Autowired
	private SearchFeignService searchFeignService;

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		IPage<SpuInfoEntity> page = this.page(new Query<SpuInfoEntity>().getPage(params),
				new QueryWrapper<SpuInfoEntity>());

		return new PageUtils(page);
	}

	@Transactional
	@Override
	public void saveSpuInfo(SpuSaveVo vo) {
		// 1.??????spu???????????? pms_spu_info
		SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
		BeanUtils.copyProperties(vo, spuInfoEntity);
		spuInfoEntity.setCreateTime(new Date());
		spuInfoEntity.setUpdateTime(new Date());
		this.save(spuInfoEntity);

		// 2.??????spu??????????????? pms_spu_desc
		List<String> decript = vo.getDecript();
		SpuInfoDescEntity descEntity = new SpuInfoDescEntity();
		descEntity.setSpuId(spuInfoEntity.getId());
		descEntity.setDecript(String.join(",", decript));
		spuInfoDescService.save(descEntity);

		// 3.??????spu???????????? pms_spu_images
		List<String> images = vo.getImages();
		spuImagesService.saveImages(spuInfoEntity.getId(), images);

		// 4.??????spu??????????????? pms_product_attr_value
		List<BaseAttrs> baseAttrs = vo.getBaseAttrs();
		List<ProductAttrValueEntity> collect = baseAttrs.stream().map(attr -> {
			ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
			productAttrValueEntity.setAttrId(attr.getAttrId());
			AttrEntity attrEntity = attrService.getById(attr.getAttrId());
			productAttrValueEntity.setAttrName(attrEntity.getAttrName());
			productAttrValueEntity.setAttrValue(attr.getAttrValues());
			productAttrValueEntity.setQuickShow(attr.getShowDesc());
			productAttrValueEntity.setSpuId(spuInfoEntity.getId());
			return productAttrValueEntity;
		}).collect(Collectors.toList());
		productAttrValueService.saveBatch(collect);

		// 5.??????spu??????????????? mymall_sms.sms_spu_bounds
		Bounds bounds = vo.getBounds();
		SpuBoundTo spuBoundTo = new SpuBoundTo();
		BeanUtils.copyProperties(bounds, spuBoundTo);
		spuBoundTo.setSpuId(spuInfoEntity.getId());
		R saveSpuBounds = couponFeignService.saveSpuBounds(spuBoundTo);
		if (saveSpuBounds.getCode() != 0) {
			log.error("????????????spu??????????????????");
		}

		// 6.????????????spu???????????????sku?????? VO: Skurs
		List<Skus> skus = vo.getSkus();
		if (skus != null && skus.size() > 0) {
			skus.forEach(item -> {
				String defaultImg = "";

				for (Images image : item.getImages()) {
					if (image.getDefaultImg() == 1) {
						defaultImg = image.getImgUrl();
					}
				}

				SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
				BeanUtils.copyProperties(item, skuInfoEntity);
				skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
				skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
				skuInfoEntity.setSaleCount(0L);
				skuInfoEntity.setSpuId(spuInfoEntity.getId());
				skuInfoEntity.setSkuDefaultImg(defaultImg);
				// 6.1.??????sku??????????????? pms_sku_info
				skuInfoService.save(skuInfoEntity);

				Long skuId = skuInfoEntity.getSkuId();
				List<SkuImagesEntity> imagesEntities = item.getImages().stream().map(img -> {
					SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
					skuImagesEntity.setSkuId(skuId);
					skuImagesEntity.setImgUrl(img.getImgUrl());
					skuImagesEntity.setDefaultImg(img.getDefaultImg());
					return skuImagesEntity;
				}).filter(img -> !StringUtils.isEmpty(img.getImgUrl())).collect(Collectors.toList());

				// 6.2.??????sku??????????????? pms_sku_images
				skuImagesService.saveBatch(imagesEntities);

				List<Attr> attr = item.getAttr();
				Long finalSkuId = skuId;
				List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = attr.stream().map(attrVo -> {
					SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
					BeanUtils.copyProperties(attrVo, skuSaleAttrValueEntity);
					skuSaleAttrValueEntity.setSkuId(finalSkuId);
					return skuSaleAttrValueEntity;
				}).collect(Collectors.toList());
				// 6.3.??????sku????????????????????? pms_sku_sale_attr_value
				skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);

				// 6.4.??????sku???????????????????????? ???????????? mymall_sms.sms_sku_ladder, sms_sku_full_reduction,
				// sms_member_price
				SkuReductionTo skuReductionTo = new SkuReductionTo();
				BeanUtils.copyProperties(item, skuReductionTo);
				skuReductionTo.setSkuId(skuId);

				// ???????????????0??????????????????????????????????????????
				if (skuReductionTo.getFullCount() > 0
						|| skuReductionTo.getFullPrice().compareTo(new BigDecimal(0)) == 1) {
					R saveSkuReduction = couponFeignService.saveSkuReduction(skuReductionTo);
					if (saveSkuReduction.getCode() == 0) {
						log.error("????????????sku??????????????????");
					}
				}

			});

		}

	}

	@Override
	public PageUtils queryPageByCondition(Map<String, Object> params) {

		QueryWrapper<SpuInfoEntity> queryWrapper = new QueryWrapper<SpuInfoEntity>();

		String key = (String) params.get("key");
		if (!StringUtils.isEmpty(key)) {
			queryWrapper.and(wrapper -> {
				wrapper.eq("id", key).or().like("spu_name", key);
			});
		}
		String status = (String) params.get("status");
		if (!StringUtils.isEmpty(status)) {
			queryWrapper.eq("publish_status", status);
		}
		String brandId = (String) params.get("brandId");
		if (!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId)) {
			queryWrapper.eq("brand_id", brandId);
		}
		String catelogId = (String) params.get("catelogId");
		if (!StringUtils.isEmpty(catelogId) && !"0".equalsIgnoreCase(catelogId)) {
			queryWrapper.eq("catalog_id", catelogId);
		}

		IPage<SpuInfoEntity> page = this.page(new Query<SpuInfoEntity>().getPage(params), queryWrapper);

		return new PageUtils(page);
	}

	/**
	 * Data see: product.json
	 */
	@Override
	public void up(Long spuId) {

		// attrs: attrId, attrName, attrValue
		// ????????????sku?????????????????????????????????????????????
		// ???????????? pms.product_attr_value ??????????????? pms_attr???
		List<ProductAttrValueEntity> baseAttrs = productAttrValueService.baseAttrListForSpu(spuId);
		List<Long> attrIds = baseAttrs.stream().map(attr -> attr.getAttrId()).collect(Collectors.toList());

		List<Long> searchAttrIds = attrService.selectSearchAttrIds(attrIds);

		Set<Long> idSet = new HashSet<>(searchAttrIds);

		List<Attrs> attrsList = baseAttrs.stream().filter(item -> idSet.contains(item.getAttrId()))//
				.map(item -> {
					Attrs attrs = new SkuEsModel.Attrs();
					BeanUtils.copyProperties(item, attrs);
					return attrs;
				}).collect(Collectors.toList());

		// ????????????spuid???????????????sku????????????????????????
		List<SkuInfoEntity> skuInfoEntities = skuInfoService.getSkusBySpuId(spuId);

		List<Long> skuIdList = skuInfoEntities.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());
		Map<Long, Boolean> stockMap = null;
		try {
			R hasStock = wareFeignService.getSkusHasStock(skuIdList);

			TypeReference<List<SkuHasStockVo>> typeReference = new TypeReference<List<SkuHasStockVo>>() {
			};
			stockMap = hasStock.getData("data", typeReference).stream()
					.collect(Collectors.toMap(SkuHasStockVo::getSkuId, SkuHasStockVo::getHasStock));

		} catch (Exception e) {
			log.error("????????????????????????????????????{}" + e);
		}
		Map<Long, Boolean> finalStockMap = stockMap;

		// ????????????sku?????????
		List<SkuEsModel> upProducts = skuInfoEntities.stream().map(sku -> {
			SkuEsModel esModel = new SkuEsModel();
			BeanUtils.copyProperties(sku, esModel);
			// skuPrice, skuImg
			esModel.setSkuPrice(sku.getPrice());
			esModel.setSkuImg(sku.getSkuDefaultImg());
			// hasStock
			// ???????????????????????????????????????
			if (finalStockMap == null) {
				esModel.setHasStock(true);
			} else {
				esModel.setHasStock(finalStockMap.get(sku.getSkuId()));
			}

			// hotScore
			// ???????????? ??????0 TODO: ???????????????
			esModel.setHotScore(0L);

			// brandName, brandImg
			// ????????????????????????????????????
			BrandEntity brandEntity = brandService.getById(sku.getBrandId());
			esModel.setBrandName(brandEntity.getName());
			esModel.setBrandImg(brandEntity.getLogo());

			// catalogName
			CategoryEntity categoryEntity = categoryService.getById(sku.getCatalogId());
			esModel.setCatalogName(categoryEntity.getName());

			// ?????????????????????
			esModel.setAttrs(attrsList);

			return esModel;
		}).collect(Collectors.toList());

		// ???????????????ES????????????
		R r = searchFeignService.productStatusUp(upProducts);

		if (r.getCode() == 0) {
			// ??????????????????
			// ????????????spu?????????
			this.baseMapper.updateSpuStatus(spuId, ProductConstant.StatusEnum.SPU_UP.getCode());

		}
		{
			// ??????????????????
			// TODO: ???????????? ??????????????????
			// Feign ???????????? SynchronousMethodHandler
			// 1. ???????????????????????????????????? json??? RequestTemplate template =
			// buildTemplateFromArgs.create(argv);
			// 2. ??????????????????????????????????????????????????????????????? executeAndDecode(template);
			// 3. ??????????????????????????????
			// while (true) {
			// try {
			// return executeAndDecode(template, options);
			// } catch (RetryableException e) {
			// try {
			// retryer.continueOrPropagate(e);
			// } catch() {
			// }
		}

	}

}