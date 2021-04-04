package de.killbuqs.mall.product.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import de.killbuqs.common.to.SkuReductionTo;
import de.killbuqs.common.to.SpuBoundTo;
import de.killbuqs.common.utils.R;

@FeignClient("mymall-coupon")
public interface CouponFeignService {

	@PostMapping("/coupon/spubounds/save")
	R saveSpuBounds(@RequestBody SpuBoundTo spuBoundTo);

	@PostMapping("/coupon/skufullreduction/saveinfo")
	R saveSkuReduction(@RequestBody SkuReductionTo skuReductionTo);

}
