package ru.mifiSoul.MultiBankApp.dto.AccountDtoResponse;

import lombok.Data;

import java.util.List;

@Data
public class AccountDto {
    private String accountId;
    private String status;
    private String currency;
    private String accountType;
    private String accountSubType;
    private String nickname;
    private String openingDate;
    private List<AccountInfo> account;
}
