package de.killbuqs.mymall.search.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import de.killbuqs.common.utils.R;

@FeignClient("mymall-product")
public interface ProductFeignService {

	@GetMapping("/product/attr/info/{attrId}")
	public R attrInfo(@PathVariable("attrId") Long attrId);
	
	@GetMapping("/product/brand/infos")
	public R brandsInfo(@RequestParam("brandIds") List<Long> brandIds);
}
