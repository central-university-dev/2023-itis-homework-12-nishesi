package ru.shop.backend.search.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.shop.backend.search.model.CatalogueEntity;
import ru.shop.backend.search.model.CatalogueWithParent;

import java.util.List;

public interface CatalogueJpaRepository extends JpaRepository<CatalogueEntity, Long> {

    @Query(value = "select distinct       " +
            "c.name,                      " +
            "cp.name as parentName,      " +
            "c.realcatname as url ,       " +
            "cp.realcatname as parentUrl," +
            "c.image                      " +
            "from catalogue as c join catalogue cp" +
            "   on cp.catalogue_id  = c.parent_id " +
            "where c.catalogue_id in :ids", nativeQuery = true)
    List<CatalogueWithParent> findAllWithParentByIdIn(List<Long> ids);
}
