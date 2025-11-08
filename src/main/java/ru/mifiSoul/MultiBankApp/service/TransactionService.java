package ru.mifiSoul.MultiBankApp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.mifiSoul.MultiBankApp.database.repository.BankRepository;
import ru.mifiSoul.MultiBankApp.database.repository.ConsentRepository;
import ru.mifiSoul.MultiBankApp.database.repository.UserRepository;
import ru.mifiSoul.MultiBankApp.dto.AccountDtoResponse.AccountResponseDto;
import ru.mifiSoul.MultiBankApp.dto.ConsentRequest;
import ru.mifiSoul.MultiBankApp.dto.TransactionDtoRequest;
import ru.mifiSoul.MultiBankApp.dto.TransactionDtoResponse;
import ru.mifiSoul.MultiBankApp.dto.TransactionsDtos.TransactionRoot;
import ru.mifiSoul.MultiBankApp.service.util.UrlWrapper;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class TransactionService {
    private ConsentRepository consentRepository;
    private UserRepository userRepository;
    private BankRepository bankRepository;
    private BankAuthService bankAuthService;

    @Value("${client_id}")
    private String clientId;

    public TransactionService(ConsentRepository consentRepository, UserRepository userRepository,
                              BankAuthService bankAuthService ,BankRepository bankRepository) {
        this.consentRepository = consentRepository;
        this.userRepository = userRepository;
        this.bankRepository = bankRepository;
        this.bankAuthService = bankAuthService;
    }

    public List<TransactionDtoResponse> getAll(TransactionDtoRequest request) {
        var restTemplate = new RestTemplate();
        var userDetails = (UserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        var username = userDetails.getUsername();
        var user = userRepository.findByUsername(username).get();
        var bank = bankRepository.findByName(request.getBankName()).get();
        var consent = consentRepository.findByBankAndUser(bank, user).get();
        var bearerToken = bankAuthService.getTokenByBankUrl(request.getBankName());

        var baseUrl = UrlWrapper.wrap(bank.getUrl());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MM yyyy HH:mm", Locale.forLanguageTag("ru-RU"));
        LocalDateTime  localDateTimeStart = LocalDateTime.parse(request.getStartDateTime(), formatter);
        ZonedDateTime zdt = localDateTimeStart.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneOffset.UTC);
        String isoFormattedDateStart = zdt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        LocalDateTime  localDateTimeend = LocalDateTime.parse(request.getStartDateTime(), formatter);
        ZonedDateTime zdtend = localDateTimeStart.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneOffset.UTC);
        String isoFormattedDateEnd = zdtend.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        String fullUrl = UriComponentsBuilder.fromUriString(baseUrl)
                .pathSegment("accounts")
                .pathSegment(request.getAccountId())
                .pathSegment("transactions")
                .queryParam("from_booking_date_time", isoFormattedDateStart)
                .queryParam("to_booking_date_time", isoFormattedDateEnd)
                .queryParam("page", request.getPageNumber())
                .queryParam("limit", request.getPageSize())
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + bearerToken);
        headers.add("X-Requesting-Bank", clientId);
        headers.add("X-Consent-Id", consent.getBankIdentifier());

        HttpEntity<ConsentRequest> entity = new HttpEntity<>(null, headers);

        var response = restTemplate.exchange(fullUrl, HttpMethod.GET, entity, TransactionRoot.class);
        var result = response.getBody().getData().getTransaction().stream()
                .map(item ->
                {
                    var responseItem  = new TransactionDtoResponse();
                    responseItem.setAmount(item.getAmount().getAmount());
                    responseItem.setAccountId(item.getAccountId());
                    responseItem.setCreditDebitIndicator(item.getCreditDebitIndicator());
                    responseItem.setTransactionInformation(item.getTransactionInformation());
                    responseItem.setValueDateTime(item.getValueDateTime());
                    return responseItem;
                }).toList();
        return result;
    }
}
