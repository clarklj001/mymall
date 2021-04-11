package de.killbuqs.mall.product.app;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import de.killbuqs.common.utils.PageUtils;
import de.killbuqs.common.utils.R;
import de.killbuqs.mall.product.entity.BrandEntity;
import de.killbuqs.mall.product.entity.CategoryBrandRelationEntity;
import de.killbuqs.mall.product.service.CategoryBrandRelationService;
import de.killbuqs.mall.product.vo.BrandVo;



/**
 * 品牌分类关联
 *
 * @author jlong
 * @email jie.long@killbuqs.de
 * @date 2021-03-14 21:40:00
 */
@RestController
@RequestMapping("product/categorybrandrelation")
public class CategoryBrandRelationController {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = categoryBrandRelationService.queryPage(params);

        return R.ok().put("page", page);
    }
    
    
    /**
     * https://easydoc.xyz/s/78237135/ZUqEdvA4/HgVjlzWV
     * 
	 *响应数据
	 *{
	 *	"msg": "success",
	 *	"code": 0,
	 *	"data": [{
	 *		"brandId": 0,
	 *		"brandName": "string",
	 *	}]
	 *}
     * 
     * 获取分类关联的品牌
     */
    @RequestMapping("/brands/list")
    public R relationBrandsList(@RequestParam(value = "catId", required = true) Long catId){
    	
    	List<BrandEntity> brandEntities = categoryBrandRelationService.getBrandsByCatId(catId);
    	
    	List<BrandVo> brandVos = brandEntities.stream().map( brand -> {
    		BrandVo brandVo = new BrandVo();
    		brandVo.setBrandId(brand.getBrandId());
    		brandVo.setBrandName(brand.getName());
    		return brandVo;
    	}).collect(Collectors.toList());
    	
    	return R.ok().put("data", brandVos);
    }
    
    /**
     * 
     * https://easydoc.xyz/s/78237135/ZUqEdvA4/SxysgcEF
     * 
	 * 响应数据
	 *{
	 *	"msg": "success",
	 *	"code": 0,
	 *	"data": [{
	 *		"catelogId": 0,
	 *		"catelogName": "string",
	 *	}]
	 *}
     * 
     * 获取当前品牌关联的所有分类列表
     */
    @GetMapping("/catelog/list")
    public R catelogList(@RequestParam("brandId") Long brandId){
    	
    	Wrapper<CategoryBrandRelationEntity> queryWrapper = new QueryWrapper<CategoryBrandRelationEntity>().eq("brand_id", brandId);
		List<CategoryBrandRelationEntity> data = categoryBrandRelationService.list(queryWrapper );
    	
    	
    	return R.ok().put("data", data);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		CategoryBrandRelationEntity categoryBrandRelation = categoryBrandRelationService.getById(id);

        return R.ok().put("categoryBrandRelation", categoryBrandRelation);
    }

    /**
     * 
     * https://easydoc.xyz/s/78237135/ZUqEdvA4/7jWJki5e
     * 
	 * 请求参数
	 *{"brandId":1,"catelogId":2}
	 *响应数据
	 *{
	 *	"msg": "success",
	 *	"code": 0
	 *}
     * 
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
    	
		categoryBrandRelationService.saveDetail(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
		categoryBrandRelationService.updateById(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		categoryBrandRelationService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
