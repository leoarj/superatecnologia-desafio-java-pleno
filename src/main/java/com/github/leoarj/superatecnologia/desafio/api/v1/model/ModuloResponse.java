package com.github.leoarj.superatecnologia.desafio.api.v1.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModuloResponse {

    private Long id;
    private String nome;
    private String descricao;
    private boolean ativo;
    private boolean disponivel;

}
