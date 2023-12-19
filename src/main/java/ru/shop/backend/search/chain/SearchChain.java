package ru.shop.backend.search.chain;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.shop.backend.search.dto.CatalogueElastic;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SearchChain {
    private final List<SearchLink<CatalogueElastic>> chain;

    public List<CatalogueElastic> searchByText(String text, Pageable pageable) {
        for (var link : chain) {
            var result = link.findAll(text, pageable);
            if (!result.isEmpty())
                return result;
        }
        return List.of();
    }
}
