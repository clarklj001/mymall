package de.killbuqs.mall.product.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import de.killbuqs.common.utils.PageUtils;
import de.killbuqs.common.utils.Query;
import de.killbuqs.mall.product.dao.ProductAttrValueDao;
import de.killbuqs.mall.product.entity.ProductAttrValueEntity;
import de.killbuqs.mall.product.service.ProductAttrValueService;

@Service("productAttrValueService")
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueDao, ProductAttrValueEntity>
		implements ProductAttrValueService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		IPage<ProductAttrValueEntity> page = this.page(new Query<ProductAttrValueEntity>().getPage(params),
				new QueryWrapper<ProductAttrValueEntity>());

		return new PageUtils(page);
	}

	@Override
	public List<ProductAttrValueEntity> baseAttrListForSpu(Long spuId) {
		List<ProductAttrValueEntity> entities = this.baseMapper
				.selectList(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId));
		return entities;
	}

	@Transactional
	@Override
	public void updateSpuAttr(Long spuId, List<ProductAttrValueEntity> entities) {
		// 删除掉数据库中spuId对应的旧属性
		this.baseMapper.delete(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId));
		
		// 添加新属性
		List<ProductAttrValueEntity> collect = entities.stream().map( entity -> {
			entity.setSpuId(spuId);
			return entity;
		}).collect(Collectors.toList());
		this.saveBatch(collect);
	}

}