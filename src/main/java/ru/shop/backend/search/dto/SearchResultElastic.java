package ru.shop.backend.search.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchResultElastic {
    public List<CatalogueElastic> result;
}
