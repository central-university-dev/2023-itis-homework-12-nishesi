package ru.shop.backend.search.scheduled;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.shop.backend.search.converter.ItemConverter;
import ru.shop.backend.search.repository.ItemJpaRepository;
import ru.shop.backend.search.repository.ItemElasticRepository;

import javax.transaction.Transactional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReindexTask {
    private final ItemJpaRepository itemJpaRepository;
    private final ItemElasticRepository itemElasticRepository;
    private final ItemConverter itemConverter;

    @Scheduled(fixedDelay = 12, timeUnit = TimeUnit.HOURS)
    @Transactional
    public void reindex(){
        log.info("генерация индексов по товарам запущена");
        try (var all = itemJpaRepository.streamAllBy()) {
            all.parallel()
                    .map(itemConverter::toItemElastic)
                    .forEach(itemElasticRepository::save);
        }
        log.info("генерация индексов по товарам закончилась");
    }
}
