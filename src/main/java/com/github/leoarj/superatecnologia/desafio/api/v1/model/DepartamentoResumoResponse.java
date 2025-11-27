package com.github.leoarj.superatecnologia.desafio.api.v1.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DepartamentoResumoResponse {

    @Schema(description = "ID do Departamento", example = "1")
    private Long id;
    @Schema(description = "Nome do Departamento", example = "2")
    private String nome;

}
