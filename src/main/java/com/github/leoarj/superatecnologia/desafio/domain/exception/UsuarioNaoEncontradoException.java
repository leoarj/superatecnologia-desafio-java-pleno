package com.github.leoarj.superatecnologia.desafio.domain.exception;

public class UsuarioNaoEncontradoException extends EntidadeNaoEncontradaException {

    private static final long serialVersionUID = 1L;

    public UsuarioNaoEncontradoException(String message) {
        super(message);
    }

    public UsuarioNaoEncontradoException(Long moduloId) {
        this(String.format("Não existe um cadastro de usuário com código %d", moduloId));
    }
}
