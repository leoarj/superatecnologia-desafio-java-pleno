package com.github.leoarj.superatecnologia.desafio.domain.exception;

public class SolicitacaoNaoEncontradaException extends EntidadeNaoEncontradaException {

    private static final long serialVersionUID = 1L;

    public SolicitacaoNaoEncontradaException(String message) {
        super(message);
    }

    public SolicitacaoNaoEncontradaException(Long moduloId) {
        this(String.format("Não existe um cadastro de solicitação com código %d", moduloId));
    }
}
