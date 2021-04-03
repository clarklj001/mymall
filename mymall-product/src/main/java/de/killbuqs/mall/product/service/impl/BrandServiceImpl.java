package de.killbuqs.mall.product.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import de.killbuqs.common.utils.PageUtils;
import de.killbuqs.common.utils.Query;
import de.killbuqs.mall.product.dao.BrandDao;
import de.killbuqs.mall.product.entity.BrandEntity;
import de.killbuqs.mall.product.service.BrandService;

@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {

		String key = (String) params.get("key");

		QueryWrapper<BrandEntity> queryWrapper = new QueryWrapper<BrandEntity>();
		if (!StringUtils.isEmpty(key)) {
			queryWrapper.eq("brand_id", key).or().like("name", key);
		}

		IPage<BrandEntity> page = this.page(new Query<BrandEntity>().getPage(params), queryWrapper);

		return new PageUtils(page);
	}

}