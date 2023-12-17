package ru.shop.backend.search.chain;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.shop.backend.search.model.CatalogueElastic;
import ru.shop.backend.search.service.SearchService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SearchChain {
    private final SearchService service;

    public List<CatalogueElastic> searchByText(String text, Pageable pageable) {
        return service.getAll(text, pageable);
    }
}
