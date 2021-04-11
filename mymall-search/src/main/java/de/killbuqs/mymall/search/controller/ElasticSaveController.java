package de.killbuqs.mymall.search.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.killbuqs.common.exception.BizCodeEnum;
import de.killbuqs.common.to.es.SkuEsModel;
import de.killbuqs.common.utils.R;
import de.killbuqs.mymall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/search/save")
@RestController
public class ElasticSaveController {

	@Autowired
	private ProductSaveService productSaveService;

	@PostMapping("/product")
	public R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels) {
		boolean success = false;
		try {
			success = productSaveService.productStatusUp(skuEsModels);
		} catch (IOException e) {
			log.error("ElasticSaveController商品上架错误: {}", e);
			return R.error(BizCodeEnum.PRODUCT_UP_EXCEPTION.getCode(), BizCodeEnum.PRODUCT_UP_EXCEPTION.getMsg());
		}

		if (success) {
			return R.ok();
		} else {
			return R.error(BizCodeEnum.PRODUCT_UP_EXCEPTION.getCode(), BizCodeEnum.PRODUCT_UP_EXCEPTION.getMsg());
		}
	}
}
