package ru.mifiSoul.MultiBankApp.api.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mifiSoul.MultiBankApp.dto.ConsentCreateRequest;
import ru.mifiSoul.MultiBankApp.service.ConsentService;

@RestController
@RequestMapping("/api/consents")
@AllArgsConstructor
public class ConsentController {
    private ConsentService consentService;

    @PostMapping("/create")
    public ResponseEntity<?> createConsent(@RequestBody ConsentCreateRequest consentCreateRequest) {
        return ResponseEntity.ok(consentService.createConsent(consentCreateRequest));
    }
}
