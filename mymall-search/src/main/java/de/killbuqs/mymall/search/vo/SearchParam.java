package de.killbuqs.mymall.search.vo;

import java.util.List;

import lombok.Data;

/**
 * 
 * /list.html?catalog3I=225&keyword="oppo&sort=saleCount_desc&hasStock=1&skuPrice=400_1900
 * 		&brandId=1&attrs=1_3G&attrs=1_3G:4G:5G&attrs=2_骁龙&attrs=4_高清屏
 * 
 * @author jlong
 *
 */
@Data
public class SearchParam {
	
	private String keyword; // keyword: 全文检索 skuTitle
	
	
	
	// 
	
	// 聚合 attrs
	
	private Long catalog3Id; //三级分类id
	/**
	 * 排序 saleCount, hotScore, skuPrice
	 * sort=saleCount_desc/asc
	 * sort=skuPrice_desc/asc
	 * sort=hotScore_desc/asc
	 */
	private String sort;
	
	// 过滤 hasStock, skuPrice区间, brandId, catalog3Id, attrs
	/**
	 * hasStock=0/1
	 */
	private Integer hasStock; // 是否有货
	
	/**
	 * skuPrice=1_500/_500/500_
	 */
	private String skuPrice; // 价格区间

	/**
	 * brandId=1&brandId=2
	 */
	private List<Long> brandId; // 品牌ID 可多选
	
	/**
	 * attrs=1_其他:安卓&attrs=2_5寸:6寸
	 */
	private List<String> attrs; // 属性 可多选
	
	private Integer pageNum = 1;  // 页码
	
	private String queryString;
	
}
