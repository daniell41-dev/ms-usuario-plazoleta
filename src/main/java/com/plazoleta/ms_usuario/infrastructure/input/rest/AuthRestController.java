package com.plazoleta.ms_usuario.infrastructure.input.rest;

import com.plazoleta.ms_usuario.domain.constants.UsuarioConstantes;
import com.plazoleta.ms_usuario.domain.ports.in.IUsuarioServicePort;
import com.plazoleta.ms_usuario.infrastructure.config.security.TokenBlacklistService;
import com.plazoleta.ms_usuario.infrastructure.input.rest.dto.LoginRequestDto;
import com.plazoleta.ms_usuario.infrastructure.input.rest.dto.LoginResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthRestController {

    private final IUsuarioServicePort usuarioServicePort;
    private final TokenBlacklistService tokenBlacklistService;

    public AuthRestController(IUsuarioServicePort usuarioServicePort, TokenBlacklistService tokenBlacklistService) {
        this.usuarioServicePort = usuarioServicePort;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        String token = usuarioServicePort.login(request.getCorreo(), request.getClave());
        return ResponseEntity.ok(new LoginResponseDto(token));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(UsuarioConstantes.BEARER_PREFIX.length());
        tokenBlacklistService.invalidar(token);
        return ResponseEntity.ok().build();
    }

}
