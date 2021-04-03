package de.killbuqs.mall.product.controller;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.killbuqs.common.utils.R;
import de.killbuqs.mall.product.entity.CategoryEntity;
import de.killbuqs.mall.product.service.CategoryService;

/**
 * 商品三级分类
 *
 * @author jlong
 * @email jie.long@killbuqs.de
 * @date 2021-03-14 21:40:00
 */
@RestController
@RequestMapping("product/category")
public class CategoryController {
	@Autowired
	private CategoryService categoryService;

	/**
	 * 查出所有分类以及子分类
	 */
	@RequestMapping("/list/tree")
	public R list() {

		List<CategoryEntity> entities = categoryService.listWithTree();

		return R.ok().put("data", entities);
	}

	/**
	 * 信息
	 */
	@RequestMapping("/info/{catId}")
	public R info(@PathVariable("catId") Long catId) {
		CategoryEntity category = categoryService.getById(catId);

		return R.ok().put("data", category);
	}

	/**
	 * 保存
	 */
	@RequestMapping("/save")
	public R save(@RequestBody CategoryEntity category) {
		categoryService.save(category);
		return R.ok();
	}

	/**
	 * 修改
	 */
	@RequestMapping("/update")
	public R update(@RequestBody CategoryEntity category) {
		categoryService.updateById(category);

		return R.ok();
	}

	@RequestMapping("/update/sort")
	public R updateSort(@RequestBody Collection<CategoryEntity> categories) {
		boolean updateBatchById = categoryService.updateBatchById(categories);

		return R.ok();
	}

	/**
	 * 删除
	 */
	@PostMapping
	@RequestMapping("/delete")
	public R delete(@RequestBody Long[] catIds) {
//		categoryService.removeByIds(Arrays.asList(catIds));
		categoryService.removeMenuByIds(Arrays.asList(catIds));
		return R.ok();
	}

}
