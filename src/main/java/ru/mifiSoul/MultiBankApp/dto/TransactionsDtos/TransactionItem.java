package ru.mifiSoul.MultiBankApp.dto.TransactionsDtos;

import lombok.Data;

@Data
public class TransactionItem {
    private String accountId;
    private AmountDto amount;
    private String valueDateTime;
    private String transactionInformation;
    private String creditDebitIndicator;
}
