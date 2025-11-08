package ru.mifiSoul.MultiBankApp.api.controller;


import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import ru.mifiSoul.MultiBankApp.dto.BankCreateRequest;
import ru.mifiSoul.MultiBankApp.service.BankService;

import java.util.List;

@RestController
@RequestMapping("/api/banks")
@AllArgsConstructor
public class BankController {
    private BankService bankService;

    @GetMapping("/")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(bankService.getAll());
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody BankCreateRequest request) {
        return ResponseEntity.ok(bankService.create(request));
    }
}
