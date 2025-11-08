package ru.mifiSoul.MultiBankApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AccessInfo {
    private String access_token;
    private long expires_in;
}
