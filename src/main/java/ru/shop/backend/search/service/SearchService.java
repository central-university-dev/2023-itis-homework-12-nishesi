package ru.shop.backend.search.service;

import ru.shop.backend.search.dto.SearchResult;
import ru.shop.backend.search.dto.SearchResultElastic;

public interface SearchService {
    SearchResult getSearchResult(Integer regionId, String text);

    SearchResultElastic getSearchResultElastic(String text);
}
