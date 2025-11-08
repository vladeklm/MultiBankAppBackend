package ru.mifiSoul.MultiBankApp.dto;

import lombok.Data;
import ru.mifiSoul.MultiBankApp.dto.TransactionsDtos.AmountDto;

@Data
public class TransactionDtoResponse {
    private String accountId;
    private String amount;
    private String valueDateTime;
    private String transactionInformation;
    private String creditDebitIndicator;
}
