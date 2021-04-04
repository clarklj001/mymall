package de.killbuqs.mall.product.service.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import de.killbuqs.common.utils.PageUtils;
import de.killbuqs.common.utils.Query;
import de.killbuqs.mall.product.dao.AttrGroupDao;
import de.killbuqs.mall.product.entity.AttrEntity;
import de.killbuqs.mall.product.entity.AttrGroupEntity;
import de.killbuqs.mall.product.service.AttrGroupService;
import de.killbuqs.mall.product.service.AttrService;
import de.killbuqs.mall.product.vo.AttrGroupWithAttrsVo;
import de.killbuqs.mall.product.vo.AttrVo;

@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

	@Autowired
	private AttrGroupDao attrGroupDao;

	@Autowired
	private AttrService attrService;

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params),
				new QueryWrapper<AttrGroupEntity>());

		return new PageUtils(page);
	}

	@Override
	public PageUtils queryPage(Map<String, Object> params, Long catelogId) {

		String key = (String) params.get("key");
		// select * from pms_attr_group where catelog_id = ? and (attr_group_id=key or
		// attr_group_name like '%key%')
		QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<AttrGroupEntity>();
		if (!StringUtils.isEmpty(key)) {
			wrapper.and((obj) -> {
				obj.eq("attr_group_id", key).or().like("attr_group_name", key);
			});
		}
		if (catelogId != 0) {
			wrapper.eq("catelog_id", catelogId);
		}

		IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params), wrapper);
		return new PageUtils(page);

	}

	/**
	 * 根据分类id查出所有的分组信息和这些分组里面的所有属性
	 */
	@Override
	public List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCatelogId(Long catelogId) {

		// 查询分组信息
		List<AttrGroupEntity> attrGroupEntities = this
				.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));

		List<AttrGroupWithAttrsVo> collect = attrGroupEntities.stream().map(group -> {
			AttrGroupWithAttrsVo attrGroupWithAttrsVo = new AttrGroupWithAttrsVo();
			BeanUtils.copyProperties(group, attrGroupWithAttrsVo);

			List<AttrEntity> attrs = attrService.getRelationAttr(group.getAttrGroupId());
			List<AttrVo> attrVos = attrs.stream().map(attr -> {
				AttrVo attrVo = new AttrVo();
				BeanUtils.copyProperties(attr, attrVo);
				return attrVo;
			}).collect(Collectors.toList());
			attrGroupWithAttrsVo.setAttrs(attrVos);
			return attrGroupWithAttrsVo;
		}).collect(Collectors.toList());
		return collect;
	}

}