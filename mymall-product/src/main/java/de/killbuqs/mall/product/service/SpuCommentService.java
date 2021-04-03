package de.killbuqs.mall.product.service;

import java.util.Map;

import com.baomidou.mybatisplus.extension.service.IService;

import de.killbuqs.common.utils.PageUtils;
import de.killbuqs.mall.product.entity.SpuCommentEntity;

/**
 * 商品评价
 *
 * @author jlong
 * @email jie.long@killbuqs.de
 * @date 2021-03-14 21:40:00
 */
public interface SpuCommentService extends IService<SpuCommentEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

