package de.killbuqs.common.to.es;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

/*
"skuId": {
	"type": "long"
},
"spuId": {
	"type": "keyword"
},
"skuTitle": {
	"type": "text",
	"analyzer": "ik_smart"
},
"skuPrice": {
	"type": "keyword"
},
"skuImg": {
	"type": "keyword",
	"index": false,
	"doc_values": false
},
"saleCount": {
	"type": "long"
},
"hasStock": {
	"type": "boolean"
},
"hotScore": {
	"type": "long"
},
"brandId": {
	"type": "long"
},
"catalogId": {
	"type": "long"
},
"catalogName": {
	"type": "keyword",
	"index": false,
	"doc_values": false
},
"brandName": {
	"type": "keyword",
	"index": false,
	"doc_values": false
},
"brandImg": {
	"type": "keyword",
	"index": false,
	"doc_values": false
},
"attrs": {
	"type": "nested",
	"properties": {
		"attrId": {
			"type": "long"
		},
		"attrName": {
			"type": "keyword",
			"index": false,
			"doc_values": false
		},
		"attrValue": {
			"type": "keyword"
				*/
@Data
public class SkuEsModel {

	private Long skuId;
	private Long spuId;
	private String skuTitle;
	private BigDecimal skuPrice;
	private String skuImg;
	private Long ssaleCount;
	private Boolean hasStock;
	private Long hotScore;
	private Long sbrandId;
	private Long scatalogId;
	private String catalogName;
	private String brandName;
	private String brandImg;
	private List<Attrs> attrs;

	@Data
	public static class Attrs {
		private Long attrId;
		private String attrName;
		private String attrValue;
	}
}
