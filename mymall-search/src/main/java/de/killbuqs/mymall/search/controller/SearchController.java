package de.killbuqs.mymall.search.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import de.killbuqs.mymall.search.service.MallSearchService;
import de.killbuqs.mymall.search.vo.SearchParam;
import de.killbuqs.mymall.search.vo.SearchResult;

@Controller
public class SearchController {

	@Autowired
	private MallSearchService mallSearchServie;

	@GetMapping("list.html")
	public String listPage(SearchParam param,
			Model model, HttpServletRequest request) {
		String queryString = request.getQueryString();
		param.setQueryString(queryString);
		// 根据传递来的页面的查询参数去es中检索商品
		SearchResult result = mallSearchServie.search(param);
		model.addAttribute("result", result);
		return "list";
	}

}
