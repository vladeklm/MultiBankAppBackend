package ru.mifiSoul.MultiBankApp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.mifiSoul.MultiBankApp.database.entity.Consent;
import ru.mifiSoul.MultiBankApp.database.repository.BankRepository;
import ru.mifiSoul.MultiBankApp.database.repository.ConsentRepository;
import ru.mifiSoul.MultiBankApp.database.repository.UserRepository;
import ru.mifiSoul.MultiBankApp.dto.ConsentCreateRequest;
import ru.mifiSoul.MultiBankApp.dto.ConsentRequest;
import ru.mifiSoul.MultiBankApp.service.util.UrlWrapper;

import java.util.List;
import java.util.Map;

@Service
public class ConsentService {
    private BankAuthService bankAuthService;
    private BankRepository bankRepository;
    private UserRepository userRepository;
    private List<String> permissions;
    private RestTemplate restTemplate = new RestTemplate();
    private ConsentRepository consentRepository;

    @Value("${client_id}")
    private String clientId;

    public ConsentService(BankAuthService bankAuthService,
                          UserRepository userRepository,
                          ConsentRepository consentRepository,
                          BankRepository bankRepository) {
        this.userRepository = userRepository;
        this.bankAuthService = bankAuthService;
        this.consentRepository = consentRepository;
        this.bankRepository = bankRepository;
        this.permissions = List.of("ReadAccountsDetail", "ReadBalances", "ReadTransactionsDetail");
    }

    public String createConsent(ConsentCreateRequest consentCreateRequest) {
        var bankName = consentCreateRequest.getBankName();
        var userDetails = (UserDetails) SecurityContextHolder
                                                            .getContext()
                                                            .getAuthentication()
                                                            .getPrincipal();
        var username = userDetails.getUsername();
        var user = userRepository.findByUsername(username).get();
        var bank = bankRepository.findByName(bankName).get();
        var bearerToken = bankAuthService.getTokenByBankUrl(bank.getName());
        var baseUrl = UrlWrapper.wrap(bank.getUrl());
        String fullUrl = UriComponentsBuilder.fromUriString(baseUrl)
                .pathSegment("account-consents")
                .pathSegment("request")
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + bearerToken);
        headers.add("X-Requesting-Bank", clientId);

        ConsentRequest consentRequest = new ConsentRequest();
        consentRequest.setClientId(username);
        consentRequest.setPermissions(permissions);
        consentRequest.setReason("");
        consentRequest.setRequestingBank(clientId);
        consentRequest.setRequestingBankName(clientId);

        HttpEntity<ConsentRequest> entity = new HttpEntity<>(consentRequest, headers);

        var response = restTemplate.exchange(fullUrl, HttpMethod.POST, entity, Map.class);
        var consentId = response.getBody().get("consent_id");
        var consent = new Consent();
        consent.setUser(user);
        consent.setBank(bank);
        consent.setBankIdentifier(consentId.toString());
        consentRepository.save(consent);
        return consentId.toString();
    }
}
