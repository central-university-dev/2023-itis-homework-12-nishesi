package ru.shop.backend.search.chain.links;

import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.shop.backend.search.chain.SearchLink;
import ru.shop.backend.search.chain.TypeMatchingAbstractSearch;
import ru.shop.backend.search.dto.CatalogueElastic;
import ru.shop.backend.search.model.ItemElastic;
import ru.shop.backend.search.repository.ItemElasticRepository;

import java.util.ArrayList;
import java.util.List;

import static ru.shop.backend.search.util.SearchUtils.*;
import static ru.shop.backend.search.util.StringUtils.parseAndAssertNeedConvert;

@Order(15)
@Component
public class CatalogueMatchingSearch extends TypeMatchingAbstractSearch implements SearchLink<CatalogueElastic> {
    public CatalogueMatchingSearch(ItemElasticRepository repository) {
        super(repository);
    }

    @Override
    public List<CatalogueElastic> findAll(String text, Pageable pageable) {
        List<String> words = new ArrayList<>();
        boolean needConvert = parseAndAssertNeedConvert(text, words);
        Long catalogueId = tryFindCatalogueId(words, needConvert);
        if (catalogueId == null)
            return List.of();

        String type = tryFindType(words, needConvert);

        List<ItemElastic> list = findByCriteria(type, catalogueId, pageable);

        var result = findExactMatching(list, List.of(text.split(" ")), "");
        return result.orElseGet(() -> groupByCatalogue(list, ""));
    }

    private Long tryFindCatalogueId(List<String> words, boolean needConvert) {
        for (var iterator = words.iterator(); iterator.hasNext(); ) {
            String word = iterator.next();
            var list = findWithConvert(word, needConvert,
                    t -> itemElasticRepository.findAllByCatalogueFuzzy(t, ONE_ELEMENT));
            if (!list.isEmpty()) {
                iterator.remove();
                return list.get(0).getCatalogueId();
            }
        }
        return null;
    }

    private List<ItemElastic> findByCriteria(String type, Long catalogueId, Pageable pageable) {
        if (type.isEmpty()) {
            return itemElasticRepository.findByCatalogueId(catalogueId, pageable);
        }
        return itemElasticRepository.findByCatalogueIdAndType(catalogueId, type, pageable);
    }
}
