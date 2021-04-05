package de.killbuqs.mall.ware.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import de.killbuqs.common.utils.R;

@FeignClient("mymall-product")
public interface ProductFeignService {
	
    @RequestMapping("/product/skuinfo/info/{skuId}")
    public R info(@PathVariable("skuId") Long skuId);

}
