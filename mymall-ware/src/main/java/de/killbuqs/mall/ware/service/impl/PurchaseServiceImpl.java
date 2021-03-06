package de.killbuqs.mall.ware.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import de.killbuqs.common.constant.WareConstant;
import de.killbuqs.common.utils.PageUtils;
import de.killbuqs.common.utils.Query;
import de.killbuqs.mall.ware.dao.PurchaseDao;
import de.killbuqs.mall.ware.entity.PurchaseDetailEntity;
import de.killbuqs.mall.ware.entity.PurchaseEntity;
import de.killbuqs.mall.ware.service.PurchaseDetailService;
import de.killbuqs.mall.ware.service.PurchaseService;
import de.killbuqs.mall.ware.service.WareSkuService;
import de.killbuqs.mall.ware.vo.MergeVo;
import de.killbuqs.mall.ware.vo.PurchaseDoneVo;
import de.killbuqs.mall.ware.vo.PurchaseItemDoneVo;

@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

	@Autowired
	private PurchaseDetailService purchaseDetailService;
	
	@Autowired
	private WareSkuService wareSkuService;

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		IPage<PurchaseEntity> page = this.page(new Query<PurchaseEntity>().getPage(params),
				new QueryWrapper<PurchaseEntity>());

		return new PageUtils(page);
	}

	@Override
	public PageUtils queryPageUnreceive(Map<String, Object> params) {

		IPage<PurchaseEntity> page = this.page(new Query<PurchaseEntity>().getPage(params),
				new QueryWrapper<PurchaseEntity>().eq("status", 0).or().eq("status", 1));

		return new PageUtils(page);
	}

	@Transactional
	@Override
	public void mergePurchase(MergeVo mergeVo) {
		Long purchaseId = mergeVo.getPurchaseId();
		if (purchaseId == null) {
			PurchaseEntity purchaseEntity = new PurchaseEntity();
			purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.CREATED.getCode());
			purchaseEntity.setCreateTime(new Date());
			purchaseEntity.setUpdateTime(new Date());
			this.save(purchaseEntity);

			purchaseId = purchaseEntity.getId();
		}
		// ????????????????????????0???1???????????????
		else {
			PurchaseEntity purchaseEntity = this.getById(purchaseId);
			Integer status = purchaseEntity.getStatus();
			if (WareConstant.PurchaseStatusEnum.CREATED.getCode() != status
					&& WareConstant.PurchaseStatusEnum.ASSIGNED.getCode() != status) {
				return;
			}
		}

		List<Long> items = mergeVo.getItems();

		Long finalPurchaseId = purchaseId;

		List<PurchaseDetailEntity> collect = items.stream().map(item -> {
			PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
			purchaseDetailEntity.setId(item);
			purchaseDetailEntity.setPurchaseId(finalPurchaseId);
			purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode());
			return purchaseDetailEntity;
		}).collect(Collectors.toList());

		purchaseDetailService.updateBatchById(collect);

		PurchaseEntity purchaseEntity = new PurchaseEntity();
		purchaseEntity.setId(purchaseId);
		purchaseEntity.setUpdateTime(new Date());
		this.updateById(purchaseEntity);
	}

	/**
	 */
	@Override
	public void received(Long assigneeId, List<Long> ids) {

		// TODO: ??????????????????????????????????????????????????????assign????????????

		// ???????????????????????????????????????????????????
		List<PurchaseEntity> purchaseEntities = ids.stream().map(id -> {
			PurchaseEntity purchaseEntity = this.getById(id);
			return purchaseEntity;
		}).filter(item -> item.getStatus() == WareConstant.PurchaseStatusEnum.CREATED.getCode()
				|| item.getStatus() == WareConstant.PurchaseStatusEnum.ASSIGNED.getCode()).map(item -> {
					item.setStatus(WareConstant.PurchaseStatusEnum.RECEIVED.getCode());
					item.setUpdateTime(new Date());
					return item;
				}).collect(Collectors.toList());

		// ????????????????????????
		if (purchaseEntities.size() > 0) {
			this.updateBatchById(purchaseEntities);

			// ?????????????????????????????????????????????
			purchaseEntities.forEach(item -> {
				List<PurchaseDetailEntity> purchaseDetailEntities = purchaseDetailService
						.listDetailByPurchaseId(item.getId());
				List<PurchaseDetailEntity> collect = purchaseDetailEntities.stream().map(entity -> {
					PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
					purchaseDetailEntity.setId(entity.getId());
					purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.BUYING.getCode());
					return purchaseDetailEntity;
				}).collect(Collectors.toList());

				purchaseDetailService.updateBatchById(collect);
			});
		}
	}

	@Override
	public void done(Long assigneeId, PurchaseDoneVo doneVo) {
		// ????????????????????????
		Boolean flag = true; // ??????????????????????????????
		List<PurchaseItemDoneVo> items = doneVo.getItems();
		List<PurchaseDetailEntity> purchaseDetailEntities = new ArrayList<>();
		for (PurchaseItemDoneVo item : items) {
			if (item.getStatus() == WareConstant.PurchaseDetailStatusEnum.HASERROR.getCode()) {
				flag = false;
			} else {
				// ??????????????????????????????
				// ???????????????id ????????????id?????????????????????id
				PurchaseDetailEntity entity = purchaseDetailService.getById(item.getItemId());
				wareSkuService.addStock(item.getItemId(), entity.getWareId(), entity.getSkuNum());
			}
			// TODO: ???????????????reason?????? ????????????10??????????????????8?????????????????????????????????
			PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
			purchaseDetailEntity.setId(item.getItemId());
			purchaseDetailEntity.setStatus(item.getStatus());
			purchaseDetailEntities.add(purchaseDetailEntity);
		}
		purchaseDetailService.updateBatchById(purchaseDetailEntities);

		// ?????????????????????
		PurchaseEntity purchaseEntity = new PurchaseEntity();
		purchaseEntity.setId(doneVo.getId());
		purchaseEntity.setStatus(flag ? WareConstant.PurchaseStatusEnum.FINISHED.getCode()
				: WareConstant.PurchaseStatusEnum.HASERROR.getCode());
		purchaseEntity.setUpdateTime(new Date());
		this.updateById(purchaseEntity);
		
	
		
	}

}