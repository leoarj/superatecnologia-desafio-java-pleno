package com.github.leoarj.superatecnologia.desafio.api.v1.model.Input;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SolicitacaoCancelamentoRequest {

    @Schema(description = "Motivo do Cancelamento de uma Solicitação",
            example = "Não atuo mais neste projeto, solicitando cancelamento.")
    @NotBlank
    @Size(min = 10, max = 200)
    private String motivo;

}
