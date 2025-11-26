package com.github.leoarj.superatecnologia.desafio.api;

import com.github.leoarj.superatecnologia.desafio.domain.exception.EntidadeNaoEncontradaException;
import com.github.leoarj.superatecnologia.desafio.domain.exception.NegocioException;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.util.Map;
import java.util.stream.Collectors;

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

    // Para ter acesso ao recurso de mensagens personalizadas de validação (messages.properties).
    private final MessageSource messageSource;

    /*
     * Método para personalizar a resposta de acordo com a RFC 7807,
     * para a exceção de MethodArgumentNotValidException.
     */

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(status);
        problemDetail.setTitle("Um ou mais campos estão inválidos");
        problemDetail.setType(URI.create("https://algatransito.com/erros/campos-invalidos"));

        // Extrai da exceção um mapa com os campos associados a suas respectivas mensagens de erro.
        // Com MessageSource: Recupera a mensagem específica de acordo com o code do objeto de erro.
        Map<String, String> fields = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .collect(Collectors.toMap(objectError -> ((FieldError) objectError).getField(),
                        //DefaultMessageSourceResolvable::getDefaultMessage));
                        objectError -> messageSource.getMessage(objectError, LocaleContextHolder.getLocale())));

        // Cria uma propriedade personalizada no corpo do detalhamento de erro
        // passando o mapeamento dos campos e mensagens de erro.
        problemDetail.setProperty("fields", fields);

        // Deve repassar uma instância de ProblemDetail (do pacote org.springframework.http)
        // para que a alteração tenha efeito.
        return handleExceptionInternal(ex, problemDetail, headers, status, request);
    }

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

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDeniedException(AccessDeniedException e) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.FORBIDDEN); // 403
        problem.setTitle("Acesso negado");
        problem.setDetail(e.getMessage());
        problem.setType(URI.create("https://github.leoarj.superatecnologia.desafio.com/erros/acesso-negado"));
        return problem;
    }
}
