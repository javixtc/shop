package com.javixtc.products_service.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ProductRequest {

    private String sku;
    private String name;
    private String description;
    private Double price;
    private Boolean status;
}
