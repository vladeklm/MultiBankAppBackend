package ru.mifiSoul.MultiBankApp.service;

import chat.giga.client.GigaChatClient;
import chat.giga.model.*;
import chat.giga.model.completion.ChatMessage;
import chat.giga.model.completion.CompletionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GigaChatService {

    private final GigaChatClient gigaChatClient;

    public String ask(String question) {
        var request = CompletionRequest.builder()
                .model(ModelName.GIGA_CHAT)
                .message(ChatMessage.builder()
                        .content(question)
                        .role(ChatMessage.Role.SYSTEM)
                        .build())
                .build();

        var response = gigaChatClient.completions(request);

        return response.choices().get(0).message().content();
    }
}