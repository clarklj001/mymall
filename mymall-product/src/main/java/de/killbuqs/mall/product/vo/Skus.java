/**
  * Copyright 2021 bejson.com 
  */
package de.killbuqs.mall.product.vo;
import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

/**
 * Auto-generated: 2021-04-04 16:4:53
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
public class Skus {

    private List<Attr> attr;
    private String skuName;
    private BigDecimal price;
    private String skuTitle;
    private String skuSubtitle;
    private List<Images> images;
    private List<String> descar;
    private int fullCount;
    private BigDecimal discount;
    private int countStatus;
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private int priceStatus;
    private List<MemberPrice> memberPrice;
    

}