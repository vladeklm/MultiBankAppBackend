package ru.mifiSoul.MultiBankApp.dto.AccountDtoResponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Amount {
    @JsonProperty("amount")
    private String amount;

    @JsonProperty("currency")
    private String currency;

}
