package ru.shop.backend.search.chain;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.shop.backend.search.model.CatalogueElastic;
import ru.shop.backend.search.repository.ItemDbRepository;
import ru.shop.backend.search.service.SearchService;

import java.util.List;

import static ru.shop.backend.search.service.SearchService.isNumeric;

@Component
@RequiredArgsConstructor
public class SearchChain {
    @Lazy
    @Autowired
    private SearchService service;
    @Autowired
    private ItemDbRepository repoDb;

    public List<CatalogueElastic> searchByText(String text, Pageable pageable) {
        if (isNumeric(text)) {
            Integer itemId = repoDb.findBySku(text).stream().findFirst().orElse(null);
            if (itemId == null) {
                var catalogue = service.getByName(text);
                if (catalogue.size() > 0) {
                    return catalogue;
                }
            }
            try {
                return service.getByItemId(itemId.toString());
            } catch (Exception e) {
            }
        }
        return service.getAll(text, pageable);
    }
}
