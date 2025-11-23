package com.github.leoarj.superatecnologia.desafio.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class Modulo {

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

    @Column(nullable = false)
    private Boolean disponivel = true;

    // TODO: Criar relacionamento com Módulos incompatíveis
    // DONE: Criar relacionamento com Departamentos
    @ManyToMany
    @JoinTable(name = "departamento_modulo",
        joinColumns = @JoinColumn(name = "modulo_id"),
        inverseJoinColumns = @JoinColumn(name = "departamento_id"))
    private Set<Departamento> departamentosPermitidos = new HashSet<>();
}
