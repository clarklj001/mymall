package de.killbuqs.mall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;

import de.killbuqs.common.to.SkuReductionTo;
import de.killbuqs.common.utils.PageUtils;
import de.killbuqs.mall.coupon.entity.SkuFullReductionEntity;

import java.util.Map;

/**
 * 商品满减信息
 *
 * @author jlong
 * @email jie.long@killbuqs.de
 * @date 2021-03-14 23:20:27
 */
public interface SkuFullReductionService extends IService<SkuFullReductionEntity> {

    PageUtils queryPage(Map<String, Object> params);

	void saveSkuReduction(SkuReductionTo skuReductionTo);
}

