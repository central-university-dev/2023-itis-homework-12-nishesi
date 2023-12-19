package ru.shop.backend.search.converter;

import org.springframework.stereotype.Component;
import ru.shop.backend.search.dto.Item;
import ru.shop.backend.search.model.ItemElastic;
import ru.shop.backend.search.model.ItemEntity;
import ru.shop.backend.search.model.ItemWithPrice;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Component
public class ItemConverter {

    public List<Item> toItem(List<ItemWithPrice> list) {
        return list.stream()
                .map(item -> new Item(
                        item.getPrice(),
                        item.getName(),
                        item.getUrl(),
                        item.getImage(),
                        item.getItemId(),
                        item.getType()))
                .collect(Collectors.toList());
    }

    public ItemElastic toItemElastic(ItemEntity entity) {
        String desc = buildDescription(entity.getDescription());
        return ItemElastic.builder()
                .description(desc)
                .fulltext(entity.getCatalogue() + " " + entity.getType() + " " + entity.getName() + " " + desc)
                .name(entity.getName().replace(entity.getBrand(), "").trim())
                .itemId(entity.getItemId())
                .catalogueId(entity.getCatalogueId())
                .catalogue(entity.getCatalogue())
                .brand(entity.getBrand())
                .type(entity.getType())
                .build();
    }

    private static String buildDescription(String description) {
        return Arrays.stream(description.split(";"))
                .filter(s -> !s.contains(": нет")
                        && !s.contains(": -")
                        && !s.contains(": 0"))
                .map(s -> s.toLowerCase(Locale.ROOT)
                        .replace(": есть", "")
                        .replace(":", ""))
                .collect(Collectors.joining());
    }
}
