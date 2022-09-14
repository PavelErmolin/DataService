package com.example.redishamster.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class HamsterItem {
    @Id
    private Integer id;

    private String title;
    private String category;
    private String price;
    private Map<String, String> filter_features;
    private Map<String, String> non_filter_features;
    private String description;
    private String image;
}
