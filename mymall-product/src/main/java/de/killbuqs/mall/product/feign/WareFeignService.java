package de.killbuqs.mall.product.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import de.killbuqs.common.utils.R;

@FeignClient("mymall-ware")
public interface WareFeignService {

	@PostMapping("/ware/waresku/hasstock")
	R getSkusHasStock(@RequestBody List<Long> skuIds);

}
