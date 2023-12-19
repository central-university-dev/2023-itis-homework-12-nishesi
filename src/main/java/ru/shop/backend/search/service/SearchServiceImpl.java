package ru.shop.backend.search.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.shop.backend.search.chain.SearchChain;
import ru.shop.backend.search.converter.CatalogueConverter;
import ru.shop.backend.search.converter.ItemConverter;
import ru.shop.backend.search.dto.*;
import ru.shop.backend.search.model.CatalogueWithParent;
import ru.shop.backend.search.model.ItemElastic;
import ru.shop.backend.search.model.ItemWithPrice;
import ru.shop.backend.search.repository.CatalogueJpaRepository;
import ru.shop.backend.search.repository.ItemJpaRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.shop.backend.search.util.SearchUtils.getTypeQueries;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {
    private static final Pageable PAGE_150 = PageRequest.of(0, 150);
    private static final Pageable PAGE_10 = PageRequest.of(0, 10);
    private final SearchChain searchChain;
    private final ItemJpaRepository itemJpaRepository;
    private final CatalogueJpaRepository catalogueJpaRepository;
    private final ItemConverter itemConverter;
    private final CatalogueConverter catalogueConverter;

    @Override
    public SearchResult getSearchResult(Integer regionId, String text) {
        text = text.trim().replaceAll("\\s+", " ");
        List<CatalogueElastic> searchResult = searchChain.searchByText(text, PAGE_10);
        List<ItemWithPrice> items = getItemsWithPrices(regionId, searchResult);
        List<CatalogueWithParent> catalogues = getCataloguesWithParents(items);

        String brand = !searchResult.isEmpty() && searchResult.get(0).getBrand() != null
                ? searchResult.get(0).getBrand().toLowerCase(Locale.ROOT)
                : "";

        return new SearchResult(
                itemConverter.toItem(items),
                catalogueConverter.toCategory(catalogues, brand),
                getTypeQueries(searchResult, brand)
        );
    }

    @Override
    public SearchResultElastic getSearchResultElastic(String text) {
        List<CatalogueElastic> list = searchChain.searchByText(text, PAGE_150);
        return new SearchResultElastic(list);
    }

    private List<ItemWithPrice> getItemsWithPrices(Integer regionId, List<CatalogueElastic> result) {
        List<Long> foundItemIds = result.stream()
                .flatMap(category -> category.getItems().stream())
                .map(ItemElastic::getItemId)
                .collect(Collectors.toList());
        return itemJpaRepository.findAllByRegionIdAndIdIn(regionId, foundItemIds);
    }

    private List<CatalogueWithParent> getCataloguesWithParents(List<ItemWithPrice> items) {
        var catalogueIds = items.stream()
                .map(ItemWithPrice::getCatalogueId)
                .distinct()
                .collect(Collectors.toList());
        Set<String> uniqueUrls = new HashSet<>();
        return catalogueJpaRepository.findAllWithParentByIdIn(catalogueIds)
                .stream()
                .filter(c -> uniqueUrls.add(c.getUrl()))
                .collect(Collectors.toList());
    }
}
