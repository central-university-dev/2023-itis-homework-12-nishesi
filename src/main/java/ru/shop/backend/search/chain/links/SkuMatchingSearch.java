package ru.shop.backend.search.chain.links;

import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.shop.backend.search.chain.SearchLink;
import ru.shop.backend.search.converter.ItemConverter;
import ru.shop.backend.search.dto.CatalogueElastic;
import ru.shop.backend.search.repository.ItemJpaRepository;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNumeric;

@Order(0)
@Component
@RequiredArgsConstructor
public class SkuMatchingSearch implements SearchLink<CatalogueElastic> {
    private final ItemJpaRepository itemJpaRepository;
    private final ItemConverter itemConverter;

    @Override
    public List<CatalogueElastic> findAll(String text, Pageable pageable) {
        if (!isNumeric(text))
            return List.of();

        return itemJpaRepository.findBySku(text)
                .map(itemConverter::toItemElastic)
                .map(item -> List.of(new CatalogueElastic(
                        item.getCatalogue(),
                        item.getCatalogueId(),
                        List.of(item),
                        item.getBrand()
                )))
                .orElseGet(List::of);
    }
}
