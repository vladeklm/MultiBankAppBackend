package ru.mifiSoul.MultiBankApp.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.mifiSoul.MultiBankApp.dto.ChatAskDto;
import ru.mifiSoul.MultiBankApp.service.GigaChatService;

@RestController
@RequestMapping("/api/gigachat")
@RequiredArgsConstructor
public class GigaChatController {

    private final GigaChatService gigaChatService;

    @PostMapping("/ask")
    public String ask(@RequestBody ChatAskDto q) {
        return gigaChatService.ask(q);
    }
}