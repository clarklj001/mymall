package de.killbuqs.mall.coupon.dao;

import de.killbuqs.mall.coupon.entity.SeckillPromotionEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 秒杀活动
 * 
 * @author jlong
 * @email jie.long@killbuqs.de
 * @date 2021-03-14 23:20:27
 */
@Mapper
public interface SeckillPromotionDao extends BaseMapper<SeckillPromotionEntity> {
	
}
