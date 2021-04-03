package de.killbuqs.mall.product.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.service.IService;

import de.killbuqs.common.utils.PageUtils;
import de.killbuqs.mall.product.entity.AttrEntity;
import de.killbuqs.mall.product.vo.AttrGroupRelationVo;
import de.killbuqs.mall.product.vo.AttrRespVo;
import de.killbuqs.mall.product.vo.AttrVo;

/**
 * 商品属性
 *
 * @author jlong
 * @email jie.long@killbuqs.de
 * @date 2021-03-14 21:40:00
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

	void saveAttr(AttrVo attr);

	PageUtils queryBaseAttrPage(Map<String, Object> params, Long categoryId, String type);

	AttrRespVo getAttrInfo(Long attrId);

	void updateAttr(AttrVo attr);

	List<AttrEntity> getRelationAttr(Long attrgroupId);

	void deleteRelation(AttrGroupRelationVo[] attrGroupRelationVos);

	PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId);
}

