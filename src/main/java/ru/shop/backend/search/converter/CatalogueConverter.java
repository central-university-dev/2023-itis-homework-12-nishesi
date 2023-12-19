package ru.shop.backend.search.converter;

import org.springframework.stereotype.Component;
import ru.shop.backend.search.model.CatalogueWithParent;
import ru.shop.backend.search.dto.Category;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CatalogueConverter {
    public List<Category> toCategory(List<CatalogueWithParent> list, String brand) {
        return list.stream()
                .map(c -> new Category(c.getName(),
                        c.getParentName(),
                        "/cat/" + c.getUrl() + (brand.isBlank() ? "" : "/brands/" + brand),
                        "/cat/" + c.getParentUrl(),
                        c.getImage()))
                .collect(Collectors.toList());
    }
}
