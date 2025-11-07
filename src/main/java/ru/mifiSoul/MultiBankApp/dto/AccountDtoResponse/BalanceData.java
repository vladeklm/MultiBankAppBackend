package ru.mifiSoul.MultiBankApp.dto.AccountDtoResponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class BalanceData {
    @JsonProperty("balance")
    private List<Balance> balance;
}
