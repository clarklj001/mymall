package de.killbuqs.mall.order.dao;

import de.killbuqs.mall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author jlong
 * @email jie.long@killbuqs.de
 * @date 2021-03-14 23:42:22
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
