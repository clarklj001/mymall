package de.killbuqs.mall.product.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import de.killbuqs.common.utils.PageUtils;
import de.killbuqs.common.utils.Query;
import de.killbuqs.mall.product.dao.BrandDao;
import de.killbuqs.mall.product.dao.CategoryBrandRelationDao;
import de.killbuqs.mall.product.dao.CategoryDao;
import de.killbuqs.mall.product.entity.BrandEntity;
import de.killbuqs.mall.product.entity.CategoryBrandRelationEntity;
import de.killbuqs.mall.product.entity.CategoryEntity;
import de.killbuqs.mall.product.service.CategoryBrandRelationService;

@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity>
		implements CategoryBrandRelationService {

	@Autowired
	private BrandDao brandDao;

	@Autowired
	private CategoryDao categoryDao;

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		IPage<CategoryBrandRelationEntity> page = this.page(new Query<CategoryBrandRelationEntity>().getPage(params),
				new QueryWrapper<CategoryBrandRelationEntity>());

		return new PageUtils(page);
	}

	@Override
	public void saveDetail(CategoryBrandRelationEntity categoryBrandRelation) {
		Long brandId = categoryBrandRelation.getBrandId();
		Long catelogId = categoryBrandRelation.getCatelogId();

		BrandEntity brandEntity = brandDao.selectById(brandId);
		CategoryEntity categoryEntity = categoryDao.selectById(catelogId);

		categoryBrandRelation.setBrandName(brandEntity.getName());
		categoryBrandRelation.setCatelogName(categoryEntity.getName());

		this.save(categoryBrandRelation);

	}

	@Override
	public void updateBrand(Long brandId, String name) {
		CategoryBrandRelationEntity relationEntity = new CategoryBrandRelationEntity();
		relationEntity.setBrandId(brandId);
		relationEntity.setBrandName(name);
		this.update(relationEntity, new UpdateWrapper<CategoryBrandRelationEntity>().eq("brand_id", brandId));
	}

	@Override
	public void updateCategory(Long catId, String name) {
		this.baseMapper.updateCategory(catId, name);
	}

}