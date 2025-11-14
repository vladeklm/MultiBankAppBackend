package ru.mifiSoul.MultiBankApp.dto.ProductDtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductDtoOurResponse {
    private String productId;
    private String productType;
    private String productName;
    private String description;
    private String interestRate;
    private String minAmount;
    private String maxAmount;
    private int termMonths;
    private String bankName;
}
