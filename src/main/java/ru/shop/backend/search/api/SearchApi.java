package ru.shop.backend.search.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.shop.backend.search.dto.SearchResult;
import ru.shop.backend.search.dto.SearchResultElastic;

@RequestMapping("/api/search")
@Tag(name = "Поиск", description = "Методы поиска")
public interface SearchApi {

    @Operation(description = "Найти каталоги и товары с ценами соответствующего региона")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Результаты поиска",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = SearchResult.class))}),
            @ApiResponse(responseCode = "400", description = "Ошибка обработки",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Регион не найден",
                    content = @Content)
    })
    @GetMapping
    SearchResult find(
            @Parameter(description = "Поисковый запрос")
            @RequestParam
            String text,
            @CookieValue(defaultValue = "1")
            int regionId
    );

    @Operation(description = "Найти товары сгруппировав по каталогам")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Результаты поиска",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = SearchResultElastic.class))}),
            @ApiResponse(responseCode = "400", description = "Ошибка обработки",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Регион не найден",
                    content = @Content)
    })
    @GetMapping("/by")
    SearchResultElastic finds(
            @Parameter(description = "Поисковый запрос")
            @RequestParam
            String text);
}
