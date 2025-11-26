package com.github.leoarj.superatecnologia.desafio.api.v1.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SolicitacaoResumoResponse {

    @Schema(description = "ID da Solicitação", example = "1")
    private Long id;
    @Schema(description = "Protocolo da Solicitação", example = "SOL-20251126-PDAR")
    private String protocolo;

}
