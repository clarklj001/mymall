package de.killbuqs.mall.order.dao;

import de.killbuqs.mall.order.entity.OrderItemEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单项信息
 * 
 * @author jlong
 * @email jie.long@killbuqs.de
 * @date 2021-03-14 23:42:22
 */
@Mapper
public interface OrderItemDao extends BaseMapper<OrderItemEntity> {
	
}
