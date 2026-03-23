package com.plazoleta.ms_usuario.infrastructure.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7); // quita "Bearer "

            if (jwtTokenProvider.validarToken(token)) {
                String correo = jwtTokenProvider.extraerCorreo(token);
                String rol = jwtTokenProvider.extraerRol(token);

                // SimpleGrantedAuthority representa un permiso/rol en Spring Security.
                // Le dice a Spring: "este usuario tiene el rol ROL_X" para que
                // hasAuthority("ROL_X") funcione correctamente en SecurityConfig.
                List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(rol));

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(correo, null, authorities);

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // Continuar con el siguiente filtro de la cadena, siempre
        filterChain.doFilter(request, response);
    }
}
