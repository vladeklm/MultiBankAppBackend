package ru.mifiSoul.MultiBankApp.dto.TransactionsDtos;

import lombok.Data;

import java.util.List;

@Data
public class TransactionData {
    private List<TransactionItem> transaction;
}
