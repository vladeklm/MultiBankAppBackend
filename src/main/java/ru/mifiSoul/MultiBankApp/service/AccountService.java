package ru.mifiSoul.MultiBankApp.service;

import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.mifiSoul.MultiBankApp.database.entity.Bank;
import ru.mifiSoul.MultiBankApp.database.repository.ConsentRepository;
import ru.mifiSoul.MultiBankApp.database.repository.UserRepository;
import ru.mifiSoul.MultiBankApp.dto.AccountResponse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@AllArgsConstructor
public class AccountService {
    private ConsentRepository consentRepository;
    private UserRepository userRepository;
    private BankAuthService bankAuthService;

    public List<?> getAll() {
        var userDetails = (UserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        var username = userDetails.getUsername();
        var user = userRepository.findByUsername(username).get();
        var consents = consentRepository.findAllByUser(user);
        var banks = consents.stream().map(consent -> consent.getBank()).toList();
        var accounts = new ArrayList<AccountResponse>();
        for (var bank : banks) {
            accounts.addAll(getAccounts(bank));
        }
    }

    private Collection<? extends AccountResponse> getAccounts(Bank bank) {

    }
}
