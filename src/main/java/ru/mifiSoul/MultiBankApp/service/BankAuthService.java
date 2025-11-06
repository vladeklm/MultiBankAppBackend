package ru.mifiSoul.MultiBankApp.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.mifiSoul.MultiBankApp.database.repository.BankRepository;
import ru.mifiSoul.MultiBankApp.dto.AccessInfo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BankAuthService {
    @Value("${client_id}")
    private String clientId;

    @Value("${client_secret}")
    private String clientSecret;

    private RestTemplate restTemplate;
    private volatile String currentAccessToken;
    private volatile Long nextRefreshTimeMillis;
    private BankRepository bankRepository;

    private ConcurrentHashMap<String, AccessInfo> tokenStorage;

    public BankAuthService(BankRepository bankRepository) {
        this.bankRepository = bankRepository;
        restTemplate = new RestTemplate();
        tokenStorage = new ConcurrentHashMap<>();
    }

    public String getTokenByBankUrl(String bankUrl) {
        if (tokenStorage.containsKey(bankUrl)) {
            return tokenStorage.get(bankUrl).getAccess_token();
        } else {
            return null;
        }
    }

    public void initializeAllBanks() {
        var banks = bankRepository.findAll().stream().map(bank -> bank.getUrl()).toList();
        for (String bankUrl : banks) {
            updateTokenForBank(bankUrl);
        }
    }

    public void updateTokenForBank(String bankUrl) {
        try {
            var response = sendPostRequest(bankUrl);
            if (response.getStatusCode().is2xxSuccessful()) {
                handleResponseAndScheduleNextUpdate(bankUrl, response.getBody());
            }
        } catch (Exception ex) {
            var x = 1;
        }
    }

    private void handleResponseAndScheduleNextUpdate(String bankUrl, Map<String, ?> responseBody) {
        String accessToken = (String) responseBody.get("access_token");
        Integer expiresInSeconds = (Integer) responseBody.get("expires_in");

        if (tokenStorage.containsKey(bankUrl)) {
            tokenStorage.replace(bankUrl, new AccessInfo(accessToken, expiresInSeconds));
        } else {
            tokenStorage.put(bankUrl, new AccessInfo(accessToken, expiresInSeconds));
        }

    }

    private ResponseEntity<Map> sendPostRequest(String requestUrl) {
        var baseUrl = "https://" + requestUrl;
        String fullUrl = UriComponentsBuilder.fromUriString(baseUrl)
                .pathSegment("auth", "bank-token") // добавляем конечный путь
                .queryParam("client_id", clientId)
                .queryParam("client_secret", clientSecret)
                .toUriString();

        // Передача пустого тела запроса
        HttpEntity<?> emptyEntity = new HttpEntity<>(null, new HttpHeaders());

        return restTemplate.exchange(fullUrl, HttpMethod.POST, emptyEntity, Map.class);
    }
}
