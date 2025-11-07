package ru.mifiSoul.MultiBankApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConsentDtoForAccounts {
    private String consentId;
    private String bankUrl;
    private String bankName;
}
