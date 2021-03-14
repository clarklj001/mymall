package de.killbuqs.mall.coupon.dao;

import de.killbuqs.mall.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author jlong
 * @email jie.long@killbuqs.de
 * @date 2021-03-14 23:20:27
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
