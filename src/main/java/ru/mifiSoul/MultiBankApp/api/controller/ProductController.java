package ru.mifiSoul.MultiBankApp.api.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mifiSoul.MultiBankApp.service.ProductService;

@RestController
@RequestMapping("/api/products")
@AllArgsConstructor
public class ProductController {
    private ProductService productService;

    @GetMapping("/")
    public ResponseEntity<?> getAllBanksProducts() {
        return ResponseEntity.ok(productService.getAllBankProducts());
    }
}
