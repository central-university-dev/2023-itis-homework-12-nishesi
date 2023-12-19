package ru.shop.backend.search.chain.links;

import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.shop.backend.search.chain.SearchLink;
import ru.shop.backend.search.dto.CatalogueElastic;
import ru.shop.backend.search.model.ItemElastic;
import ru.shop.backend.search.repository.ItemElasticRepository;

import java.util.ArrayList;
import java.util.List;

import static ru.shop.backend.search.util.SearchUtils.*;
import static ru.shop.backend.search.util.StringUtils.parseAndAssertNeedConvert;

@Order(25)
@Component
@RequiredArgsConstructor
public class NameMatchingSearch implements SearchLink<CatalogueElastic> {
    private final ItemElasticRepository itemElasticRepository;

    @Override
    public List<CatalogueElastic> findAll(String text, Pageable pageable) {
        List<String> words = new ArrayList<>();
        boolean needConvert = parseAndAssertNeedConvert(text, words);

        // '_' - prevent fuzzy search for last word
        text =  String.join(" ", words) + "_";
        List<ItemElastic> list = findWithConvert(text, needConvert,
                t -> itemElasticRepository.findByNameOrDescription(t, pageable));
        var result = findExactMatching(list, words, "");
        return result.orElseGet(() -> groupByCatalogue(list, ""));
    }
}
