package ru.shop.backend.search.chain.links;

import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.shop.backend.search.chain.SearchLink;
import ru.shop.backend.search.dto.CatalogueElastic;
import ru.shop.backend.search.model.ItemElastic;
import ru.shop.backend.search.repository.ItemElasticRepository;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNumeric;
import static ru.shop.backend.search.util.SearchUtils.groupByCatalogue;

@Order(5)
@Component
@RequiredArgsConstructor
public class NumericNameMatchingSearch implements SearchLink<CatalogueElastic> {
    private final ItemElasticRepository itemElasticRepository;

    @Override
    public List<CatalogueElastic> findAll(String text, Pageable pageable) {
        if (!isNumeric(text))
            return List.of();
        List<ItemElastic> list = itemElasticRepository.findAllByNameContaining(text, pageable);
        return groupByCatalogue(list, "");
    }
}
