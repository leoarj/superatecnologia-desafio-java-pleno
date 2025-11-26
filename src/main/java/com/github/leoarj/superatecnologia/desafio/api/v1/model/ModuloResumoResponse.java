package com.github.leoarj.superatecnologia.desafio.api.v1.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModuloResumoResponse {

    @Schema(description = "ID do Módulo", example = "1")
    private Long id;
    @Schema(description = "Nome do Módulo", example = "Portal do Colaborador")
    private String nome;

}
