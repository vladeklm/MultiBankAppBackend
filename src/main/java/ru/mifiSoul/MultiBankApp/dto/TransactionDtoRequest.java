package ru.mifiSoul.MultiBankApp.dto;

import lombok.Data;

@Data
public class TransactionDtoRequest {
    private String accountId;
    private String bankName;
    private String startDateTime;
    private String endDateTime;
    private int pageNumber;
    private int pageSize;
}
