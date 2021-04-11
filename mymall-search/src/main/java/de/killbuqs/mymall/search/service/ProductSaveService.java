package de.killbuqs.mymall.search.service;

import java.io.IOException;
import java.util.List;

import de.killbuqs.common.to.es.SkuEsModel;

public interface ProductSaveService {

	public boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException;

}
