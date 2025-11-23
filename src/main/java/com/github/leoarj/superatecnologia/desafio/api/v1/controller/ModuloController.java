package com.github.leoarj.superatecnologia.desafio.api.v1.controller;

import com.github.leoarj.superatecnologia.desafio.api.v1.model.ModuloResponse;
import com.github.leoarj.superatecnologia.desafio.api.v1.assembler.ModuloResponseAssembler;
import com.github.leoarj.superatecnologia.desafio.domain.service.CadastroModuloService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/modulos")
public class ModuloController {

    private final CadastroModuloService moduloService;
    private final ModuloResponseAssembler moduloResponseAssembler;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ModuloResponse> listarTodos() {
        return moduloResponseAssembler.toCollectionModel(moduloService.listarComRelacionamentos());
    }

    @GetMapping(path = "/{moduloId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ModuloResponse buscar(@PathVariable Long moduloId) {
        return moduloResponseAssembler.toModel(
                moduloService.buscarOuFalhar(moduloId));
    }

}
