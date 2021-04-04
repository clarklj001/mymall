package de.killbuqs.mall.coupon.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import de.killbuqs.common.to.MemberPrice;
import de.killbuqs.common.to.SkuReductionTo;
import de.killbuqs.common.utils.PageUtils;
import de.killbuqs.common.utils.Query;

import de.killbuqs.mall.coupon.dao.SkuFullReductionDao;
import de.killbuqs.mall.coupon.entity.MemberPriceEntity;
import de.killbuqs.mall.coupon.entity.SkuFullReductionEntity;
import de.killbuqs.mall.coupon.entity.SkuLadderEntity;
import de.killbuqs.mall.coupon.service.MemberPriceService;
import de.killbuqs.mall.coupon.service.SkuFullReductionService;
import de.killbuqs.mall.coupon.service.SkuLadderService;

@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity>
		implements SkuFullReductionService {

	@Autowired
	private SkuLadderService skuLadderService;

	@Autowired
	private MemberPriceService memberPriceService;

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		IPage<SkuFullReductionEntity> page = this.page(new Query<SkuFullReductionEntity>().getPage(params),
				new QueryWrapper<SkuFullReductionEntity>());

		return new PageUtils(page);
	}

	@Override
	public void saveSkuReduction(SkuReductionTo skuReductionTo) {

		// 1. 保存sku的优惠，满减等信息 sms_sku_ladder,
		SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
		BeanUtils.copyProperties(skuReductionTo, skuLadderEntity);
		if (skuLadderEntity.getFullCount() > 0) {
			skuLadderService.save(skuLadderEntity);
		}

		// sms_sku_full_reduction
		SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
		BeanUtils.copyProperties(skuReductionTo, skuFullReductionEntity);
		if (skuFullReductionEntity.getFullPrice().compareTo(new BigDecimal(0)) == 1) {
			this.save(skuFullReductionEntity);
		}

		// sms_member_price
		List<MemberPrice> memberPrice = skuReductionTo.getMemberPrice();
		List<MemberPriceEntity> collect = memberPrice.stream().map(item -> {
			MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
			memberPriceEntity.setSkuId(skuReductionTo.getSkuId());
			memberPriceEntity.setMemberLevelId(item.getId());
			memberPriceEntity.setMemberLevelName(item.getName());
			memberPriceEntity.setMemberPrice(item.getPrice());
			memberPriceEntity.setAddOther(1);
			return memberPriceEntity;
		})
				// 会员价大于0的时候，数据才存入数据表
				.filter(item -> item.getMemberPrice().compareTo(new BigDecimal(0)) == 1).collect(Collectors.toList());
		memberPriceService.saveBatch(collect);
	}

}