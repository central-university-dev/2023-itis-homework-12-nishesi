package ru.shop.backend.search;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.shop.backend.search.chain.SearchChain;
import ru.shop.backend.search.model.CatalogueElastic;
import ru.shop.backend.search.model.ItemElastic;
import ru.shop.backend.search.repository.ItemDbRepository;
import ru.shop.backend.search.repository.ItemRepository;
import ru.shop.backend.search.service.SearchService;
import ru.shop.backend.search.util.SimplePostgresContainer;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SearchApplication.class)
@ContextConfiguration(initializers = {SearchServiceIntegrationTest.TestContextInitializer.class})
public class SearchServiceIntegrationTest {
    @Container
    static final PostgreSQLContainer<?> postgres = new SimplePostgresContainer()
            .withInitScript("test-schema.sql");

    @MockBean
    ItemRepository itemElasticRepository;

    @MockBean
    SearchChain searchChain;

    @Autowired
    ItemDbRepository itemJpaRepository;

    @Autowired
    EntityManager entityManager;

    @Autowired
    SearchService searchService;

    @BeforeAll
    static void setUp() {
        postgres.start();
    }

    @AfterAll
    static void destroy() {
        postgres.stop();
    }

    static class TestContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + postgres.getJdbcUrl(),
                    "spring.datasource.username=" + postgres.getUsername(),
                    "spring.datasource.password=" + postgres.getPassword(),
                    "spring.datasource.driver-class-name=org.postgresql.Driver"
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

    @Nested
    class test_method_getSearchResult {

        @Nested
        class test_catalogues {
            @Test
            void should_return_only_from_SearchChain() {
                var catalogues = List.of(
                        new CatalogueElastic(null, 105L, List.of(
                                new ItemElastic(null, null, 10L, 105L, null, null, null, null)
                        ), null),
                        new CatalogueElastic(null, 106L, List.of(
                                new ItemElastic(null, null, 11L, 106L, null, null, null, null)
                        ), null)
                );
                when(searchChain.searchByText(any(), any())).thenReturn(catalogues);
                var categories = searchService.getSearchResult(1, "any").getCategories();

                assertThat(categories)
                        .hasSize(2)
                        .anySatisfy(cat -> assertThat(cat)
                                .hasFieldOrPropertyWithValue("name", "cat_name_5"))
                        .anySatisfy(cat -> assertThat(cat)
                                .hasFieldOrPropertyWithValue("name", "cat_name_6"));
            }

            @Test
            void should_return_cats_of_items() {
            }

            @Test
            void should_return_only_if_have_parent() {
                var catalogues = List.of(
                        new CatalogueElastic(null, 107L, List.of(
                                new ItemElastic(null, null, 12L, 107L, null, null, null, null)
                        ), null),
                        new CatalogueElastic(null, 108L, List.of(
                                new ItemElastic(null, null, 13L, 108L, null, null, null, null)
                        ), null),
                        new CatalogueElastic(null, 109L, List.of(
                                new ItemElastic(null, null, 14L, 109L, null, null, null, null)
                        ), null)
                );
                when(searchChain.searchByText(any(), any())).thenReturn(catalogues);
                var categories = searchService.getSearchResult(1, "any").getCategories();

                assertThat(categories)
                        .hasSize(2)
                        .anySatisfy(cat -> assertThat(cat)
                                .hasFieldOrPropertyWithValue("name", "cat_name_7"))
                        .anySatisfy(cat -> assertThat(cat)
                                .hasFieldOrPropertyWithValue("name", "cat_name_8"));
            }

            @Test
            void should_return_by_unique_url() {
                var catalogues = List.of(
                        new CatalogueElastic(null, 110L, List.of(
                                new ItemElastic(null, null, 15L, 110L, null, null, null, null)
                        ), null),
                        new CatalogueElastic(null, 111L, List.of(
                                new ItemElastic(null, null, 16L, 111L, null, null, null, null)
                        ), null)
                );
                when(searchChain.searchByText(any(), any())).thenReturn(catalogues);
                var categories = searchService.getSearchResult(1, "any").getCategories();

                assertThat(categories)
                        .hasSize(1)
                        .allSatisfy(cat -> assertThat(cat).satisfiesAnyOf(
                                category -> assertThat(category)
                                        .hasFieldOrPropertyWithValue("name", "cat_name_10"),
                                category -> assertThat(category)
                                        .hasFieldOrPropertyWithValue("name", "cat_name_11")));
            }

            @Test
            void should_return_url_with_brand() {
                var catalogues = List.of(
                        new CatalogueElastic(null, 112L, List.of(
                                new ItemElastic(null, null, 17L, 112L, null, null, null, null)
                        ), "Apple")
                );
                when(searchChain.searchByText(any(), any())).thenReturn(catalogues);
                var categories = searchService.getSearchResult(1, "any").getCategories();

                assertThat(categories)
                        .hasSize(1)
                        .allSatisfy(cat -> assertThat(cat)
                                .hasFieldOrPropertyWithValue("name", "cat_name_12")
                                .hasFieldOrPropertyWithValue("url", "/cat/smartphones/brands/apple")
                                .hasFieldOrPropertyWithValue("parentUrl", "/cat/techniques")
                        );
            }

            @Test
            void should_return_url_without_brand() {
                var catalogues = List.of(
                        new CatalogueElastic(null, 112L, List.of(
                                new ItemElastic(null, null, 17L, 112L, null, "", null, null)
                        ), null)
                );
                when(searchChain.searchByText(any(), any())).thenReturn(catalogues);
                var categories = searchService.getSearchResult(1, "any").getCategories();

                assertThat(categories)
                        .hasSize(1)
                        .allSatisfy(cat -> assertThat(cat)
                                .hasFieldOrPropertyWithValue("name", "cat_name_12")
                                .hasFieldOrPropertyWithValue("url", "/cat/smartphones")
                                .hasFieldOrPropertyWithValue("parentUrl", "/cat/techniques")
                        );
            }
        }
    }

    @Nested
    class bug_fix {
        @Nested
        class test_items {
            @Test
            void should_return_all_from_searchChain() {
                var catalogues = List.of(
                        new CatalogueElastic(null, 101L, List.of(
                                new ItemElastic(null, null, 1L, 101L, null, null, null, null),
                                new ItemElastic(null, null, 2L, 101L, null, null, null, null)
                        ), null),
                        new CatalogueElastic(null, 102L, List.of(
                                new ItemElastic(null, null, 3L, 102L, null, null, null, null)
                        ), null)
                );
                when(searchChain.searchByText(any(), any())).thenReturn(catalogues);
                var items = searchService.getSearchResult(1, "any").getItems();

                assertThat(items)
                        .hasSize(3)
                        .anySatisfy(item -> assertThat(item)
                                .hasFieldOrPropertyWithValue("itemId", 1L))
                        .anySatisfy(item -> assertThat(item)
                                .hasFieldOrPropertyWithValue("itemId", 2L))
                        .anySatisfy(item -> assertThat(item)
                                .hasFieldOrPropertyWithValue("itemId", 3L));
            }

            @Test
            void should_return_only_containing_in_remain_and_in_item() {
                var catalogues = List.of(
                        new CatalogueElastic(null, 103L, List.of(
                                new ItemElastic(null, null, 4L, 103L, null, null, null, null),
                                new ItemElastic(null, null, 5L, 103L, null, null, null, null),
                                new ItemElastic(null, null, 6L, 103L, null, null, null, null)
                        ), null)
                );
                when(searchChain.searchByText(any(), any())).thenReturn(catalogues);
                var items = searchService.getSearchResult(2, "any").getItems();

                assertThat(items)
                        .hasSize(1)
                        .anySatisfy(item -> assertThat(item)
                                .hasFieldOrPropertyWithValue("itemId", 5L));
            }

            @Test
            void should_return_with_correct_region() {
                var catalogues = List.of(
                        new CatalogueElastic(null, 103L, List.of(
                                new ItemElastic(null, null, 7L, 103L, null, null, null, null),
                                new ItemElastic(null, null, 8L, 103L, null, null, null, null)
                        ), null)
                );
                when(searchChain.searchByText(any(), any())).thenReturn(catalogues);
                var items = searchService.getSearchResult(3, "any").getItems();

                assertThat(items)
                        .hasSize(2)
                        .anySatisfy(item -> assertThat(item)
                                .hasFieldOrPropertyWithValue("itemId", 7L)
                                .hasFieldOrPropertyWithValue("price", 101001001000L))
                        .anySatisfy(item -> assertThat(item)
                                .hasFieldOrPropertyWithValue("itemId", 8L)
                                .hasFieldOrPropertyWithValue("price", 30L));
            }

            @Test
            void should_return_with_correct_data() {
                var catalogues = List.of(
                        new CatalogueElastic(null, 104L, List.of(
                                new ItemElastic(null, null, 9L, 104L, null, null, null, null)
                        ), null)
                );
                when(searchChain.searchByText(any(), any())).thenReturn(catalogues);
                var items = searchService.getSearchResult(1, "any").getItems();

                assertThat(items)
                        .hasSize(1)
                        .anySatisfy(item -> assertThat(item)
                                .hasFieldOrPropertyWithValue("itemId", 9L)
                                .hasFieldOrPropertyWithValue("name", "Kit Kat")
                                .hasFieldOrPropertyWithValue("url", "kit-kat-1")
                                .hasFieldOrPropertyWithValue("image", "kit-kat-image.jpg")
                                .hasFieldOrPropertyWithValue("cat", "Chocolate Bar")
                                .hasFieldOrPropertyWithValue("price", 10L));
            }
        }
    }
}
