package de.killbuqs.mall.product.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.service.IService;

import de.killbuqs.common.utils.PageUtils;
import de.killbuqs.mall.product.entity.CategoryEntity;

/**
 * 商品三级分类
 *
 * @author jlong
 * @email jie.long@killbuqs.de
 * @date 2021-03-14 21:40:00
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

	List<CategoryEntity> listWithTree();

	void removeMenuByIds(List<Long> asList);

	/**
	 * 找到CatelogId完整路径
	 * 
	 * @param catelogId
	 * @return
	 */
	Long[] findCatelogPath(Long catelogId);
}

