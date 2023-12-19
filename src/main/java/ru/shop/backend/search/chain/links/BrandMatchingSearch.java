package ru.shop.backend.search.chain.links;

import org.springframework.core.annotation.Order;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.shop.backend.search.chain.SearchLink;
import ru.shop.backend.search.chain.TypeMatchingAbstractSearch;
import ru.shop.backend.search.dto.CatalogueElastic;
import ru.shop.backend.search.model.ItemElastic;
import ru.shop.backend.search.repository.ItemElasticRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ru.shop.backend.search.util.SearchUtils.*;
import static ru.shop.backend.search.util.StringUtils.*;

@Order(10)
@Component
public class BrandMatchingSearch extends TypeMatchingAbstractSearch implements SearchLink<CatalogueElastic> {
    private static final Pageable ONE = PageRequest.of(0, 1);
    public BrandMatchingSearch(ItemElasticRepository repository) {
        super(repository);
    }

    @Override
    public List<CatalogueElastic> findAll(String text, Pageable pageable) {
        List<String> words = new ArrayList<>();
        boolean needConvert = parseAndAssertNeedConvert(text, words);

        String brand = tryFindBrand(words, needConvert);
        if (brand.isEmpty())
            return List.of();

        if (words.isEmpty()) {
            var list = itemElasticRepository.findAllByBrandFuzzy(brand, pageable);
            return groupByCatalogue(list, brand);
        }

        String type = tryFindType(words, needConvert, pageable);

        // '_' - prevent fuzzy search for last word
        text = String.join(" ", words) + "_";
        int fuzziness = type.isEmpty() ? 1 : 2;
        List<ItemElastic> list = findWithConvert(text, needConvert, i ->
                itemElasticRepository.findByTextAndOptionalFilterByBrandAndType(i, fuzziness, brand, type, pageable));

        Optional<List<CatalogueElastic>> result = findExactMatching(list, words, brand);
        return result.orElseGet(() -> groupByCatalogue(list, brand));
    }

    private String tryFindBrand(List<String> words, boolean needConvert) {
        for (var iterator = words.iterator(); iterator.hasNext(); ) {
            String word = iterator.next();
            var list = itemElasticRepository.findAllByBrandFuzzy(createQuery(word, needConvert), ONE);
            if (!list.isEmpty()) {
                iterator.remove();
                return list.get(0).getBrand();
            }
        }
        return "";
    }
}
