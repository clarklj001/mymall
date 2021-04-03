package de.killbuqs.mall.product.dao;

import de.killbuqs.mall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author jlong
 * @email jie.long@killbuqs.de
 * @date 2021-03-14 21:40:00
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
