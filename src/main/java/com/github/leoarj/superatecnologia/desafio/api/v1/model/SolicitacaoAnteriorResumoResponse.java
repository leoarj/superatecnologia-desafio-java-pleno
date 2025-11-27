package com.github.leoarj.superatecnologia.desafio.api.v1.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class SolicitacaoAnteriorResumoResponse extends SolicitacaoResumoResponse {

    @Schema(description = "Data da Solicitação", example = "2025-11-26T10:15:49.519142916-04:00")
    private OffsetDateTime dataSolicitacao;
    @Schema(description = "Status da Solicitação", example = "ATIVA")
    private String status;
    @Schema(description = "Motivo de rejeiçãop da Solicitação (Se aplicável)", example = "Departamento sem permissão para acessar este módulo")
    private String motivoRejeicao;
    @Schema(description = "Motivo de cancelamento da Solicitação (Se aplicável)", example = "Não preciso mais desse acesso")
    private String motivoCancelamento;

}
