package de.killbuqs.mall.product.controller;

import java.util.Arrays;
import java.util.Map;

import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.killbuqs.common.utils.PageUtils;
import de.killbuqs.common.utils.R;
import de.killbuqs.mall.product.entity.AttrEntity;
import de.killbuqs.mall.product.service.AttrService;
import de.killbuqs.mall.product.vo.AttrRespVo;
import de.killbuqs.mall.product.vo.AttrVo;

/**
 * 商品属性
 *
 * @author jlong
 * @email jie.long@killbuqs.de
 * @date 2021-03-14 21:40:00
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {

	@Autowired
	private AttrService attrService;

	/**
	 * https://easydoc.xyz/s/78237135/ZUqEdvA4/Ld1Vfkcd
	 * 
	 * 需要将所属分类名称和所属分组加入 Attr对象，返回 AttrRespVo
	 * 
	 * 请求参数 { page: 1,//当前页码 limit: 10,//每页记录数 sidx: 'id',//排序字段 order:
	 * 'asc/desc',//排序方式 key: '华为'//检索关键字 } 分页数据
	 *
	 * 响应数据 { "msg": "success", "code": 0, "page": { "totalCount": 0, "pageSize":
	 * 10, "totalPage": 0, "currPage": 1, "list": [{ "attrId": 0, //属性id "attrName":
	 * "string", //属性名 "attrType": 0, //属性类型，0-销售属性，1-基本属性 "catelogName":
	 * "手机/数码/手机", //所属分类名字 "groupName": "主体", //所属分组名字 "enable": 0, //是否启用 "icon":
	 * "string", //图标 "searchType": 0,//是否需要检索[0-不需要，1-需要] "showDesc":
	 * 0,//是否展示在介绍上；0-否 1-是 "valueSelect": "string",//可选值列表[用逗号分隔] "valueType":
	 * 0//值类型[0-为单个值，1-可以选择多个值] }] } }
	 * 
	 */
	@GetMapping("/{attrType}/list/{catelogId}")
	public R baseAttrList(@RequestParam Map<String, Object> params, @PathVariable("catelogId") Long catelogId,
			@PathVariable("attrType") String type) {

		PageUtils page = attrService.queryBaseAttrPage(params, catelogId, type);
		return R.ok().put("page", page);
	}

	/**
	 * 
	 * 列表
	 */
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params) {
		PageUtils page = attrService.queryPage(params);

		return R.ok().put("page", page);
	}

	/**
	 * 
	 * https://easydoc.xyz/s/78237135/ZUqEdvA4/7C3tMIuF
	 * 
	 * 需要将分组id，分类完整路径， 分类id
	 * 
	 * { "msg": "success", "code": 0, "attr": { "attrId": 4, "attrName": "aad",
	 * "searchType": 1, "valueType": 1, "icon": "qq", "valueSelect": "v;q;w",
	 * "attrType": 1, "enable": 1, "showDesc": 1, "attrGroupId": 1, //分组id
	 * "catelogId": 225, //分类id "catelogPath": [2, 34, 225] //分类完整路径 } }
	 *
	 * 信息
	 */
	@RequestMapping("/info/{attrId}")
	public R info(@PathVariable("attrId") Long attrId) {
//		AttrEntity attr = attrService.getById(attrId);
		AttrRespVo respVo = attrService.getAttrInfo(attrId);

		return R.ok().put("attr", respVo);
	}

	/**
	 * https://easydoc.xyz/s/78237135/ZUqEdvA4/ZtYHCcI5
	 * 
	 * 请求参数 { "attrGroupId": 0, //属性分组id "attrName": "string",//属性名保存 "attrType": 0,
	 * //属性类型 "catelogId": 0, //分类idquestMapping("/save") "enable": 0, //是否可用 lic R
	 * save(@RequestBody AttrEntity attr){ "icon": "string", //图标
	 * attrService.save(attr); "searchType": 0, //是否检索 "showDesc": 0, //快速展示 return
	 * R.ok(); "valueSelect": "string", //可选值列表 "valueType": 0 //可选值模式 } 分页数据修改
	 *
	 * 响应数据questMapping("/update") {lic R update(@RequestBody AttrEntity attr){
	 * "msg": "success", attrService.updateById(attr); "code": 0 } return R.ok(); }
	 * 
	 * 保存
	 */
	@RequestMapping("/save")
	public R save(@RequestBody AttrVo attr) {
		attrService.saveAttr(attr);

		return R.ok();
	}

	/**
	 * 
	 * https://easydoc.xyz/s/78237135/ZUqEdvA4/10r1cuqn
	 * 
	 * 请求参数 { "attrId": 0, //属性id "attrGroupId": 0, //属性分组id "attrName":
	 * "string",//属性名 "attrType": 0, //属性类型 "catelogId": 0, //分类id "enable": 0,
	 * //是否可用 "icon": "string", //图标 "searchType": 0, //是否检索 "showDesc": 0, //快速展示
	 * "valueSelect": "string", //可选值列表 "valueType": 0 //可选值模式 } 分页数据
	 *
	 * 响应数据 { "msg": "success", "code": 0 }
	 * 
	 * 修改
	 */
	@RequestMapping("/update")
	public R update(@RequestBody AttrVo attr) {
		attrService.updateAttr(attr);

		return R.ok();
	}

	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	public R delete(@RequestBody Long[] attrIds) {
		attrService.removeByIds(Arrays.asList(attrIds));

		return R.ok();
	}

}
