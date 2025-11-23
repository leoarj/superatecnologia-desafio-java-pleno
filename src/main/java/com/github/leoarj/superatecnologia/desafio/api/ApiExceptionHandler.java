package com.github.leoarj.superatecnologia.desafio.api;

import com.github.leoarj.superatecnologia.desafio.domain.exception.EntidadeNaoEncontradaException;
import com.github.leoarj.superatecnologia.desafio.domain.exception.NegocioException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;

/*
* Ponto único para tratamento de exceções de forma global.
*
* ResponseEntityExceptionHandler = estende essa classe que por padrão
* já trata as mensagens de erro de acordo com a RFC 7807.
* Ref.: https://www.rfc-editor.org/rfc/rfc7807
*/

@AllArgsConstructor
@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NegocioException.class)
    public ProblemDetail handleNegocio(NegocioException e) {
        //return ResponseEntity.badRequest().body(e.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST); //400
        problemDetail.setTitle(e.getMessage());
        problemDetail.setType(URI.create("https://github.leoarj.superatecnologia.desafio.com/erros/regra-de-negocio"));

        return problemDetail;
    }

    @ExceptionHandler(EntidadeNaoEncontradaException.class)
    public ProblemDetail handleEntidadeNaoEncontrada(EntidadeNaoEncontradaException e) {
        //return ResponseEntity.badRequest().body(e.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND); //404
        problemDetail.setTitle(e.getMessage());
        problemDetail.setType(URI.create("https://github.leoarj.superatecnologia.desafio.com/erros/nao-encontrado"));

        return problemDetail;
    }
}
