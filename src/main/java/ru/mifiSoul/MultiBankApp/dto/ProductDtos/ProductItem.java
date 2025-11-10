package ru.mifiSoul.MultiBankApp.dto.ProductDtos;

import lombok.Data;

@Data
public class ProductItem {
    private String productId;
    private String productType;
    private String productName;
    private String description;
    private String interestRate;
    private String minAmount;
    private String maxAmount;
    private int termMonths;
}
