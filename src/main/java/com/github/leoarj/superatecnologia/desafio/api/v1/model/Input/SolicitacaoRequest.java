package com.github.leoarj.superatecnologia.desafio.api.v1.model.Input;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SolicitacaoRequest {

    @Schema(description = "Lista (Multiseleção) com o ID dos Módulos solicitados",
            example = "[1, 2, 3].")
    @NotEmpty
    @Size(min = 1, max = 3)
    @Valid
    private List<ModuloIdRequest> modulosSolicitados;

    @Schema(description = "Justificativa da Solicitação", example = "Preciso de acesso para fechamento mensal da folha.")
    @NotBlank
    @Size(min = 20, max = 500)
    private String justificativa;
    @Schema(description = "Indicador de Urgência da Solicitação", example = "false")
    private boolean urgente;

}
