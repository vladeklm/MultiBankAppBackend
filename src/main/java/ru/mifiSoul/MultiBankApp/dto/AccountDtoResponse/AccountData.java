package ru.mifiSoul.MultiBankApp.dto.AccountDtoResponse;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
public class AccountData {
    private List<AccountDto> account;
}
