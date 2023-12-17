package ru.shop.backend.search.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableElasticsearchRepositories
@EnableScheduling
public class AppConfig {
}
