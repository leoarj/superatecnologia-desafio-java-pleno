package com.github.leoarj.superatecnologia.desafio.api.v1.controller;

import com.github.leoarj.superatecnologia.desafio.domain.model.Usuario;
import com.github.leoarj.superatecnologia.desafio.domain.repository.UsuarioRepository;
import com.github.leoarj.superatecnologia.desafio.domain.service.TokenService;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test") // Força o uso do H2 e migrations H2
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
//@AutoConfigureMockMvc
class ModuloControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TokenService tokenService;

    private String tokenAdmin; //

    @BeforeEach
    void setUp() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;
        RestAssured.basePath = "/v1/modulos";

        Usuario usuario = usuarioRepository.findById(1L).orElseThrow();
        tokenAdmin = tokenService.gerarToken(usuario);
    }

    //@WithMockUsuario(id = 1L) // <--- Simula o Usuário 1 logado
    @Test
    void listar_DeveRetornarStatus200_E_ListaDeModulos() {
        // Como o Flyway roda automaticamente antes dos testes iniciarem,
        // o banco H2 já tem os dados inseridos pelo V008.

        given()
                .header("Authorization", "Bearer " + tokenAdmin) // <--- Passamos o Token
                .accept(ContentType.JSON)
                .when()
                .get()
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("", hasSize(greaterThanOrEqualTo(10))) // 10 inserts no script
                .body("nome", hasItems("Portal do Colaborador", "Gestão Financeira"));
    }

    //@WithMockUsuario(id = 1L) // <--- Simula o Usuário 1 logado
    @Test
    void buscar_DeveRetornarModuloCompleto_QuandoIdExistir() {
        // Teste: Módulo 4 (Aprovador Financeiro) que tem relacionamento
        Long moduloId = 4L;

        given()
                .header("Authorization", "Bearer " + tokenAdmin) // <--- Passamos o Token
                .pathParam("moduloId", moduloId)
                .accept(ContentType.JSON)
                .when()
                .get("/{moduloId}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(4))
                .body("nome", equalTo("Aprovador Financeiro"))
                // Verifica se o ModelMapper mapeou a lista de incompatíveis corretamente
                // O script diz que o 4 é incompatível com o 5
                .body("modulosIncompativeis", hasSize(1))
                .body("modulosIncompativeis[0].id", equalTo(5));
    }

    //@WithMockUsuario(id = 1L) // <--- Simula o Usuário 1 logado
    @Test
    void buscar_DeveRetornarStatus404_QuandoIdInexistente() {
        given()
                .header("Authorization", "Bearer " + tokenAdmin) // <--- Passamos o Token
                .pathParam("moduloId", 999)
                .accept(ContentType.JSON)
                .when()
                .get("/{moduloId}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }
}
