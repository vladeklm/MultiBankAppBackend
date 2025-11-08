package ru.mifiSoul.MultiBankApp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ConsentRequest {

    @JsonProperty("client_id")
    private String clientId;

    @JsonProperty("permissions")
    private List<String> permissions;

    @JsonProperty("requesting_bank")
    private String requestingBank;

    @JsonProperty("requesting_bank_name")
    private String requestingBankName;

    @JsonProperty("reason")
    private String reason;
}
