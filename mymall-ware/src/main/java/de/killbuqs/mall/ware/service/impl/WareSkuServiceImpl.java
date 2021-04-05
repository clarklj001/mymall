package de.killbuqs.mall.ware.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import de.killbuqs.common.utils.PageUtils;
import de.killbuqs.common.utils.Query;
import de.killbuqs.common.utils.R;
import de.killbuqs.mall.ware.dao.WareSkuDao;
import de.killbuqs.mall.ware.entity.WareSkuEntity;
import de.killbuqs.mall.ware.feign.ProductFeignService;
import de.killbuqs.mall.ware.service.WareSkuService;

@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

	@Autowired
	private WareSkuDao wareSkuDao;

	@Autowired
	private ProductFeignService productFeignService;

	@Override
	public PageUtils queryPage(Map<String, Object> params) {

		QueryWrapper<WareSkuEntity> queryWrapper = new QueryWrapper<WareSkuEntity>();

		String skuId = (String) params.get("skuId");
		if (!StringUtils.isEmpty(skuId)) {
			queryWrapper.eq("sku_id", skuId);
		}

		String wareId = (String) params.get("wareId");
		if (!StringUtils.isEmpty(wareId)) {
			queryWrapper.eq("ware_id", wareId);
		}

		IPage<WareSkuEntity> page = this.page(new Query<WareSkuEntity>().getPage(params), queryWrapper);

		return new PageUtils(page);
	}

	@Transactional
	@Override
	public void addStock(Long skuId, Long wareId, Integer skuNum) {
		// 判断如果还没有这个库存，就新增记录，有的话就更新
		List<WareSkuEntity> wareSkuList = wareSkuDao
				.selectList(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));

		if (wareSkuList == null || wareSkuList.size() == 0) {
			WareSkuEntity entity = new WareSkuEntity();
			entity.setSkuId(skuId);
			entity.setStock(skuNum);
			entity.setWareId(wareId);
			entity.setStockLocked(0);
			// 远程查询sku的名字，如果失败，整个事务不需要回滚
			// TODO 另外的办法 让异常出现以后不回滚
			try {
				R info = productFeignService.info(skuId);
				if (info.getCode() == 0) {
					Map<String, Object> data = (Map<String, Object>) info.get("skuInfo");
					entity.setSkuName((String) data.get("skuName"));
				}
			} catch (Exception e) {
				// Do not need to handle exception
			}
			wareSkuDao.insert(entity);
		} else {
			wareSkuDao.addStock(skuId, wareId, skuNum);

		}

	}

}