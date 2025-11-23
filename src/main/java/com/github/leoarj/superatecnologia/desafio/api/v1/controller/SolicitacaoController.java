package com.github.leoarj.superatecnologia.desafio.api.v1.controller;

import com.github.leoarj.superatecnologia.desafio.api.v1.assembler.SolicitacaoResponseAssembler;
import com.github.leoarj.superatecnologia.desafio.api.v1.disassembler.SolicitacaoRequestDisassembler;
import com.github.leoarj.superatecnologia.desafio.api.v1.model.Input.SolicitacaoRequest;
import com.github.leoarj.superatecnologia.desafio.api.v1.model.SolicitacaoResponse;
import com.github.leoarj.superatecnologia.desafio.domain.model.Solicitacao;
import com.github.leoarj.superatecnologia.desafio.domain.repository.SolicitacaoRepository;
import com.github.leoarj.superatecnologia.desafio.domain.service.CadastroSolicitacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/usuarios/{usuarioId}/solicitacoes")
@RequiredArgsConstructor
public class SolicitacaoController {

    private final CadastroSolicitacaoService cadastroSolicitacaoService;
    private final SolicitacaoRepository solicitacaoRepository;
    private final SolicitacaoRequestDisassembler solicitacaoRequestDisassembler;
    private final SolicitacaoResponseAssembler solicitacaoResponseAssembler;

    @GetMapping
    public Page<SolicitacaoResponse> listar(@PathVariable Long usuarioId,
                                            @PageableDefault(size = 10) Pageable pageable) {

        Page<Solicitacao> solicitacoesPage = cadastroSolicitacaoService.listar(usuarioId, pageable);


        //return solicitacaoResponseAssembler.toCollectionModel();
        return solicitacoesPage.map(solicitacaoResponseAssembler::toModel);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SolicitacaoResponse solicitar(@PathVariable Long usuarioId,
                                 @RequestBody @Valid SolicitacaoRequest solicitacaoRequest) {
        
        Solicitacao solicitacao = solicitacaoRequestDisassembler.toDomainObject(solicitacaoRequest);
        return solicitacaoResponseAssembler.toModel(cadastroSolicitacaoService.solicitar(usuarioId, solicitacao));
    }

    @PostMapping("/{solicitacaoId}/renovacao")
    @ResponseStatus(HttpStatus.CREATED)
    public SolicitacaoResponse renovar(@PathVariable Long usuarioId,
                               @PathVariable Long solicitacaoId) {
        
        return solicitacaoResponseAssembler.toModel(cadastroSolicitacaoService.renovar(usuarioId, solicitacaoId));
    }
}