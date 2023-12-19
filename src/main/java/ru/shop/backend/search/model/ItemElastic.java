package ru.shop.backend.search.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(indexName = "item")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemElastic {
    @Field(type = FieldType.Text)
    private String name;
    @Field(type = FieldType.Text)
    @JsonIgnore
    private String fulltext;
    @Id
    @Field(name = "item_id", type = FieldType.Integer)
    private Long itemId;
    @Field(name = "catalogue_id", type = FieldType.Integer)
    @JsonIgnore
    private Long catalogueId;
    @Field(type = FieldType.Text)
    @JsonIgnore
    private String catalogue;
    @Field(type = FieldType.Text)
    private String brand;
    @Field(type = FieldType.Text)
    private String type;

    @Field(type = FieldType.Text)
    private String description;
}