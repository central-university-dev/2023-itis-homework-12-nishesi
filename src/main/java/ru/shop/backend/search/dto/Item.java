package ru.shop.backend.search.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    private Long price;
    private String name;
    private String url;
    private String image;
    private Long itemId;
    private String cat;
}
