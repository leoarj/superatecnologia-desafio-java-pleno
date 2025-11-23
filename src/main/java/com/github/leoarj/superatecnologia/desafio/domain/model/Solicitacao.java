package com.github.leoarj.superatecnologia.desafio.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class Solicitacao {

    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // Mapeamento simplificado com a mudança da V007 (Associativa em vez de entidade filha)
    @ManyToMany
    @JoinTable(name = "solicitacao_modulo",
            joinColumns = @JoinColumn(name = "solicitacao_id"),
            inverseJoinColumns = @JoinColumn(name = "modulo_id"))
    private Set<Modulo> modulosSolicitados = new HashSet<>();

    @Column(nullable = false, length = 500)
    private String justificativa;

    private Boolean urgente = false;

    // @PrePersist ou no Service
    @Column(nullable = false, unique = true)
    private String protocolo;

    @Column(nullable = false)
    private OffsetDateTime dataSolicitacao = OffsetDateTime.now();

    private OffsetDateTime dataExpiracao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusSolicitacao status;

    private String motivoRejeicao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitacao_anterior_id")
    private Solicitacao solicitacaoAnterior;

    //  DDD: Métodos utilitários para adicionar módulos
    public void adicionarModulo(Modulo modulo) {
        this.modulosSolicitados.add(modulo);
    }
}
