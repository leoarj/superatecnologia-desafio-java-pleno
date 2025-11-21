package com.github.leoarj.superatecnologia.desafio.controller;

import com.github.leoarj.superatecnologia.desafio.api.v1.model.ModuloResponse;
import com.github.leoarj.superatecnologia.desafio.assembler.ModuloResponseAssembler;
import com.github.leoarj.superatecnologia.desafio.domain.service.ModuloService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/modulos")
public class ModuloController {

    private final ModuloService moduloService;
    private final ModuloResponseAssembler moduloResponseAssembler;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ModuloResponse> listarTodos() {
        return moduloResponseAssembler.toCollectionModel(moduloService.listarTodosModulos());
    }


}
