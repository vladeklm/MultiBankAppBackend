package ru.mifiSoul.MultiBankApp.dto.AccountDtoResponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Root {
    @JsonProperty("data")
    private BalanceData data;
}
