package ru.mifiSoul.MultiBankApp.dto.AccountDtoResponse;

import lombok.Data;

import java.util.List;

@Data
public class AccountResponseDto {
    private AccountData data;
    private Links links;
    private Meta meta;
}
