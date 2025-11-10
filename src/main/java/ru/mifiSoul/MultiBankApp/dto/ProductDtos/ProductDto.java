package ru.mifiSoul.MultiBankApp.dto.ProductDtos;

import lombok.Data;

import java.util.List;

@Data
public class ProductDto {
    private List<ProductItem> product;
}
