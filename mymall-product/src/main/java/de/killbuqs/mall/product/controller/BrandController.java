package de.killbuqs.mall.product.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.killbuqs.common.utils.PageUtils;
import de.killbuqs.common.utils.R;
import de.killbuqs.common.valid.AddGroup;
import de.killbuqs.common.valid.UpdateGroup;
import de.killbuqs.common.valid.UpdateStatusGroup;
import de.killbuqs.mall.product.entity.BrandEntity;
import de.killbuqs.mall.product.service.BrandService;

/**
 * 品牌
 *
 * @author jlong
 * @email jie.long@killbuqs.de
 * @date 2021-03-14 21:40:00
 */
@RestController
@RequestMapping("product/brand")
public class BrandController {
	@Autowired
	private BrandService brandService;

	/**
	 * 列表
	 */
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params) {
		PageUtils page = brandService.queryPage(params);

		return R.ok().put("page", page);
	}

	/**
	 * 信息
	 */
	@RequestMapping("/info/{brandId}")
	public R info(@PathVariable("brandId") Long brandId) {
		BrandEntity brand = brandService.getById(brandId);

		return R.ok().put("brand", brand);
	}

	/**
	 * 保存
	 */
	@RequestMapping("/save")
	public R save(@Validated({ AddGroup.class }) @RequestBody BrandEntity brand/* , BindingResult result */) {

//    	Map<String, String> map = new HashMap<>();
//    	if(result.hasErrors()) {
//    		result.getFieldErrors().forEach(item -> {
//    			String message = item.getDefaultMessage();
//    			String field = item.getField();
//    			map.put(field, message);
//    		});
//    		return R.error(400, "提交的数据不合法").put("data", map);
//    	} else {
		brandService.save(brand);
//    	}
		return R.ok();
	}

	/**
	 * 修改
	 */
	@RequestMapping("/update")
	public R update(@Validated({ UpdateGroup.class }) @RequestBody BrandEntity brand) {
		brandService.updateById(brand);

		return R.ok();
	}
	
	/**
	 * 修改状态
	 */
	@RequestMapping("/update/status")
	public R updateStatus(@Validated({ UpdateStatusGroup.class }) @RequestBody BrandEntity brand) {
		brandService.updateById(brand);
		
		return R.ok();
	}

	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	public R delete(@RequestBody Long[] brandIds) {
		brandService.removeByIds(Arrays.asList(brandIds));

		return R.ok();
	}

}
