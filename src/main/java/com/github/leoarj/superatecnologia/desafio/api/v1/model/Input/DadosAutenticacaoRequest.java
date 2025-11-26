package com.github.leoarj.superatecnologia.desafio.api.v1.model.Input;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// Imutabilidade
public record DadosAutenticacaoRequest(
        @NotBlank @Email String email,
        @NotBlank String senha
) {}