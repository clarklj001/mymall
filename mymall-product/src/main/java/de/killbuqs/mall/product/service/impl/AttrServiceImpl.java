package de.killbuqs.mall.product.service.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import de.killbuqs.common.constant.ProductConstant;
import de.killbuqs.common.utils.PageUtils;
import de.killbuqs.common.utils.Query;
import de.killbuqs.mall.product.dao.AttrAttrgroupRelationDao;
import de.killbuqs.mall.product.dao.AttrDao;
import de.killbuqs.mall.product.dao.AttrGroupDao;
import de.killbuqs.mall.product.dao.CategoryDao;
import de.killbuqs.mall.product.entity.AttrAttrgroupRelationEntity;
import de.killbuqs.mall.product.entity.AttrEntity;
import de.killbuqs.mall.product.entity.AttrGroupEntity;
import de.killbuqs.mall.product.entity.CategoryEntity;
import de.killbuqs.mall.product.service.AttrService;
import de.killbuqs.mall.product.service.CategoryService;
import de.killbuqs.mall.product.vo.AttrGroupRelationVo;
import de.killbuqs.mall.product.vo.AttrRespVo;
import de.killbuqs.mall.product.vo.AttrVo;

@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

	@Autowired
	private AttrAttrgroupRelationDao relationDao;

	@Autowired
	private AttrGroupDao attrGroupDao;

	@Autowired
	private CategoryDao categoryDao;

	@Autowired
	private CategoryService categoryService;

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), new QueryWrapper<AttrEntity>());

		return new PageUtils(page);
	}

	@Transactional
	@Override
	public void saveAttr(AttrVo attr) {
		AttrEntity attrEntity = new AttrEntity();
		BeanUtils.copyProperties(attr, attrEntity);
		// 保存基本数据
		this.save(attrEntity);
		// 保存关联关系
		if (attr.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() && attr.getAttrGroupId() != null) {
			AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
			relationEntity.setAttrGroupId(attr.getAttrGroupId());
			relationEntity.setAttrId(attrEntity.getAttrId());
			relationDao.insert(relationEntity);
		}
	}

	@Override
	public PageUtils queryBaseAttrPage(Map<String, Object> params, Long categoryId, String type) {

		QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<AttrEntity>().eq("attr_type",
				"base".equalsIgnoreCase(type) ? ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()
						: ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode());

		if (categoryId != 0) {
			queryWrapper.eq("catelog_id", categoryId);
		}

		String key = (String) params.get("key");
		if (!StringUtils.isEmpty(key)) {
			queryWrapper.and((wrapper) -> {
				wrapper.eq("attr_id", key).or().like("attr_name", key);
			});
		}

		IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), queryWrapper);

		PageUtils pageUtils = new PageUtils(page);

		List<AttrEntity> records = page.getRecords();
		List<AttrRespVo> respVos = records.stream().map((attrEntity) -> {
			AttrRespVo attrRespVo = new AttrRespVo();
			BeanUtils.copyProperties(attrEntity, attrRespVo);

			// 设置分类和分组的名字
			if ("base".equalsIgnoreCase(type)) {
				AttrAttrgroupRelationEntity relationEntity = relationDao.selectOne(
						new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));
				if (relationEntity != null && relationEntity.getAttrGroupId() != null) {
					AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(relationEntity.getAttrGroupId());
					attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
				}
			}
			CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatelogId());
			if (categoryEntity != null) {
				attrRespVo.setCatelogName(categoryEntity.getName());
			}

			return attrRespVo;
		}).collect(Collectors.toList());

		pageUtils.setList(respVos);
		return pageUtils;
	}

	@Override
	public AttrRespVo getAttrInfo(Long attrId) {
		AttrEntity attrEntity = this.getById(attrId);

		AttrRespVo attrRespVo = new AttrRespVo();
		BeanUtils.copyProperties(attrEntity, attrRespVo);

		if (attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
			// 设置分组信息
			AttrAttrgroupRelationEntity attrgroupRelationEntity = relationDao
					.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));
			if (attrgroupRelationEntity != null) {
				attrRespVo.setAttrGroupId(attrgroupRelationEntity.getAttrGroupId());
				AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrgroupRelationEntity.getAttrGroupId());
				if (attrGroupEntity != null) {
					attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
				}
			}
		}

		// 设置分类信息
		Long catelogId = attrEntity.getCatelogId();
		attrRespVo.setCatelogId(catelogId);
		Long[] catelogPath = categoryService.findCatelogPath(catelogId);
		attrRespVo.setCatelogPath(catelogPath);
		CategoryEntity categoryEntity = categoryService.getById(catelogId);
		if (categoryEntity != null) {
			attrRespVo.setCatelogName(categoryEntity.getName());
		}

		return attrRespVo;
	}

	@Transactional
	@Override
	public void updateAttr(AttrVo attr) {
		AttrEntity attrEntity = new AttrEntity();
		BeanUtils.copyProperties(attr, attrEntity);
		this.baseMapper.updateById(attrEntity);

		if (attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
			AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
			relationEntity.setAttrGroupId(attr.getAttrGroupId());
			relationEntity.setAttrId(attr.getAttrId());

			Integer count = relationDao
					.selectCount(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId()));
			if (count > 0) {
				relationDao.update(relationEntity,
						new UpdateWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId()));
			} else {
				relationDao.insert(relationEntity);
			}
		}
	}

	/**
	 * 根据分组id查找关联的所有基本属性
	 */
	@Override
	public List<AttrEntity> getRelationAttr(Long attrgroupId) {

		List<AttrAttrgroupRelationEntity> entities = relationDao
				.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrgroupId));

		List<Long> attrIds = entities.stream().map((attr) -> {
			return attr.getAttrId();
		}).collect(Collectors.toList());

		if (attrIds == null || attrIds.size() == 0) {
			return null;
		}
		Collection<AttrEntity> attrEntities = this.listByIds(attrIds);

		return (List<AttrEntity>) attrEntities;
	}

	@Override
	public void deleteRelation(AttrGroupRelationVo[] attrGroupRelationVos) {

		List<AttrAttrgroupRelationEntity> entities = Arrays.asList(attrGroupRelationVos).stream().map((item) -> {
			AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
			BeanUtils.copyProperties(item, relationEntity);
			return relationEntity;
		}).collect(Collectors.toList());

		relationDao.deleteBatchRelation(entities);

	}

	/**
	 * 获取当前分组没有关联的所有属性
	 */
	@Override
	public PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId) {
		// 1.当前分组只能关联自己所属的分类里面的所有属性
		AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrgroupId);
		Long catelogId = attrGroupEntity.getCatelogId();

		// 2.当前分组只能关联别的分组没有引用的属性
		// 2.1 找到当前分类下的其他分组
		List<AttrGroupEntity> groups = attrGroupDao
				.selectList(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
		List<Long> groupIds = groups.stream().map(item -> item.getAttrGroupId()).collect(Collectors.toList());

		// 2.2 找到这些分组关联的属性
		List<AttrAttrgroupRelationEntity> relationEntities = relationDao
				.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().in("attr_group_id", groupIds));
		List<Long> attrIds = relationEntities.stream().map(item -> item.getAttrId()).collect(Collectors.toList());

		// 2.3 从当前分类的所有属性中移除这些属性
		QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<AttrEntity>().eq("catelog_id", catelogId)
				// 属性分组只关联基本类型
				.eq("attr_type", ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());
		if (attrIds != null && attrIds.size() > 0) {
			queryWrapper.notIn("attr_id", attrIds);
		}

		String key = (String) params.get("key");
		if (!StringUtils.isEmpty(key)) {
			queryWrapper.and(wrapper -> {
				wrapper.eq("attr_id", key).or().like("attr_name", key);
			});
		}
		IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), queryWrapper);

		return new PageUtils(page);
	}

	@Override
	public List<Long> selectSearchAttrIds(List<Long> attrIds) {
		// select attr_id from pms_attr where attr_id in () and search_type = 1
		List<Long> attrs = this.baseMapper.selectSearchAttrIds(attrIds);
		return attrs;
	}
}