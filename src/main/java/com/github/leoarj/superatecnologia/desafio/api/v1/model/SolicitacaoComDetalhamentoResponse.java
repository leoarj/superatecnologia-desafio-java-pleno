package com.github.leoarj.superatecnologia.desafio.api.v1.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SolicitacaoComDetalhamentoResponse extends SolicitacaoResponse {

    @Schema(description = "(Histórico) - Listagem resumida das Solicitações anteriores vinculadas")
    private List<SolicitacaoAnteriorResumoResponse> historico;

}
