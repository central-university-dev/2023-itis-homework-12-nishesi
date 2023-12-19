package ru.shop.backend.search.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    private String name;
    private String parentName;
    private String url;
    private String parentUrl;
    private String image;
}
