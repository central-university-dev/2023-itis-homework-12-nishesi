package ru.shop.backend.search.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "catalogue")
public class CatalogueEntity {
    @Id
    @Column(name = "catalogue_id")
    private Long id;
    private String name;
    @Column(name = "parent_id")
    private Long parentId;
    @Column(name = "realcatname")
    private String url;
    private String image;
}
