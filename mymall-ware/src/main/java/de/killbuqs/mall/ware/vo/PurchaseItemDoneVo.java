package de.killbuqs.mall.ware.vo;

import lombok.Data;

@Data
public class PurchaseItemDoneVo {

	private Long itemId;

	// TODO: 采购结束， 采购项的状态只能是3(完成)，4(失败)， 这里需要做一个验证
	private Integer status;

	private String reason;

}
