package de.killbuqs.mall.ware.vo;

import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class PurchaseDoneVo {

	@NotNull
	private Long id;

	private List<PurchaseItemDoneVo> items;
}
