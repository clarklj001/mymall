package de.killbuqs.mall.product.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import de.killbuqs.common.utils.PageUtils;
import de.killbuqs.common.utils.Query;
import de.killbuqs.mall.product.dao.CategoryDao;
import de.killbuqs.mall.product.entity.CategoryEntity;
import de.killbuqs.mall.product.service.CategoryBrandRelationService;
import de.killbuqs.mall.product.service.CategoryService;
import de.killbuqs.mall.product.vo.ui.Catalog2Vo;
import de.killbuqs.mall.product.vo.ui.Catalog2Vo.Catalog3Vo;

@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

	@Autowired
	private CategoryBrandRelationService categoryBrandRelationService;

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		IPage<CategoryEntity> page = this.page(new Query<CategoryEntity>().getPage(params),
				new QueryWrapper<CategoryEntity>());

		return new PageUtils(page);
	}

	@Override
	public List<CategoryEntity> listWithTree() {

		// 查出所有分类，组装成父子的父型结构
		List<CategoryEntity> entities = baseMapper.selectList(null);

		List<CategoryEntity> level3Menus = entities.stream().filter(categoryEntity -> categoryEntity.getCatLevel() == 3)
				.collect(Collectors.toList());

		List<CategoryEntity> level2Menus = addChildrenForLevel(entities, 2, level3Menus);

		List<CategoryEntity> level1Menus = addChildrenForLevel(entities, 1, level2Menus);

		return level1Menus;
	}

	private List<CategoryEntity> addChildrenForLevel(List<CategoryEntity> entities, int level,
			List<CategoryEntity> level3Menus) {
		return entities.stream() //
				.filter(categoryEntity -> categoryEntity.getCatLevel() == level) //
				.map(menu -> {
					menu.setChildren(getChildren(menu, level3Menus));
					return menu;
				}) //
				.sorted((menu1, menu2) -> (menu1.getSort() == null ? 0 : menu1.getSort())
						- (menu2.getSort() == null ? 0 : menu2.getSort())) //
				.collect(Collectors.toList());
	}

	@Override
	public Long[] findCatelogPath(Long catelogId) {
		List<Long> paths = new ArrayList<>();

		CategoryEntity byId = this.getById(catelogId);
		paths.add(catelogId);

		for (int i = 0; i < 3; i++) {
			Long parentCid = byId.getParentCid();
			if (parentCid != 0) {
				byId = this.getById(parentCid);
				paths.add(parentCid);
			} else {
				break;
			}
		}
		Collections.reverse(paths);
		return (Long[]) paths.toArray(new Long[paths.size()]);
	}

	private List<CategoryEntity> getChildren(CategoryEntity currentEntity, List<CategoryEntity> all) {
		return all.stream() //
				.filter(categoryEntity -> currentEntity.getCatId() == categoryEntity.getParentCid()) //
				.sorted((menu1, menu2) -> (menu1.getSort() == null ? 0 : menu1.getSort())
						- (menu2.getSort() == null ? 0 : menu2.getSort())) //
				.collect(Collectors.toList());

	}

	@Override
	public void removeMenuByIds(List<Long> asList) {
		// TODO 检查当前删除的菜单是否被别的地方引用
		baseMapper.deleteBatchIds(asList);

	}

	@Transactional
	@Override
	public void updateCascade(CategoryEntity category) {
		this.updateById(category);
		categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
	}

	@Override
	public List<CategoryEntity> getLevel1Categories() {
		return this.baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
	}

	@Override
	public Map<String, List<Catalog2Vo>> getCatalogJson() {

		// 查出所有1级分类
		List<CategoryEntity> level1Categories = getLevel1Categories();

		Map<String, List<Catalog2Vo>> parentCid = level1Categories.stream()
				.collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
					// 查询每一个一级分类的二级分类
					List<CategoryEntity> categoryEntities = baseMapper
							.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", v.getCatId()));
					List<Catalog2Vo> catalog2Vos = null;
					if (categoryEntities != null) {
						catalog2Vos = categoryEntities.stream().map(level2Catalog -> {
							Catalog2Vo catalog2Vo = new Catalog2Vo(v.getCatId().toString(), null,
									level2Catalog.getCatId().toString(), level2Catalog.getName());
							// 找当前二级分类的三级分类
							List<CategoryEntity> level3Catalogs = baseMapper
									.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", level2Catalog.getCatId()));
							if (level3Catalogs != null) {
								List<Catalog3Vo> level3CatalogVos = level3Catalogs.stream().map(level3Catalog -> {
									Catalog3Vo catalog3Vo = new Catalog2Vo.Catalog3Vo(level2Catalog.getCatId().toString(),
											level3Catalog.getCatId().toString(), level3Catalog.getName());
									return catalog3Vo;
								}).collect(Collectors.toList());
								catalog2Vo.setCatalog3List(level3CatalogVos);
							}

							return catalog2Vo;
						}).collect(Collectors.toList());
					}

					return catalog2Vos;
				}));

		return parentCid;
	}

}