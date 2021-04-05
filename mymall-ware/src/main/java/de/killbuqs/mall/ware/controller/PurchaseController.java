package de.killbuqs.mall.ware.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.killbuqs.common.utils.PageUtils;
import de.killbuqs.common.utils.R;
import de.killbuqs.mall.ware.entity.PurchaseEntity;
import de.killbuqs.mall.ware.service.PurchaseService;
import de.killbuqs.mall.ware.vo.MergeVo;
import de.killbuqs.mall.ware.vo.PurchaseDoneVo;

/**
 * 采购信息
 *
 * @author jlong
 * @email jie.long@killbuqs.de
 * @date 2021-03-14 23:48:08
 */
@RestController
@RequestMapping("ware/purchase")
public class PurchaseController {
	@Autowired
	private PurchaseService purchaseService;
	
	/**
	 * https://easydoc.xyz/s/78237135/ZUqEdvA4/cTQHGXbK
	 * 
	 *{
	 *   id: 123,//采购单id
	 *   items: [{itemId:1,status:4,reason:""}]//完成/失败的需求详情
	 *}
	 * 
	 * 完成采购单 (通过外部系统)
	 */
	@PostMapping("/done/{assigneeId}")
	// @RequiresPermissions("ware:purchase:list")
	public R finish(@RequestBody PurchaseDoneVo doneVo,
			@PathVariable(value = "assigneeId", required = false) Long assigneeId) {
		
		purchaseService.done(assigneeId, doneVo);
		
		return R.ok();
	}

	/**
	 * https://easydoc.xyz/s/78237135/ZUqEdvA4/vXMBBgw1
	 * 
	 * 请求参数 { [1,2,3,4]//采购单id }
	 * 
	 * 采购人员领取采购单 (通过外部系统)
	 */
	@PostMapping("/received/{assigneeId}")
	// @RequiresPermissions("ware:purchase:list")
	public R received(@RequestBody List<Long> ids,
			@PathVariable(value = "assigneeId", required = false) Long assigneeId) {

		purchaseService.received(assigneeId, ids);

		return R.ok();
	}

	/**
	 * https://easydoc.xyz/s/78237135/ZUqEdvA4/cUlv9QvK
	 * 
	 * 请求参数 { purchaseId: 1, //整单id items:[1,2,3,4] //合并项集合 }
	 * 
	 * 合并采购需求到一个采购单，当没有选定采购单，将创建一个采购单并添加采购需求
	 */
	@PostMapping("/merge")
	// @RequiresPermissions("ware:purchase:list")
	public R merge(@RequestBody MergeVo mergeVo) {

		purchaseService.mergePurchase(mergeVo);

		return R.ok();
	}

	/**
	 * https://easydoc.xyz/s/78237135/ZUqEdvA4/hI12DNrH
	 * 
	 * 查询没有领取的采购单
	 */
	@RequestMapping("/unreceive/list")
	// @RequiresPermissions("ware:purchase:list")
	public R unreceiveList(@RequestParam Map<String, Object> params) {
		PageUtils page = purchaseService.queryPageUnreceive(params);

		return R.ok().put("page", page);
	}

	/**
	 * 列表
	 */
	@RequestMapping("/list")
	// @RequiresPermissions("ware:purchase:list")
	public R list(@RequestParam Map<String, Object> params) {
		PageUtils page = purchaseService.queryPage(params);

		return R.ok().put("page", page);
	}

	/**
	 * 信息
	 */
	@RequestMapping("/info/{id}")
	// @RequiresPermissions("ware:purchase:info")
	public R info(@PathVariable("id") Long id) {
		PurchaseEntity purchase = purchaseService.getById(id);

		return R.ok().put("purchase", purchase);
	}

	/**
	 * 保存
	 */
	@RequestMapping("/save")
	// @RequiresPermissions("ware:purchase:save")
	public R save(@RequestBody PurchaseEntity purchase) {
		purchase.setCreateTime(new Date());
		purchase.setUpdateTime(new Date());
		purchaseService.save(purchase);

		return R.ok();
	}

	/**
	 * 修改
	 */
	@RequestMapping("/update")
	// @RequiresPermissions("ware:purchase:update")
	public R update(@RequestBody PurchaseEntity purchase) {
		purchaseService.updateById(purchase);

		return R.ok();
	}

	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	// @RequiresPermissions("ware:purchase:delete")
	public R delete(@RequestBody Long[] ids) {
		purchaseService.removeByIds(Arrays.asList(ids));

		return R.ok();
	}

}
