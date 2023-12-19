package ru.shop.backend.search.chain;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SearchLink<T> {
    List<T> findAll(String text, Pageable pageable);
}
