package ru.shop.backend.search.util;

import ru.shop.backend.search.dto.CatalogueElastic;
import ru.shop.backend.search.dto.TypeHelpText;
import ru.shop.backend.search.dto.TypeOfQuery;
import ru.shop.backend.search.model.ItemElastic;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.shop.backend.search.util.StringUtils.convert;

public class SearchUtils {
    public static List<CatalogueElastic> groupByCatalogue(List<ItemElastic> list, String brand) {
        return list.stream()
                .collect(Collectors.groupingBy(ItemElastic::getCatalogueId))
                .values().stream()
                .map(items -> {
                    var item = items.get(0);
                    return new CatalogueElastic(
                            item.getCatalogue(),
                            item.getCatalogueId(),
                            items,
                            brand);
                })
                .collect(Collectors.toList());
    }

    public static Optional<List<CatalogueElastic>> findExactMatching(List<ItemElastic> list, List<String> words, String brand) {
        String text = String.join(" ", words);
        return list.stream()
                .filter(item -> Objects.equals(text, item.getName()) ||
                        text.startsWith(item.getType()) && text.endsWith(item.getName()))
                .findFirst()
                .map(item -> List.of(
                        new CatalogueElastic(
                                item.getCatalogue(),
                                item.getCatalogueId(),
                                List.of(item),
                                brand)));
    }

    public static List<TypeHelpText> getTypeQueries(List<CatalogueElastic> catalogues, String brand) {
        if (catalogues.isEmpty())
            return List.of();
        ItemElastic item = catalogues.get(0).getItems().get(0);
        String type = item.getType();
        if (type == null)
            type = "";

        return List.of(new TypeHelpText(
                TypeOfQuery.SEE_ALSO,
                (type + " " + brand).trim()));
    }

    public static <T> List<T> findWithConvert(String text, boolean needConvert, Function<String, List<T>> function) {
        List<T> list = function.apply(text);
        if (list.isEmpty() && needConvert)
            return function.apply(convert(text));
        return list;
    }
}
