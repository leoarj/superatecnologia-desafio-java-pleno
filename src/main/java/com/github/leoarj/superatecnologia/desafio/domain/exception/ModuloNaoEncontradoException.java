package com.github.leoarj.superatecnologia.desafio.domain.exception;

public class ModuloNaoEncontradoException extends EntidadeNaoEncontradaException {

    private static final long serialVersionUID = 1L;

    public ModuloNaoEncontradoException(String message) {
        super(message);
    }

    public ModuloNaoEncontradoException(Long moduloId) {
        this(String.format("Não existe um cadastro de módulo com código %d", moduloId));
    }

}
