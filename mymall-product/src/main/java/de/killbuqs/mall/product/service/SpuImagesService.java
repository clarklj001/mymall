package de.killbuqs.mall.product.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.service.IService;

import de.killbuqs.common.utils.PageUtils;
import de.killbuqs.mall.product.entity.SpuImagesEntity;

/**
 * spu图片
 *
 * @author jlong
 * @email jie.long@killbuqs.de
 * @date 2021-03-14 21:40:00
 */
public interface SpuImagesService extends IService<SpuImagesEntity> {

    PageUtils queryPage(Map<String, Object> params);

	void saveImages(Long id, List<String> images);
}

