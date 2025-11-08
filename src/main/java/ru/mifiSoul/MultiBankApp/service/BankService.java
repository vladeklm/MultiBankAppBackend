package ru.mifiSoul.MultiBankApp.service;


import org.springframework.stereotype.Service;
import ru.mifiSoul.MultiBankApp.database.entity.Bank;
import ru.mifiSoul.MultiBankApp.database.repository.BankRepository;
import ru.mifiSoul.MultiBankApp.dto.BankCreateRequest;
import ru.mifiSoul.MultiBankApp.dto.BankItemResponse;

import java.util.Base64;
import java.util.List;

@Service
public class BankService {
    private BankRepository bankRepository;

    public BankService(BankRepository bankRepository) {
        this.bankRepository = bankRepository;
    }

    public List<BankItemResponse> getAll() {

        return bankRepository.findAll().stream()
                .map(bank ->
                {
                    var bankItem = new BankItemResponse();
                    bankItem.setName(bank.getName());
                    if (bank.getPicture() != null) {
                        if (bank.getPicture().length > 0) {
                            bankItem.setPicture(Base64.getEncoder().encodeToString(bank.getPicture()));
                        }
                    }
                    return bankItem;
                })
                .toList();
    }

    public BankCreateRequest create(BankCreateRequest request) {
        if (bankRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Bank with name " + request.getName() + " already exists");
        } else {
            var bank = new Bank();
            bank.setName(request.getName());
            bank.setUrl(request.getUrl());
            bank.setPicture(request.getPicture());

            bankRepository.save(bank);
            return request;
        }
    }
}
