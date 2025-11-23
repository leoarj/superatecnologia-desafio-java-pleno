package com.github.leoarj.superatecnologia.desafio.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class Departamento {

    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false, unique = true, length = 60)
    private String nome;

    @Column(length = 255)
    private String descricao;

    @Column(nullable = false)
    private Boolean ativo = true;

}
