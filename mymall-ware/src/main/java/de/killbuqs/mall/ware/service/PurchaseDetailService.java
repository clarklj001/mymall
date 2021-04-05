package de.killbuqs.mall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import de.killbuqs.common.utils.PageUtils;
import de.killbuqs.mall.ware.entity.PurchaseDetailEntity;

import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author jlong
 * @email jie.long@killbuqs.de
 * @date 2021-03-14 23:48:08
 */
public interface PurchaseDetailService extends IService<PurchaseDetailEntity> {

    PageUtils queryPage(Map<String, Object> params);

	List<PurchaseDetailEntity> listDetailByPurchaseId(Long id);
}

