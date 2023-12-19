package ru.shop.backend.search.repository;

import org.springframework.data.domain.Pageable;
import ru.shop.backend.search.model.ItemElastic;

import java.util.List;

public interface CustomItemElasticRepository {
    List<ItemElastic> findByTextAndOptionalFilterByBrandAndType(
            String text, int textFuzziness, String brand, String type, Pageable pageable);
}
