package de.killbuqs.mall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import de.killbuqs.common.utils.PageUtils;
import de.killbuqs.mall.ware.entity.PurchaseEntity;
import de.killbuqs.mall.ware.vo.MergeVo;
import de.killbuqs.mall.ware.vo.PurchaseDoneVo;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author jlong
 * @email jie.long@killbuqs.de
 * @date 2021-03-14 23:48:08
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

	PageUtils queryPageUnreceive(Map<String, Object> params);

	void mergePurchase(MergeVo mergeVo);

	void received(Long assigneeId, List<Long> ids);

	void done(Long assigneeId, PurchaseDoneVo doneVo);
}

