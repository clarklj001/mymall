package de.killbuqs.mall.product.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.service.IService;

import de.killbuqs.common.utils.PageUtils;
import de.killbuqs.mall.product.entity.AttrAttrgroupRelationEntity;
import de.killbuqs.mall.product.vo.AttrGroupRelationVo;

/**
 * 属性&属性分组关联
 *
 * @author jlong
 * @email jie.long@killbuqs.de
 * @date 2021-03-14 21:40:00
 */
public interface AttrAttrgroupRelationService extends IService<AttrAttrgroupRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

	void saveBatch(List<AttrGroupRelationVo> vos);
}

