package de.killbuqs.mall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.killbuqs.common.utils.PageUtils;
import de.killbuqs.common.utils.R;
import de.killbuqs.mall.product.entity.AttrEntity;
import de.killbuqs.mall.product.entity.AttrGroupEntity;
import de.killbuqs.mall.product.service.AttrAttrgroupRelationService;
import de.killbuqs.mall.product.service.AttrGroupService;
import de.killbuqs.mall.product.service.AttrService;
import de.killbuqs.mall.product.service.CategoryService;
import de.killbuqs.mall.product.vo.AttrGroupRelationVo;
import de.killbuqs.mall.product.vo.AttrGroupWithAttrsVo;

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
	private AttrService attrService;
	
	@Autowired
	private AttrAttrgroupRelationService relationService;

	@Autowired
	private CategoryService categoryService;

	/**
	 * https://easydoc.xyz/s/78237135/ZUqEdvA4/VhgnaedC
	 * 
	 * 请求参数 [{ "attrGroupId": 0, //分组id "attrId": 0, //属性id }]
	 * 
	 * 响应数据 { "msg": "success", "code": 0 }
	 * 
	 * @return
	 */
	@PostMapping("/attr/relation")
	public R addRelation(@RequestBody List<AttrGroupRelationVo> vos) {
		
		relationService.saveBatch(vos);

		return R.ok();
	}

	/**
	 * 
	 * https://easydoc.xyz/s/78237135/ZUqEdvA4/6JM6txHf
	 * 
	 * 获取分类下所有分组&关联属性
	 * 
	 * @param catelogId
	 * @return
	 */
	@GetMapping("/{catelogId}/withattr")
	public R getAttrGroupWithAttrs(@PathVariable("catelogId") Long catelogId) {
		// 查出当前分类下的所有属性分组
		// 查出每个属性分组的所有属性
		List<AttrGroupWithAttrsVo> vos = attrGroupService.getAttrGroupWithAttrsByCatelogId(catelogId);
		
		return R.ok().put("data", vos);
	}

	/**
	 * 
	 * https://easydoc.xyz/s/78237135/ZUqEdvA4/LnjzZHPj
	 *
	 * 响应数据 { "msg": "success", "code": 0, "data": [ { "attrId": 4, "attrName":
	 * "aad", "searchType": 1, "valueType": 1, "icon": "qq", "valueSelect": "v;q;w",
	 * "attrType": 1, "enable": 1, "catelogId": 225, "showDesc": 1 } ] }
	 * 
	 * 信息
	 */
	@RequestMapping("/{attrgroupId}/attr/relation")
	public R attrRelation(@PathVariable("attrgroupId") Long attrgroupId) {

		List<AttrEntity> entities = attrService.getRelationAttr(attrgroupId);

		return R.ok().put("data", entities);
	}

	/**
	 * https://easydoc.xyz/s/78237135/ZUqEdvA4/d3EezLdO
	 * 
	 * 获取属性分组没有关联的其他属性ForSum 最后修改于 2019-11-16 GET
	 * /product/attrgroup/{attrgroupId}/noattr/relation 接口描述
	 * 获取属性分组里面还没有关联的本分类里面的其他基本属性，方便添加新的关联 请求参数 { page: 1,//当前页码 limit: 10,//每页记录数
	 * sidx: 'id',//排序字段 order: 'asc/desc',//排序方式 key: '华为'//检索关键字 } 分页数据
	 *
	 * 响应数据 { "msg": "success", "code": 0, "page": { "totalCount": 3, "pageSize":
	 * 10, "totalPage": 1, "currPage": 1, "list": [{ "attrId": 1, "attrName": "aaa",
	 * "searchType": 1, "valueType": 1, "icon": "aa", "valueSelect":
	 * "aa;ddd;sss;aaa2", "attrType": 1, "enable": 1, "catelogId": 225, "showDesc":
	 * 1 }] } }
	 *
	 *
	 */
	@RequestMapping("/{attrgroupId}/noattr/relation")
	public R attrNoRelation(@PathVariable("attrgroupId") Long attrgroupId, @RequestParam Map<String, Object> params) {

		PageUtils page = attrService.getNoRelationAttr(params, attrgroupId);

		return R.ok().put("data", page);
	}

	/**
	 * https://easydoc.xyz/s/78237135/ZUqEdvA4/OXTgKobR
	 * 
	 * 请求参数 { page: 1,//当前页码 limit: 10,//每页记录数 sidx: 'id',//排序字段 order:
	 * 'asc/desc',//排序方式 key: '华为'//检索关键字 } 分页数据
	 *
	 * 响应数据 { "msg": "success", "code": 0, "page": { "totalCount": 0, "pageSize":
	 * 10, "totalPage": 0, "currPage": 1, "list": [{ "attrGroupId": 0, //分组id
	 * "attrGroupName": "string", //分组名 "catelogId": 0, //所属分类 "descript": "string",
	 * //描述 "icon": "string", //图标 "sort": 0 //排序 "catelogPath": [2,45,225] //分类完整路径
	 * }] } }
	 * 
	 * @param params
	 * @param categoryId
	 * @return
	 */
	@RequestMapping("/list/{categoryId}")
	public R list(@RequestParam Map<String, Object> params, @PathVariable("categoryId") Long categoryId) {

		PageUtils page = attrGroupService.queryPage(params, categoryId);

		return R.ok().put("page", page);
	}

	/**
	 * 
	 * https://easydoc.xyz/s/78237135/ZUqEdvA4/zX1XGjn2
	 *
	 * { "code": 0, "msg": "success", "attrGroup": { "attrGroupId": 1,
	 * "attrGroupName": "主体", "sort": 0, "descript": null, "icon": null,
	 * "catelogId": 225, "catelogPath": [ 2, 34, 225 ] //完整分类路径 } }
	 * 
	 * 信息
	 */
	@RequestMapping("/info/{attrGroupId}")
	public R info(@PathVariable("attrGroupId") Long attrGroupId) {
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
	public R save(@RequestBody AttrGroupEntity attrGroup) {
		attrGroupService.save(attrGroup);

		return R.ok();
	}

	/**
	 * 
	 * https://easydoc.xyz/s/78237135/ZUqEdvA4/qn7A2Fht
	 * 
	 * 请求参数 [{"attrId":1,"attrGroupId":2}] 响应数据 { "msg": "success", "code": 0 }
	 *
	 * 删除
	 */
	@PostMapping("/attr/relation/delete")
	public R deleteRelation(@RequestBody AttrGroupRelationVo[] attrGroupRelationVos) {
		attrService.deleteRelation(attrGroupRelationVos);

		return R.ok();
	}

	/**
	 * 修改
	 */
	@RequestMapping("/update")
	public R update(@RequestBody AttrGroupEntity attrGroup) {
		attrGroupService.updateById(attrGroup);

		return R.ok();
	}

	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	public R delete(@RequestBody Long[] attrGroupIds) {
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

		return R.ok();
	}

}
