package de.killbuqs.mall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;

import de.killbuqs.common.to.SkuHasStockVo;
import de.killbuqs.common.utils.PageUtils;
import de.killbuqs.mall.ware.entity.WareSkuEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author jlong
 * @email jie.long@killbuqs.de
 * @date 2021-03-14 23:48:08
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

	void addStock(Long itemId, Long wareId, Integer skuNum);

	List<SkuHasStockVo> getSkusHasStock(List<Long> skuIds);
}

