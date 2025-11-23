package com.github.leoarj.superatecnologia.desafio.api.v1.model;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
public class SolicitacaoResponse extends SolicitacaoResumoResponse {

    private List<ModuloResumoResponse> modulosSolicitados;

    private String status;

    private String justificativa;

    private boolean urgente;

    private OffsetDateTime dataSolicitacao;

    private OffsetDateTime dataExpiracao;

    private String motivoRejeicao;

    private SolicitacaoResumoResponse solicitacaoAnterior;

}
