package com.github.leoarj.superatecnologia.desafio.domain.filter;

import com.github.leoarj.superatecnologia.desafio.domain.model.StatusSolicitacao;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class SolicitacaoFilter {

    private String protocolo;
    private String justificativa; // busca por 'like'
    private StatusSolicitacao status;
    private Boolean urgente;

    // Usando formato somente data para facilitar o filtro
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataCriacaoInicio;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataCriacaoFim;
}