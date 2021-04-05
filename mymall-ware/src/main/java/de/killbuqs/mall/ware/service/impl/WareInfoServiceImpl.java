package de.killbuqs.mall.ware.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import de.killbuqs.common.utils.PageUtils;
import de.killbuqs.common.utils.Query;

import de.killbuqs.mall.ware.dao.WareInfoDao;
import de.killbuqs.mall.ware.entity.WareInfoEntity;
import de.killbuqs.mall.ware.service.WareInfoService;

@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {

		QueryWrapper<WareInfoEntity> queryWrapper = new QueryWrapper<WareInfoEntity>();

		String key = (String) params.get("key");
		if (!StringUtils.isEmpty(key)) {
			queryWrapper.eq("id", key).or().like("name", key).or().like("address", key).or().like("areacode", key);
		}

		IPage<WareInfoEntity> page = this.page(new Query<WareInfoEntity>().getPage(params), queryWrapper);

		return new PageUtils(page);
	}

}