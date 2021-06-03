package de.killbuqs.mymall.search.vo;

import java.util.ArrayList;
import java.util.List;

import de.killbuqs.common.to.es.SkuEsModel;
import lombok.Data;

/**
 * @author jlong
 *
 */
@Data
public class SearchResult {

	// 查询到的所有商品信息
	private List<SkuEsModel> products;

	/**
	 * 当前页码
	 */
	private Integer pageNum;

	/**
	 * 总记录数
	 */
	private Long total;

	/**
	 * 总页数
	 */
	private Integer totalPages;
	
	private List<Integer> pageNavs;

	/**
	 * 基于当前查询所有涉及到的品牌
	 */
	private List<BrandVo> brands;
	
	
	/**
	 * 基于当前查询所有涉及到的分类
	 */
	private List<CatalogVo> catalogs;
	
	/**
	 * 基于当前查询所有涉及到的属性
	 */
	private List<AttrVo> attrs;
	
	
	/**
	 * 面包屑导航
	 */
	private List<NavVo> navs = new ArrayList<>();
	
	private List<Long> attrIds = new ArrayList<>();
	
	@Data
	public static class NavVo {
		private String navName;
		private String navValue;
		private String link;
	}

	@Data
	public static class BrandVo {
		private Long brandId;
		private String brandName;
		private String brandImg;
	}
	
	@Data
	public static class CatalogVo {
		private Long catalogId;
		private String catalogName;
	}
	
	@Data
	public static class AttrVo {
		private Long attrId;
		private String attrName;
		private List<String> attrValue;
	}
}
