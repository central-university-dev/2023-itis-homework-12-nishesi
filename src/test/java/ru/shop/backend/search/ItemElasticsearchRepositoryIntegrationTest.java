package ru.shop.backend.search;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.shop.backend.search.model.ItemElastic;
import ru.shop.backend.search.repository.ItemElasticRepository;
import ru.shop.backend.search.scheduled.ReindexTask;
import ru.shop.backend.search.util.SimpleElasticsearchContainer;
import ru.shop.backend.search.util.SimplePostgresContainer;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

// if run any test case separately, make sure, that scheduled task is done
@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SearchApplication.class)
@ContextConfiguration(initializers = {ItemElasticsearchRepositoryIntegrationTest.TestContextInitializer.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ItemElasticsearchRepositoryIntegrationTest {

    @Container
    static final ElasticsearchContainer elastic = new SimpleElasticsearchContainer();

    @Container
    static final PostgreSQLContainer<?> postgres = new SimplePostgresContainer()
            .withInitScript("ItemRepository-test-schema.sql");

    @Autowired
    ElasticsearchRestTemplate template;

    @Autowired
    ItemElasticRepository itemElasticRepository;

    @Autowired
    ReindexTask reindexTask;


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
    @Order(0)
    void reindex() {
        reindexTask.reindex();
    }

    static class TestContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + postgres.getJdbcUrl(),
                    "spring.datasource.username=" + postgres.getUsername(),
                    "spring.datasource.password=" + postgres.getPassword(),
                    "spring.elasticsearch.rest.uris=" + elastic.getHttpHostAddress(),
                    "spring.datasource.driver-class-name=org.postgresql.Driver",
                    "spring.elasticsearch.username=",
                    "spring.elasticsearch.password="
//                    "server.port=8080"
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

    @Test
    void should_return_first_result() {
        assertThatNoException()
                .isThrownBy(() -> {
                    Optional<ItemElastic> result = itemElasticRepository.findByItemId(1L);
                    assertThat(result)
                            .isPresent()
                            .get()
                            .hasFieldOrPropertyWithValue("itemId", 1L);
                });
    }
}
