package ru.mifiSoul.MultiBankApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccountResponse {
    private String number;
    private String type;
    private double balance;
}
