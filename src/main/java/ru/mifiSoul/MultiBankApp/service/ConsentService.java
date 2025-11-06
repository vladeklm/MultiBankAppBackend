package ru.mifiSoul.MultiBankApp.service;

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
import ru.mifiSoul.MultiBankApp.dto.ConsentRequest;

import java.util.List;
import java.util.Map;

@Service
public class ConsentService {
    private BankAuthService bankAuthService;
    private BankRepository bankRepository;
    private UserRepository userRepository;
    private List<String> permissions;
    private String consentEndPoint = "/account-consents/request";
    private OurBankService ourBankService;
    private RestTemplate restTemplate = new RestTemplate();
    private ConsentRepository consentRepository;

    public ConsentService(BankAuthService bankAuthService,
                          UserRepository userRepository,
                          OurBankService ourBankService,
                          ConsentRepository consentRepository) {
        this.userRepository = userRepository;
        this.bankAuthService = bankAuthService;
        this.ourBankService = ourBankService;
        this.permissions = List.of("ReadAccountsDetail", "ReadBalances", "ReadTransactionsDetail");
    }

    public String createConsent(String bankName) {
        var userDetails = (UserDetails) SecurityContextHolder
                                                            .getContext()
                                                            .getAuthentication()
                                                            .getPrincipal();
        var username = userDetails.getUsername();
        var user = userRepository.findByUsername(username).get();
        var bank = bankRepository.findByName(bankName).get();
        var bearerToken = bankAuthService.getTokenByBankUrl(bank.getUrl());
        var baseUrl = "https://" + bank.getUrl() + consentEndPoint;
        String fullUrl = UriComponentsBuilder.fromUriString(baseUrl)
                .pathSegment("consent-api/v1/consents")
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON); // тип контента
        headers.set("Authorization", "Bearer " + bearerToken); // токен Bearer
        headers.add("X-Requesting-Bank", ourBankService.getId()); // специальный заголовок

        ConsentRequest consentRequest = new ConsentRequest();
        consentRequest.setClientId("username");
        consentRequest.setPermissions(permissions);
        consentRequest.setReason("");
        consentRequest.setRequestingBank(ourBankService.getId());
        consentRequest.setRequestingBankName(ourBankService.getId());

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
