package de.killbuqs.mall.product.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.service.IService;

import de.killbuqs.common.utils.PageUtils;
import de.killbuqs.mall.product.entity.ProductAttrValueEntity;

/**
 * spu属性值
 *
 * @author jlong
 * @email jie.long@killbuqs.de
 * @date 2021-03-14 21:40:00
 */
public interface ProductAttrValueService extends IService<ProductAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

	List<ProductAttrValueEntity> baseAttrListForSpu(Long spuId);

	void updateSpuAttr(Long spuId, List<ProductAttrValueEntity> entities);
}

