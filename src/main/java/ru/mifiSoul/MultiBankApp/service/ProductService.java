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
import ru.mifiSoul.MultiBankApp.database.repository.ConsentRepository;
import ru.mifiSoul.MultiBankApp.database.repository.UserRepository;
import ru.mifiSoul.MultiBankApp.dto.AccountDtoResponse.AccountResponseDto;
import ru.mifiSoul.MultiBankApp.dto.AccountResponse;
import ru.mifiSoul.MultiBankApp.dto.ConsentDtoForAccounts;
import ru.mifiSoul.MultiBankApp.dto.ConsentRequest;
import ru.mifiSoul.MultiBankApp.dto.ProductDtos.ProductBankApiResponse;
import ru.mifiSoul.MultiBankApp.dto.ProductDtos.ProductDto;
import ru.mifiSoul.MultiBankApp.dto.ProductDtos.ProductDtoOurResponse;
import ru.mifiSoul.MultiBankApp.dto.ProductDtos.ProductItem;
import ru.mifiSoul.MultiBankApp.service.util.UrlWrapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class ProductService {
    private BankAuthService bankAuthService;
    private ConsentRepository consentRepository;
    private UserRepository userRepository;

    public ProductService(BankAuthService bankAuthService, ConsentRepository consentRepository, UserRepository userRepository) {
        this.bankAuthService = bankAuthService;
        this.consentRepository = consentRepository;
        this.userRepository = userRepository;
    }

    public List<ProductDtoOurResponse> getAllBankProducts() {
        var restTemplate = new RestTemplate();
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
        var allProducts = new ArrayList<ProductDtoOurResponse>();
        for (var consentDto : consentDtos) {
            allProducts.addAll(getProducts(consentDto));
        }
        return allProducts;
    }

    private Collection<? extends ProductDtoOurResponse> getProducts(ConsentDtoForAccounts consentDto) {
        var restTemplate = new RestTemplate();
        var bearerToken = bankAuthService.getTokenByBankUrl(consentDto.getBankName());

        var baseUrl = UrlWrapper.wrap(consentDto.getBankUrl());

        String fullUrl = UriComponentsBuilder.fromUriString(baseUrl)
                .pathSegment("products")
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + bearerToken);

        HttpEntity<ConsentRequest> entity = new HttpEntity<>(null, headers);

        var response = restTemplate.exchange(fullUrl, HttpMethod.GET, entity, ProductBankApiResponse.class);
        var result = response.getBody().getData().getProduct()
                .stream().map(item ->
                {
                    var accountResponseItem = new ProductDtoOurResponse(
                            item.getProductId(),
                            item.getProductType(),
                            item.getProductName(),
                            item.getDescription(),
                            item.getInterestRate(),
                            item.getMinAmount(),
                            item.getMaxAmount(),
                            item.getTermMonths(),
                            consentDto.getBankName());
                    return accountResponseItem;
                }).toList();
        return result;
    }
}
