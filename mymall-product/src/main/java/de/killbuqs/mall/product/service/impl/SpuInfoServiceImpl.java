package de.killbuqs.mall.product.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import de.killbuqs.common.to.SkuReductionTo;
import de.killbuqs.common.to.SpuBoundTo;
import de.killbuqs.common.utils.PageUtils;
import de.killbuqs.common.utils.Query;
import de.killbuqs.common.utils.R;
import de.killbuqs.mall.product.dao.SpuInfoDao;
import de.killbuqs.mall.product.entity.AttrEntity;
import de.killbuqs.mall.product.entity.ProductAttrValueEntity;
import de.killbuqs.mall.product.entity.SkuImagesEntity;
import de.killbuqs.mall.product.entity.SkuInfoEntity;
import de.killbuqs.mall.product.entity.SkuSaleAttrValueEntity;
import de.killbuqs.mall.product.entity.SpuInfoDescEntity;
import de.killbuqs.mall.product.entity.SpuInfoEntity;
import de.killbuqs.mall.product.feign.CouponFeignService;
import de.killbuqs.mall.product.service.AttrService;
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

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		IPage<SpuInfoEntity> page = this.page(new Query<SpuInfoEntity>().getPage(params),
				new QueryWrapper<SpuInfoEntity>());

		return new PageUtils(page);
	}

	@Transactional
	@Override
	public void saveSpuInfo(SpuSaveVo vo) {
		// 1.保存spu基本信息 pms_spu_info
		SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
		BeanUtils.copyProperties(vo, spuInfoEntity);
		spuInfoEntity.setCreateTime(new Date());
		spuInfoEntity.setUpdateTime(new Date());
		this.save(spuInfoEntity);

		// 2.保存spu的描述图片 pms_spu_desc
		List<String> decript = vo.getDecript();
		SpuInfoDescEntity descEntity = new SpuInfoDescEntity();
		descEntity.setSpuId(spuInfoEntity.getId());
		descEntity.setDecript(String.join(",", decript));
		spuInfoDescService.save(descEntity);

		// 3.保存spu的图片集 pms_spu_images
		List<String> images = vo.getImages();
		spuImagesService.saveImages(spuInfoEntity.getId(), images);

		// 4.保存spu的规格参数 pms_product_attr_value
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

		// 5.保存spu的积分信息 mymall_sms.sms_spu_bounds
		Bounds bounds = vo.getBounds();
		SpuBoundTo spuBoundTo = new SpuBoundTo();
		BeanUtils.copyProperties(bounds, spuBoundTo);
		spuBoundTo.setSpuId(spuInfoEntity.getId());
		R saveSpuBounds = couponFeignService.saveSpuBounds(spuBoundTo);
		if (saveSpuBounds.getCode() != 0) {
			log.error("远程保存spu积分信息失败");
		}

		// 6.保存当前spu对应的所有sku信息 VO: Skurs
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
				// 6.1.保存sku的基本信息 pms_sku_info
				skuInfoService.save(skuInfoEntity);

				Long skuId = skuInfoEntity.getSkuId();
				List<SkuImagesEntity> imagesEntities = item.getImages().stream().map(img -> {
					SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
					skuImagesEntity.setSkuId(skuId);
					skuImagesEntity.setImgUrl(img.getImgUrl());
					skuImagesEntity.setDefaultImg(img.getDefaultImg());
					return skuImagesEntity;
				}).filter(img -> !StringUtils.isEmpty(img.getImgUrl())).collect(Collectors.toList());

				// 6.2.保存sku的图片信息 pms_sku_images
				skuImagesService.saveBatch(imagesEntities);

				List<Attr> attr = item.getAttr();
				List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = attr.stream().map(attrVo -> {
					SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
					BeanUtils.copyProperties(attrVo, skuSaleAttrValueEntity);
					return skuSaleAttrValueEntity;
				}).collect(Collectors.toList());
				// 6.3.保存sku的销售属性信息 pms_sku_sale_attr_value
				skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);

				// 6.4.保存sku的优惠，满减信息 跨库操作 mymall_sms.sms_sku_ladder, sms_sku_full_reduction,
				// sms_member_price
				SkuReductionTo skuReductionTo = new SkuReductionTo();
				BeanUtils.copyProperties(item, skuReductionTo);
				skuReductionTo.setSkuId(skuId);

				// 满减信息为0的时候，数据不需要提交数据库
				if (skuReductionTo.getFullCount() > 0
						|| skuReductionTo.getFullPrice().compareTo(new BigDecimal(0)) == 1) {
					R saveSkuReduction = couponFeignService.saveSkuReduction(skuReductionTo);
					if (saveSkuReduction.getCode() == 0) {
						log.error("远程保存sku优惠信息失败");
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

}