package ru.shop.backend.search;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.shop.backend.search.chain.SearchChain;
import ru.shop.backend.search.model.ItemElastic;
import ru.shop.backend.search.repository.ItemDbRepository;
import ru.shop.backend.search.service.ReindexSearchService;
import ru.shop.backend.search.util.SimpleElasticsearchContainer;
import ru.shop.backend.search.util.SimplePostgresContainer;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

// if run any test case separately, make sure, that scheduled task is done
@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SearchApplication.class)
@ContextConfiguration(initializers = {TestMain.TestContextInitializer.class})
public class TestMain {
    @Container
    static final ElasticsearchContainer elastic = new SimpleElasticsearchContainer();

    @Container
    static final PostgreSQLContainer<?> postgres = new SimplePostgresContainer()
            .withInitScript("test-schema.sql");

    @Autowired
    ItemDbRepository itemJpaRepository;

    @Autowired
    ReindexSearchService schedulingService;

    @Autowired
    EntityManager entityManager;

    @Autowired
    ElasticsearchRestTemplate elasticTemplate;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    SearchChain searchChain;


    @BeforeAll
    static void setUp() {
        elastic.start();
        postgres.start();
    }

    @AfterAll
    static void destroy() {
        elastic.stop();
        postgres.stop();
    }

    @Test
    @Order(1)
    void test_DB_filling() {
        List<?> items = entityManager.createNativeQuery("select * from item").getResultList();
        assertThat(items)
                .isNotEmpty()
                .hasSize(11);
    }

    @Test
    @Order(2)
    void test_ES_filling() throws InterruptedException {
        // wait until scheduled reindex task finishes
        Thread.sleep(1000);
        SearchHits<ItemElastic> items = elasticTemplate.search(Query.findAll(), ItemElastic.class);
        assertThat(items.getTotalHits())
                .isEqualTo(11);
    }

    static class TestContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + postgres.getJdbcUrl(),
                    "spring.datasource.username=" + postgres.getUsername(),
                    "spring.datasource.password=" + postgres.getPassword(),
                    "spring.elasticsearch.rest.uris=" + elastic.getHttpHostAddress(),
                    "spring.datasource.driver-class-name=org.postgresql.Driver"
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

    @Nested
    class test_method_searchByText {
        final Pageable pageable = PageRequest.of(0, 10);

        @Test
        void should_return_empty_list() {
            String text = "";
            var result = searchChain.searchByText(text, pageable);
            assertThat(result)
                    .isEmpty();
        }

        @Test
        void should_return_catalogue_and_item_by_sku() {
            String text = "100001";
            var result = searchChain.searchByText(text, pageable);
            assertThat(result)
                    .hasSize(1)
                    .allSatisfy(catalogue -> {
                        assertThat(catalogue)
                                .hasFieldOrPropertyWithValue("catalogueId", 101L);
                        assertThat(catalogue.getItems())
                                .hasSize(1)
                                .allMatch(item -> item.getItemId() == 1L);
                    });
        }

        @Test
        void should_return_cat_and_item_by_item_name() {
            String text = "200002";
            var result = searchChain.searchByText(text, pageable);
            assertThat(result)
                    .hasSize(1)
                    .allSatisfy(catalogue -> {
                        assertThat(catalogue)
                                .hasFieldOrPropertyWithValue("catalogueId", 101L);
                        assertThat(catalogue.getItems())
                                .hasSize(1)
                                .allMatch(item -> item.getItemId() == 2L);
                    });
        }

        //TODO изменить логику поиска по числовой строке.
//        @Test
//        void should_return_cat_and_item_by_text_as_regexp() {
//            // "'type'.*'name'"
//            String text = "30000199200003";
//            var result = searchChain.searchByText(text, pageable);
//            assertThat(result)
//                    .hasSize(1)
//                    .allSatisfy(catalogue -> {
//                        assertThat(catalogue)
//                                .hasFieldOrPropertyWithValue("catalogueId", 101L);
//                        assertThat(catalogue.getItems())
//                                .hasSize(1)
//                                .allMatch(item -> item.getItemId() == 3L);
//                    });
//        }

        @Test
        void should_return_by_name_containing_number() {
            String text = "200003";
            var result = searchChain.searchByText(text, pageable);

            assertThat(result)
                    .hasSize(2)
                    .anySatisfy(catalogue -> {
                        assertThat(catalogue)
                                .hasFieldOrPropertyWithValue("catalogueId", 101L);
                        assertThat(catalogue.getItems())
                                .hasSize(1)
                                .allMatch(item -> item.getItemId() == 3L &&
                                        item.getCatalogueId() == 101L);
                    })
                    .anySatisfy(catalogue -> {
                        assertThat(catalogue)
                                .hasFieldOrPropertyWithValue("catalogueId", 102L);
                        assertThat(catalogue.getItems())
                                .hasSize(2)
                                .anySatisfy(item -> assertThat(item)
                                        .hasFieldOrPropertyWithValue("itemId", 4L)
                                        .hasFieldOrPropertyWithValue("catalogueId", 102L)
                                )
                                .anySatisfy(item -> assertThat(item)
                                        .hasFieldOrPropertyWithValue("itemId", 5L)
                                        .hasFieldOrPropertyWithValue("catalogueId", 102L));
                    });
        }

        @ParameterizedTest
        @CsvSource({
                "Laptop,6", "Gaming Laptop,7"
        })
        void should_return_by_exact_name_matching(String text, long itemId) {
            var result = searchChain.searchByText(text, pageable);

            assertThat(result)
                    .hasSize(1)
                    .allSatisfy(catalogue -> {
                        assertThat(catalogue)
                                .hasFieldOrPropertyWithValue("catalogueId", 102L);
                        assertThat(catalogue.getItems())
                                .hasSize(1)
                                .allMatch(item -> item.getItemId() == itemId && item.getCatalogueId() == 102L);
                    });
        }

        @Nested
        class test_for_one_word {

            @ParameterizedTest
            @CsvSource({
                    "Laptp",
                    "Laptoop",
                    "Loptop",
            })
            void should_return_by_fuzzy_name_matching(String text) {
                var result = searchChain.searchByText(text, pageable);

                assertThat(result)
                        .hasSize(1)
                        .allSatisfy(catalogue -> {
                            assertThat(catalogue)
                                    .hasFieldOrPropertyWithValue("catalogueId", 102L);
                            assertThat(catalogue.getItems())
                                    .hasSize(2)
                                    .anySatisfy(item -> assertThat(item)
                                            .hasFieldOrPropertyWithValue("itemId", 6L)
                                            .hasFieldOrPropertyWithValue("catalogueId", 102L)
                                    )
                                    .anySatisfy(item -> assertThat(item)
                                            .hasFieldOrPropertyWithValue("itemId", 7L)
                                            .hasFieldOrPropertyWithValue("catalogueId", 102L));
                        });
            }

            @ParameterizedTest
            @CsvSource({"Xiaomi", "Xiyaomi", "Xiomi", "Xyaomi"})
            void should_not_return_by_brand(String text) {
                var result = searchChain.searchByText(text, pageable);

                assertThat(result)
                        .isEmpty();
            }

            @ParameterizedTest
            @CsvSource({"redmi,9", "desc_word,11"})
            public void should_return_by_name_or_description(String text, Long itemId) {
                var result = searchChain.searchByText(text, pageable);

                assertThat(result)
                        .hasSize(1)
                        .allSatisfy(catalogue -> {
                            assertThat(catalogue)
                                    .hasFieldOrPropertyWithValue("catalogueId", 111L);

                            assertThat(catalogue.getItems())
                                    .hasSize(1)
                                    .allSatisfy(item -> assertThat(item)
                                            .hasFieldOrPropertyWithValue("itemId", itemId)
                                            .hasFieldOrPropertyWithValue("catalogueId", 111L));
                        });
            }

            @ParameterizedTest
            @CsvSource({"smartphone", "smartpone", "smartpthone", "smartpfone"})
            public void should_return_by_type(String text) {
                var result = searchChain.searchByText(text, pageable);

                assertThat(result)
                        .hasSize(3)
                        .anySatisfy(catalogue -> {
                            assertThat(catalogue)
                                    .hasFieldOrPropertyWithValue("catalogueId", 111L);
                            assertThat(catalogue.getItems())
                                    .hasSize(1)
                                    .allMatch(item -> item.getItemId() == 9L && item.getCatalogueId() == 111L);})
                        .anySatisfy(catalogue -> {
                            assertThat(catalogue)
                                    .hasFieldOrPropertyWithValue("catalogueId", 112L);
                            assertThat(catalogue.getItems())
                                    .hasSize(1)
                                    .allMatch(item -> item.getItemId() == 10L && item.getCatalogueId() == 112L);
                        })
                        .anySatisfy(catalogue -> {
                            assertThat(catalogue)
                                    .hasFieldOrPropertyWithValue("catalogueId", 113L);
                            assertThat(catalogue.getItems())
                                    .hasSize(2)
                                    .allMatch(item -> (item.getItemId() == 12L || item.getItemId() == 13L)
                                            && item.getCatalogueId() == 113L);
                        });
            }

            @ParameterizedTest
            @CsvSource({"Corey", "corey", "crey", "coarey"})
            public void should_return_match_by_catalogue(String text) {
                var result = searchChain.searchByText(text, pageable);

                assertThat(result)
                        .hasSize(1)
                        .allSatisfy(catalogue -> {
                            assertThat(catalogue)
                                    .hasFieldOrPropertyWithValue("catalogueId", 112L);

                            assertThat(catalogue.getItems())
                                    .hasSize(2)
                                    .anySatisfy(item -> assertThat(item)
                                            .hasFieldOrPropertyWithValue("itemId", 10L)
                                            .hasFieldOrPropertyWithValue("catalogueId", 112L))
                                    .anySatisfy(item -> assertThat(item)
                                            .hasFieldOrPropertyWithValue("itemId", 16L)
                                            .hasFieldOrPropertyWithValue("catalogueId", 112L));
                        });
            }
        }

        @Nested
        class test_two_words {
            @ParameterizedTest
            @CsvSource({"Iphone 13", "Iphon 13"})
            void should_return_exact_last_word_match(String text) {
                var result = searchChain.searchByText(text, pageable);

                assertThat(result)
                        .hasSize(1)
                        .allSatisfy(catalogue -> {
                            assertThat(catalogue)
                                    .hasFieldOrPropertyWithValue("catalogueId", 113L);
                            assertThat(catalogue.getItems())
                                    .hasSize(1)
                                    .allSatisfy(item -> assertThat(item)
                                            .hasFieldOrPropertyWithValue("itemId", 12L)
                                            .hasFieldOrPropertyWithValue("catalogueId", 113L));
                        });
            }

            @ParameterizedTest
            @CsvSource({"13,smartphone","13,smartpone"})
            void should_return_exact_match_by_type_and_last_word(String text, String type) {
                var result = searchChain.searchByText(text + " " + type, pageable);
//            var result = service.getAll(text + " " + type, pageable);

                assertThat(result)
                        .hasSize(1)
                        .allSatisfy(catalogue -> {
                            assertThat(catalogue)
                                    .hasFieldOrPropertyWithValue("catalogueId", 113L);
                            assertThat(catalogue.getItems())
                                    .hasSize(1)
                                    .allSatisfy(item -> assertThat(item)
                                            .hasFieldOrPropertyWithValue("itemId", 12L)
                                            .hasFieldOrPropertyWithValue("catalogueId", 113L));
                        });
            }

            @ParameterizedTest
            @CsvSource({"13,Apple", "13,Aple", "13,apple"})
            void should_match_by_brand_and_name(String text, String brand) {
                var result = searchChain.searchByText(text + " " + brand, pageable);

                assertThat(result)
                        .hasSize(1)
                        .allSatisfy(catalogue -> {
                            assertThat(catalogue)
                                    .hasFieldOrPropertyWithValue("catalogueId", 113L);
                            assertThat(catalogue.getItems())
                                    .hasSize(1)
                                    .allSatisfy(item -> assertThat(item)
                                            .hasFieldOrPropertyWithValue("itemId", 12L)
                                            .hasFieldOrPropertyWithValue("catalogueId", 113L));
                        });
            }

            @ParameterizedTest
            @CsvSource({"Apple smartphone", "apple smartphon", "aple smartphonee"})
            void should_match_by_brand_and_type(String text) {
                var result = searchChain.searchByText(text, pageable);

                assertThat(result)
                        .hasSize(1)
                        .allSatisfy(catalogue -> {
                            assertThat(catalogue)
                                    .hasFieldOrPropertyWithValue("catalogueId", 113L);

                            assertThat(catalogue.getItems())
                                    .hasSize(2)
                                    .anySatisfy(item -> assertThat(item)
                                            .hasFieldOrPropertyWithValue("itemId", 12L)
                                            .hasFieldOrPropertyWithValue("catalogueId", 113L))
                                    .anySatisfy(item -> assertThat(item)
                                            .hasFieldOrPropertyWithValue("itemId", 13L)
                                            .hasFieldOrPropertyWithValue("catalogueId", 113L));
                        });
            }

            //TODO не работает
            @ParameterizedTest
            @CsvSource({"Air USA", "air USa", "Airr USSA"})
            void should_match_by_catalogue_and_name(String text) {
                var result = searchChain.searchByText(text, pageable);

                assertThat(result)
                        .hasSize(1)
                        .allSatisfy(catalogue -> {
                            assertThat(catalogue)
                                    .hasFieldOrPropertyWithValue("catalogueId", 113L);

                            assertThat(catalogue.getItems())
                                    .hasSize(1)
                                    .anySatisfy(item -> assertThat(item)
                                            .hasFieldOrPropertyWithValue("itemId", 15L)
                                            .hasFieldOrPropertyWithValue("catalogueId", 113L));
                        });
            }

            @ParameterizedTest
            @CsvSource({"USA notebook", "uSA notbook"})
            void should_match_by_catalogue_and_type(String text) {
                var result = searchChain.searchByText(text, pageable);

                assertThat(result)
                        .hasSize(1)
                        .allSatisfy(catalogue -> {
                            assertThat(catalogue)
                                    .hasFieldOrPropertyWithValue("catalogueId", 113L);

                            assertThat(catalogue.getItems())
                                    .hasSize(2)
                                    .anySatisfy(item -> assertThat(item)
                                            .hasFieldOrPropertyWithValue("itemId", 15L)
                                            .hasFieldOrPropertyWithValue("catalogueId", 113L))
                                    .anySatisfy(item -> assertThat(item)
                                            .hasFieldOrPropertyWithValue("itemId", 17L)
                                            .hasFieldOrPropertyWithValue("catalogueId", 113L));
                        });
            }
        }
    }

    @Nested
    class questionable_tests {

    }

    @Nested
    class bug_fix_tests {

    }
}
