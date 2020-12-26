package com.shaidulin.rgbexample.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "color")
public class Color {

    @Id
    @EqualsAndHashCode.Exclude
    private String id;

    private Integer red;

    private Integer green;

    private Integer blue;
}
