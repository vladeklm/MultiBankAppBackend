package ru.mifiSoul.MultiBankApp.dto;

import jakarta.persistence.*;
import lombok.Data;

@Data
public class BankCreateRequest {

    private String name;

    private String url;

    private byte[] picture;
}
