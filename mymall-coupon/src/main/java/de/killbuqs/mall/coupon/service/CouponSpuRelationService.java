package de.killbuqs.mall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import de.killbuqs.common.utils.PageUtils;
import de.killbuqs.mall.coupon.entity.CouponSpuRelationEntity;

import java.util.Map;

/**
 * 优惠券与产品关联
 *
 * @author jlong
 * @email jie.long@killbuqs.de
 * @date 2021-03-14 23:20:27
 */
public interface CouponSpuRelationService extends IService<CouponSpuRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

