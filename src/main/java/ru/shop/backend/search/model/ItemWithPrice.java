package ru.shop.backend.search.model;


public interface ItemWithPrice {
    long getItemId();
    String getName();
    String getUrl();
    String getImage();
    String getType();
    long getPrice();
    Long getCatalogueId();
}
