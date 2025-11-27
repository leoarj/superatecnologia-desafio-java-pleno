package com.github.leoarj.superatecnologia.desafio.api.v1.controller;

import com.github.leoarj.superatecnologia.desafio.api.v1.model.Input.DadosAutenticacaoRequest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AutenticacaoControllerIT {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "/v1/auth";
    }

    @Test
    void deveRetornarToken_QuandoCredenciaisCorretas() {
        // O usuário admin.ti@empresa.com / 123456 é inserido pelo Flyway V014 (se estiver rodando no H2)
        
        DadosAutenticacaoRequest login = new DadosAutenticacaoRequest("admin.ti@empresa.com", "123456");

        given()
            .contentType(ContentType.JSON)
            .body(login)
        .when()
            .post("/login")
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("token", notNullValue());
    }
    
    @Test
    void deveRetornar400_QuandoCredenciaisInvalidas() {
        DadosAutenticacaoRequest login = new DadosAutenticacaoRequest("admin.ti@empresa.com", "senha_errada");

        given()
            .contentType(ContentType.JSON)
            .body(login)
        .when()
            .post("/login")
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value());
    }
}