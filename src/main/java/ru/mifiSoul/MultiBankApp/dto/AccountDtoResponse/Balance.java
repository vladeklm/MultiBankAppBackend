package ru.mifiSoul.MultiBankApp.dto.AccountDtoResponse;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class Balance {
    @JsonProperty("accountId")
    private String accountId;

    @JsonProperty("type")
    private String type;

    @JsonProperty("dateTime")
    private String dateTime;

    @JsonProperty("amount")
    private Amount amount;

    @JsonProperty("creditDebitIndicator")
    private String creditDebitIndicator;
}
