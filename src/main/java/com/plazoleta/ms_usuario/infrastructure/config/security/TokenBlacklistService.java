package com.plazoleta.ms_usuario.infrastructure.config.security;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TokenBlacklistService {

    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();

    public void invalidar(String token) {
        blacklistedTokens.add(token);
    }

    public boolean estaInvalidado(String token) {
        return blacklistedTokens.contains(token);
    }
}
