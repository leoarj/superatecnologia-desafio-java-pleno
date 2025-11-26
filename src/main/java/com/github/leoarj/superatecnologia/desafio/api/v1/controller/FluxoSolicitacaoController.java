package com.github.leoarj.superatecnologia.desafio.api.v1.controller;

import com.github.leoarj.superatecnologia.desafio.api.v1.assembler.SolicitacaoComDetalhamentoAssembler;
import com.github.leoarj.superatecnologia.desafio.api.v1.assembler.SolicitacaoResponseAssembler;
import com.github.leoarj.superatecnologia.desafio.api.v1.disassembler.SolicitacaoRequestDisassembler;
import com.github.leoarj.superatecnologia.desafio.api.v1.model.Input.SolicitacaoCancelamentoRequest;
import com.github.leoarj.superatecnologia.desafio.api.v1.model.Input.SolicitacaoRequest;
import com.github.leoarj.superatecnologia.desafio.api.v1.model.SolicitacaoComDetalhamentoResponse;
import com.github.leoarj.superatecnologia.desafio.api.v1.model.SolicitacaoResponse;
import com.github.leoarj.superatecnologia.desafio.api.v1.openapi.FluxoSolicitacaoControllerOpenApi;
import com.github.leoarj.superatecnologia.desafio.domain.filter.SolicitacaoFilter;
import com.github.leoarj.superatecnologia.desafio.domain.model.Solicitacao;
import com.github.leoarj.superatecnologia.desafio.domain.model.Usuario;
import com.github.leoarj.superatecnologia.desafio.domain.service.FluxoSolicitacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/usuarios/{usuarioId}/solicitacoes")
@RequiredArgsConstructor
public class FluxoSolicitacaoController implements FluxoSolicitacaoControllerOpenApi {

    private final FluxoSolicitacaoService fluxoSolicitacaoService;
    private final SolicitacaoRequestDisassembler solicitacaoRequestDisassembler;
    private final SolicitacaoResponseAssembler solicitacaoResponseAssembler;

    private final SolicitacaoComDetalhamentoAssembler solicitacaoComDetalhamentoAssembler;

    @GetMapping
    public Page<SolicitacaoResponse> listar(@PathVariable Long usuarioId,
                                            SolicitacaoFilter filtro,
                                            @PageableDefault(size = 10, sort = "dataSolicitacao",
                                                    direction = Sort.Direction.DESC) Pageable pageable) {

        validarUsuarioLogado(usuarioId);

        Page<Solicitacao> solicitacoesPage = fluxoSolicitacaoService.listar(usuarioId, filtro, pageable);


        //return solicitacaoResponseAssembler.toCollectionModel();
        return solicitacoesPage.map(solicitacaoResponseAssembler::toModel);
    }

    @GetMapping("/{solicitacaoId}")
    public SolicitacaoComDetalhamentoResponse buscar(@PathVariable Long usuarioId,
                                                     @PathVariable Long solicitacaoId) {
        validarUsuarioLogado(usuarioId);

        return solicitacaoComDetalhamentoAssembler.toModel(fluxoSolicitacaoService.buscarOuFalhar(usuarioId, solicitacaoId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SolicitacaoResponse solicitar(@PathVariable Long usuarioId,
                                 @RequestBody @Valid SolicitacaoRequest solicitacaoRequest) {

        validarUsuarioLogado(usuarioId);

        Solicitacao solicitacao = solicitacaoRequestDisassembler.toDomainObject(solicitacaoRequest);
        return solicitacaoResponseAssembler.toModel(fluxoSolicitacaoService.solicitar(usuarioId, solicitacao));
    }

    @PostMapping("/{solicitacaoId}/renovacao")
    @ResponseStatus(HttpStatus.CREATED)
    public SolicitacaoResponse renovar(@PathVariable Long usuarioId,
                               @PathVariable Long solicitacaoId) {

        validarUsuarioLogado(usuarioId);

        return solicitacaoResponseAssembler.toModel(fluxoSolicitacaoService.renovar(usuarioId, solicitacaoId));
    }

    @PutMapping("/{solicitacaoId}/cancelamento")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelar(@PathVariable Long usuarioId,
                        @PathVariable Long solicitacaoId,
                        @RequestBody @Valid SolicitacaoCancelamentoRequest solicitacaoCancelamentoRequest) {

        validarUsuarioLogado(usuarioId);

        fluxoSolicitacaoService.cancelar(usuarioId, solicitacaoId, solicitacaoCancelamentoRequest.getMotivo());
    }

    // Método auxiliar para validar a regra
    private void validarUsuarioLogado(Long usuarioIdDaUrl) {
        // Recupera o usuário que foi setado no SecurityFilter
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var usuarioLogado = (Usuario) authentication.getPrincipal();

        // Se o ID da URL for diferente do ID do Token -> ERRO 403
        if (!usuarioLogado.getId().equals(usuarioIdDaUrl)) {
            throw new AccessDeniedException("Você não tem permissão para acessar dados de outro usuário.");
        }
    }
}