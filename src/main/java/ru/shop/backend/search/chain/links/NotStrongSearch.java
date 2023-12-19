package ru.shop.backend.search.chain.links;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.shop.backend.search.chain.SearchLink;
import ru.shop.backend.search.dto.CatalogueElastic;
import ru.shop.backend.search.repository.ItemElasticRepository;

import java.util.List;

import static ru.shop.backend.search.util.SearchUtils.findWithConvert;
import static ru.shop.backend.search.util.SearchUtils.groupByCatalogue;

@Component
@RequiredArgsConstructor
public class NotStrongSearch implements SearchLink<CatalogueElastic> {
    private final ItemElasticRepository itemElasticRepository;
    @Override
    public List<CatalogueElastic> findAll(String text, Pageable pageable) {
        text += "_";
        var list = findWithConvert(text, true,
                t -> itemElasticRepository.findByFulltext(t, pageable));
        return groupByCatalogue(list, "");
    }
}
