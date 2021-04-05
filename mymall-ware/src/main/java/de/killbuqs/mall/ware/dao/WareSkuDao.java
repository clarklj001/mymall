package de.killbuqs.mall.ware.dao;

import de.killbuqs.mall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 商品库存
 * 
 * @author jlong
 * @email jie.long@killbuqs.de
 * @date 2021-03-14 23:48:08
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

	void addStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("skuNum") Integer skuNum);
	
}
