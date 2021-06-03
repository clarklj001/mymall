package de.killbuqs.mymall.search.service;

import de.killbuqs.mymall.search.vo.SearchParam;
import de.killbuqs.mymall.search.vo.SearchResult;

public interface MallSearchService {

	public SearchResult search(SearchParam param);

}
