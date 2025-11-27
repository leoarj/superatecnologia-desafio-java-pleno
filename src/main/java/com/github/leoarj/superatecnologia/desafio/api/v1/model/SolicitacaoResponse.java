package com.github.leoarj.superatecnologia.desafio.api.v1.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
public class SolicitacaoResponse extends SolicitacaoResumoResponse {

    @Schema(description = "Listagem resumida dos Módulos solicitados")
    private List<ModuloResumoResponse> modulosSolicitados;
    @Schema(description = "Status da Solicitação", example = "ATIVA")
    private String status;
    @Schema(description = "Justificativa da Solicitação", example = "Preciso de acesso para fechamento mensal da folha.")
    private String justificativa;
    @Schema(description = "Indicador de Urgência da Solicitação", example = "false")
    private boolean urgente;
    @Schema(description = "Data da Solicitação", example = "2025-11-26T10:15:49.519142916-04:00")
    private OffsetDateTime dataSolicitacao;
    @Schema(description = "Data de Expiração (Vencimento) Solicitação", example = "2026-05-25T10:15:49.721063235-04:00")
    private OffsetDateTime dataExpiracao;
    @Schema(description = "Motivo de rejeiçãop da Solicitação (Se aplicável)", example = "Departamento sem permissão para acessar este módulo")
    private String motivoRejeicao;
    @Schema(description = "Resumo da Solicitação anterior vinculada")
    private SolicitacaoResumoResponse solicitacaoAnterior;
    @Schema(description = "Mensagem referente a criação da Solicitação",
            example = "Solicitação criada com sucesso! Protocolo: SOL-20251126-PDAR. Seus acessos já estão disponíveis!")
    private String mensagem;

}
