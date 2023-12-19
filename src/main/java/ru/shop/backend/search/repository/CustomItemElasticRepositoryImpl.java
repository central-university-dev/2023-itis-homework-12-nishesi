package ru.shop.backend.search.repository;

import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Component;
import ru.shop.backend.search.model.ItemElastic;

import java.util.List;
import java.util.stream.Collectors;

import static org.elasticsearch.index.query.Operator.AND;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.index.query.QueryBuilders.multiMatchQuery;

@Component
@RequiredArgsConstructor
public class CustomItemElasticRepositoryImpl implements CustomItemElasticRepository {
    private final ElasticsearchRestTemplate template;

    @Override
    public List<ItemElastic> findByTextAndOptionalFilterByBrandAndType(
            String text, int textFuzziness, String brand, String type, Pageable pageable
    ) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        if (!text.isEmpty())
            boolQuery.must(multiMatchQuery(text)
                    .field("description")
                    .field("name", 4)
                    .fuzziness(textFuzziness)
                    .analyzer("russian")
                    .operator(AND));

        if (!brand.isEmpty())
            boolQuery.filter(matchQuery("brand", brand));

        if (!type.isEmpty())
            boolQuery.filter(matchQuery("type", type));

        return performQuery(pageable, boolQuery);
    }

    private List<ItemElastic> performQuery(Pageable pageable, BoolQueryBuilder boolQuery) {
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .withPageable(pageable)
                .build();

        SearchHits<ItemElastic> result = template.search(query, ItemElastic.class);
        return result.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }
}
