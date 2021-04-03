package de.killbuqs.mall.product.service;

import java.util.Map;

import com.baomidou.mybatisplus.extension.service.IService;

import de.killbuqs.common.utils.PageUtils;
import de.killbuqs.mall.product.entity.SpuInfoEntity;

/**
 * spu信息
 *
 * @author jlong
 * @email jie.long@killbuqs.de
 * @date 2021-03-14 21:40:00
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

