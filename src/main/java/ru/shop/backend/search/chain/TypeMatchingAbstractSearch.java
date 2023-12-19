package ru.shop.backend.search.chain;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.shop.backend.search.model.ItemElastic;
import ru.shop.backend.search.repository.ItemElasticRepository;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import static ru.shop.backend.search.util.SearchUtils.findWithConvert;

@RequiredArgsConstructor
public abstract class TypeMatchingAbstractSearch {
    protected static final Pageable ONE_ELEMENT = PageRequest.of(0, 1);
    protected final ItemElasticRepository itemElasticRepository;

    protected String tryFindType(List<String> words, boolean needConvert) {
        String type = "";

        List<ItemElastic> local;
        for (Iterator<String> iterator = words.iterator(); iterator.hasNext(); ) {
            String word = iterator.next();
            local = findWithConvert(word, needConvert, t -> itemElasticRepository.findAllByTypeFuzzy(t, ONE_ELEMENT));
            if (!local.isEmpty()) {
                type = local.stream()
                        .map(ItemElastic::getType)
                        .min(Comparator.comparingInt(String::length))
                        .get();
                iterator.remove();
            }
        }
        return type;
    }
}
