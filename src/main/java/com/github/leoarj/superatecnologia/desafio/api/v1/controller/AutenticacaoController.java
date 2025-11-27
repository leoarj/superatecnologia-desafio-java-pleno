package com.github.leoarj.superatecnologia.desafio.api.v1.controller;

import com.github.leoarj.superatecnologia.desafio.api.v1.model.Input.DadosAutenticacaoRequest;
import com.github.leoarj.superatecnologia.desafio.api.v1.model.TokenJWTResponse;
import com.github.leoarj.superatecnologia.desafio.domain.service.AutenticacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AutenticacaoController {

    private final AutenticacaoService autenticacaoService;

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public TokenJWTResponse login(@RequestBody @Valid DadosAutenticacaoRequest dados) {
        String token = autenticacaoService.login(dados.email(), dados.senha());
        return new TokenJWTResponse(token);
    }
}
