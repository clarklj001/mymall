package de.killbuqs.mall.product.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.killbuqs.common.utils.PageUtils;
import de.killbuqs.common.utils.R;
import de.killbuqs.mall.product.entity.AttrGroupEntity;
import de.killbuqs.mall.product.service.AttrGroupService;
import de.killbuqs.mall.product.service.CategoryService;



/**
 * 属性分组
 *
 * @author jlong
 * @email jie.long@killbuqs.de
 * @date 2021-03-14 21:40:00
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;
    
    @Autowired
    private CategoryService categoryService;

    /**
     * 请求参数
     * {
     *  page: 1,//当前页码
     *  limit: 10,//每页记录数
     *  sidx: 'id',//排序字段
     *  order: 'asc/desc',//排序方式
     *  key: '华为'//检索关键字
     * }
     * 
     * 响应数据
	 * {
	 * 	"msg": "success",
	 * 	"code": 0,
	 * 	"page": {
	 * 		"totalCount": 0,
	 * 		"pageSize": 10,
	 * 		"totalPage": 0,
	 * 		"currPage": 1,
	 * 		"list": [{
	 * 			"attrGroupId": 0, //分组id
	 * 			"attrGroupName": "string", //分组名
	 * 			"catelogId": 0, //所属分类
	 * 			"descript": "string", //描述
	 * 			"icon": "string", //图标
	 * 			"sort": 0 //排序
	 * 			"catelogPath": [2,45,225] //分类完整路径
	 * 		}]
	 * 	}
	 * }
     * 
     * 列表
     */
    @RequestMapping("/list/{categoryId}")
    public R list(@RequestParam Map<String, Object> params, @PathVariable("categoryId") Long categoryId){
//        PageUtils page = attrGroupService.queryPage(params);
        
        PageUtils page = attrGroupService.queryPage(params, categoryId);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);
		
		Long catelogId = attrGroup.getCatelogId();
		Long[] catelogPath = categoryService.findCatelogPath(catelogId);
		
		attrGroup.setCatelogPath(catelogPath);

        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

}
