package com.plazoleta.ms_usuario.infrastructure.input.rest.dto;

import lombok.Getter;

@Getter
public class LoginResponseDto {

    private String token;

    public LoginResponseDto(String token) {
        this.token = token;
    }
}
