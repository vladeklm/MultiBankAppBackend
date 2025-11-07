package ru.mifiSoul.MultiBankApp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.mifiSoul.MultiBankApp.database.repository.ConsentRepository;
import ru.mifiSoul.MultiBankApp.database.repository.UserRepository;
import ru.mifiSoul.MultiBankApp.dto.AccountDtoResponse.AccountData;
import ru.mifiSoul.MultiBankApp.dto.AccountDtoResponse.AccountResponseDto;
import ru.mifiSoul.MultiBankApp.dto.AccountDtoResponse.Root;
import ru.mifiSoul.MultiBankApp.dto.AccountResponse;
import ru.mifiSoul.MultiBankApp.dto.ConsentDtoForAccounts;
import ru.mifiSoul.MultiBankApp.dto.ConsentRequest;
import ru.mifiSoul.MultiBankApp.service.util.UrlWrapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class AccountService {
    private ConsentRepository consentRepository;
    private UserRepository userRepository;
    private BankAuthService bankAuthService;

    @Value("${client_id}")
    private String clientId;

    public AccountService(ConsentRepository consentRepository, UserRepository userRepository, BankAuthService bankAuthService) {
        this.consentRepository = consentRepository;
        this.userRepository = userRepository;
        this.bankAuthService = bankAuthService;
    }

    public List<?> getAll() {
        var userDetails = (UserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        var username = userDetails.getUsername();
        var user = userRepository.findByUsername(username).get();
        var consents = consentRepository.findAllByUser(user);
        var consentDtos = consents.stream().map(consent ->
        {
            var consentDto = new ConsentDtoForAccounts(
                    consent.getBankIdentifier(),
                    consent.getBank().getUrl(),
                    consent.getBank().getName()
            );
            return consentDto;
        }).toList();
        var accounts = new ArrayList<AccountResponse>();
        for (var consentDto : consentDtos) {
            accounts.addAll(getAccounts(consentDto));
        }
        return accounts;
    }

    private Collection<? extends AccountResponse> getAccounts(ConsentDtoForAccounts consentDto) {
        var restTemplate = new RestTemplate();
        var userDetails = (UserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        var username = userDetails.getUsername();
        var bearerToken = bankAuthService.getTokenByBankUrl(consentDto.getBankName());

        var baseUrl = UrlWrapper.wrap(consentDto.getBankUrl());


        String fullUrl = UriComponentsBuilder.fromUriString(baseUrl)
                .pathSegment("accounts")
                .queryParam("client_id", username)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + bearerToken);
        headers.add("X-Requesting-Bank", clientId);
        headers.add("X-Consent-Id", consentDto.getConsentId());

        HttpEntity<ConsentRequest> entity = new HttpEntity<>(null, headers);

        var response = restTemplate.exchange(fullUrl, HttpMethod.GET, entity, AccountResponseDto.class);
        var result = response.getBody().getData().getAccount()
                .stream().map(item ->
                {
                    var accountResponseItem = new AccountResponse(
                            item.getAccountId(),
                            item.getAccount().get(0).getIdentification(),
                            item.getAccountType(),"", consentDto.getBankName());
                    return accountResponseItem;
                }).toList();
        for (var item : result) {
            var restTemplate1 = new RestTemplate();
            fullUrl = UriComponentsBuilder.fromUriString(baseUrl)
                    .pathSegment("accounts")
                    .pathSegment(item.getId())
                    .pathSegment("balances")
                    .toUriString();
            var response1 = restTemplate1.exchange(fullUrl, HttpMethod.GET, entity, Root.class);
            var balance = response1.getBody().getData().getBalance().get(0).getAmount().getAmount();
            item.setBalance(balance);
        }
        return result;
    }
}
