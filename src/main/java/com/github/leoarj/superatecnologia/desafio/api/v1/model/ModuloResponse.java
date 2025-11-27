package com.github.leoarj.superatecnologia.desafio.api.v1.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ModuloResponse extends ModuloResumoResponse {

    @Schema(description = "Descrição do Módulo", example = "Acesso para todos os departamentos")
    private String descricao;
    @Schema(description = "Indicador se o Módulo está ativo", example = "true")
    private boolean ativo;
    @Schema(description = "Indicador se o Módulo está disponível", example = "true")
    private boolean disponivel;

    @Schema(description = "Listagem resumida de Departamentos permitidos para associação com o Módulo")
    private List<DepartamentoResumoResponse> departamentosPermitidos;
    @Schema(description = "Listagem resumida de outros Módulos incompatíveis para associação com o Módulo")
    private List<ModuloResumoResponse> modulosIncompativeis;

}
