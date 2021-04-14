package de.killbuqs.mall.product.web;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import de.killbuqs.mall.product.entity.CategoryEntity;
import de.killbuqs.mall.product.service.CategoryService;
import de.killbuqs.mall.product.vo.ui.Catalog2Vo;

@Controller
public class IndexController {
	
	@Autowired
	private CategoryService categoryService;

	@GetMapping({ "/", "/index.html" })
	public String indexPage(Model model) {
		
		// 查出所有的1级分类
		List<CategoryEntity> categoryEntities = categoryService.getLevel1Categories();
		
		model.addAttribute("categories", categoryEntities);
		return "index";
	}
	
	@GetMapping("/index/catalog.json")
	@ResponseBody
	public Map<String, List<Catalog2Vo>> getCatalogJson() {
		
		Map<String, List<Catalog2Vo>> catalogJson = categoryService.getCatalogJson();
		
		return catalogJson;
	}
	
}
