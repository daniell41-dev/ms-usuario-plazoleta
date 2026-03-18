package com.plazoleta.ms_usuario.infrastructure.input.rest.dto;

 /**
     * DTO de RESPUESTA para el endpoint GET /usuarios/{id}/rol.
     *
     * ¿Por qué existe este DTO y no devolvemos el enum Rol directamente?
     * Porque el dominio (enum Rol) no debe acoplarse al contrato HTTP.
  }  * Si mañana renombramos el enum o cambiamos su estructura, este DTO
     * actúa como escudo: absorbemos el cambio aquí sin romper la API.
    *
    * El campo "rol" es String porque JSON no tiene enums — se serializa
    * como texto plano (ej: "PROPIETARIO").
    */

public class UsuarioRolResponseDto {
    
    private String rol;

    public UsuarioRolResponseDto(String rol){
        this.rol = rol;
    }

    public String getRol(){
        return rol;
    }


}
