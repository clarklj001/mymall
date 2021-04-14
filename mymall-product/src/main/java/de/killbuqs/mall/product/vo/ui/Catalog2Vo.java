package de.killbuqs.mall.product.vo.ui;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Catalog2Vo {

	private String catalog1Id;
	private List<Catalog3Vo> catalog3List;
	private String id;
	private String name;

	@AllArgsConstructor
	@NoArgsConstructor
	@Data
	public static class Catalog3Vo {
		private String catalog2Id;
		private String id;
		private String name;
		
	}
}
