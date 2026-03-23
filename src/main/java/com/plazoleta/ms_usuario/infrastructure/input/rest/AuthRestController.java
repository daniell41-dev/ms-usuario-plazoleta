package com.plazoleta.ms_usuario.infrastructure.input.rest;

import com.plazoleta.ms_usuario.domain.ports.in.IUsuarioServicePort;
import com.plazoleta.ms_usuario.infrastructure.input.rest.dto.LoginRequestDto;
import com.plazoleta.ms_usuario.infrastructure.input.rest.dto.LoginResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthRestController {

    private final IUsuarioServicePort usuarioServicePort;
    public AuthRestController(IUsuarioServicePort usuarioServicePort) {
        this.usuarioServicePort = usuarioServicePort;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        String token = usuarioServicePort.login(request.getCorreo(), request.getClave());
        return ResponseEntity.ok(new LoginResponseDto(token));
    }

}
