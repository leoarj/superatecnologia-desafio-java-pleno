package com.github.leoarj.superatecnologia.desafio.api.v1.model.Input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SolicitacaoRequest {

    @NotEmpty
    @Size(min = 1, max = 3)
    private List<ModuloIdRequest> modulosSolicitados;

    @NotBlank
    @Size(min = 20, max = 500)
    private String justificativa;

    private boolean urgente;

}
