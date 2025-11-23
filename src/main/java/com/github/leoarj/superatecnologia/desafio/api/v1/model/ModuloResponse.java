package com.github.leoarj.superatecnologia.desafio.api.v1.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ModuloResponse extends ModuloResumoResponse {

    private String descricao;
    private boolean ativo;
    private boolean disponivel;

    private List<DepartamentoResumoResponse> departamentosPermitidos;
    private List<ModuloResumoResponse> modulosIncompativeis;

}
