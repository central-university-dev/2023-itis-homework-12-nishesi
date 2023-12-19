package ru.shop.backend.search.model;

public interface CatalogueWithParent {
    String getName();
    String getParentName();
    String getUrl();
    String getParentUrl();
    String getImage();
}
