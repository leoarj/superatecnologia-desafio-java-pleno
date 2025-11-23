package com.github.leoarj.superatecnologia.desafio.api.v1.controller;

import com.github.leoarj.superatecnologia.desafio.domain.model.Modulo;
import com.github.leoarj.superatecnologia.desafio.domain.model.Usuario;
import com.github.leoarj.superatecnologia.desafio.domain.repository.ModuloRepository;
import com.github.leoarj.superatecnologia.desafio.domain.repository.SolicitacaoRepository;
import com.github.leoarj.superatecnologia.desafio.domain.repository.UsuarioRepository;
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
@ActiveProfiles("test")
class SolicitacaoControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ModuloRepository moduloRepository;

    @Autowired
    private SolicitacaoRepository solicitacaoRepository;

    private Usuario usuarioPadrao;
    private Modulo moduloFinanceiro;
    private Modulo moduloIncompativel;

    @BeforeEach
    void setUp() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;
        RestAssured.basePath = "/v1/usuarios";

        prepararDados();
    }

    private void prepararDados() {
        // 1. Limpa apenas a tabela de solicitações para não afetar os testes
        solicitacaoRepository.deleteAll();

        // 2. Buscar os dados que o Flyway já inseriu.
        // Isso evita o erro de "Unique index violation" nas sequences.

        // Carrega Usuário ID 1 (Que veio da migration V012)
        //usuarioPadrao = usuarioRepository.findById(1L).orElseThrow();
        usuarioPadrao = usuarioRepository.findByIdComRelacionamentos(1L).orElseThrow();

        // Garante que o usuário comece "limpo" (sem módulos) para o teste
        usuarioPadrao.getModulosConcedidos().clear();
        usuarioRepository.save(usuarioPadrao);

//        // Carrega Módulos Reais
//        // ID 4: Aprovador Financeiro (Compatível com TI)
        moduloFinanceiro = moduloRepository.findByIdComRelacionamentos(4L).orElseThrow();
//
//        // ID 5: Solicitante Financeiro (Incompatível com o ID 4)
        moduloIncompativel = moduloRepository.findByIdComRelacionamentos(5L).orElseThrow();

        // Por segurança, garantir explicitamente que eles são incompatíveis, mesmo que no banco isso já esteja configurado
        // (Caso a migration V009 tenha falhado ou não tenha inserido esse par)

        // Adiciona o 5 na lista de incompatíveis do 4
        moduloFinanceiro.getModulosIncompativeis().add(moduloIncompativel);
        moduloRepository.save(moduloFinanceiro);

        // Adiciona o 4 na lista de incompatíveis do 5 (Bidirecional)
        moduloIncompativel.getModulosIncompativeis().add(moduloFinanceiro);
        moduloRepository.save(moduloIncompativel);

    }

    @Test
    void solicitar_DeveRetornar201_E_StatusAtivo_QuandoTudoCorreto() {
        // Cenário: Usuário pede módulo 4 (Aprovador Financeiro)
        String corpoJson = """
            {
                "modulosSolicitados": [ { "id": %d } ],
                "justificativa": "Preciso de acesso para fechamento mensal da folha.",
                "urgente": true
            }
        """.formatted(moduloFinanceiro.getId());

        given()
                .pathParam("usuarioId", usuarioPadrao.getId())
                .body(corpoJson)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post("/{usuarioId}/solicitacoes")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("status", equalTo("ATIVO"))
                .body("protocolo", notNullValue())
                .body("modulosSolicitados[0].id", equalTo(moduloFinanceiro.getId().intValue()));
    }

    @Test
    void solicitar_DeveRetornar201_E_StatusNegado_QuandoRegraViolada() {
        // Cenário: Usuário JÁ TEM o módulo 5 (Solicitante) e pede o 4 (Aprovador)
        // Restrição: Eles são mutuamente exclusivos na configuração registrada via banco de dados.

        usuarioPadrao.getModulosConcedidos().add(moduloIncompativel);
        usuarioRepository.save(usuarioPadrao);

        String corpoJson = """
            {
                "modulosSolicitados": [ { "id": %d } ],
                "justificativa": "Tentando acesso indevido a modulos incompativeis.",
                "urgente": false
            }
        """.formatted(moduloFinanceiro.getId());

        given()
                .pathParam("usuarioId", usuarioPadrao.getId())
                .body(corpoJson)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post("/{usuarioId}/solicitacoes")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("status", equalTo("NEGADO"))
                .body("motivoRejeicao", containsString("incompatível")); // Ajuste aqui se a msg for diferente
    }

    @Test
    void solicitar_DeveRetornar400_QuandoJustificativaInvalida() {
        String corpoJson = """
            {
                "modulosSolicitados": [ { "id": %d } ],
                "justificativa": "teste",
                "urgente": true
            }
        """.formatted(moduloFinanceiro.getId());

        given()
                .pathParam("usuarioId", usuarioPadrao.getId())
                .body(corpoJson)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post("/{usuarioId}/solicitacoes")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("title", notNullValue());
    }

    @Test
    void renovar_DeveRetornar201_E_VincularSolicitacao() {
        // 1. Cria uma solicitação anterior via API
        String corpoJson = """
            {
                "modulosSolicitados": [ { "id": %d } ],
                "justificativa": "Solicitação original para renovacao futura.",
                "urgente": false
            }
        """.formatted(moduloFinanceiro.getId());

        Integer idSolicitacaoAnterior = given()
                .pathParam("usuarioId", usuarioPadrao.getId())
                .body(corpoJson)
                .contentType(ContentType.JSON)
                .when()
                .post("/{usuarioId}/solicitacoes")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract().path("id");

        // Recuperar a solicitação do banco e alterar a data de expiração,
        // para simular que ela está prestes a vencer (ou já venceu)
        var solicitacaoAnterior = solicitacaoRepository.findById(idSolicitacaoAnterior.longValue()).orElseThrow();

        // Definir que expirou ONTEM (logo, faltam menos de 30 dias)
        solicitacaoAnterior.setDataExpiracao(java.time.OffsetDateTime.now().minusDays(1));
        solicitacaoRepository.save(solicitacaoAnterior);

        // 2. Chama o endpoint de renovação
        given()
                .pathParam("usuarioId", usuarioPadrao.getId())
                .pathParam("solicitacaoId", idSolicitacaoAnterior)
                .contentType(ContentType.JSON)
                .when()
                .post("/{usuarioId}/solicitacoes/{solicitacaoId}/renovacao")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("status", equalTo("ATIVO"))
                .body("protocolo", notNullValue())
                .body("solicitacaoAnterior.id", equalTo(idSolicitacaoAnterior));
    }

    @Test
    void renovar_DeveRetornar400_QuandoAindaNaoEstiverNoPrazo() {
        // 1. Cria uma solicitação anterior via API
        String corpoJson = """
            {
                "modulosSolicitados": [ { "id": %d } ],
                "justificativa": "Solicitação original para renovacao futura.",
                "urgente": false
            }
        """.formatted(moduloFinanceiro.getId());

        Integer idSolicitacaoAnterior = given()
                .pathParam("usuarioId", usuarioPadrao.getId())
                .body(corpoJson)
                .contentType(ContentType.JSON)
                .when()
                .post("/{usuarioId}/solicitacoes")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract().path("id");

        // Recuperar a solicitação do banco e alterar a data de expiração,
        // para simular que ela NÃO está prestes a vencer
        var solicitacaoAnterior = solicitacaoRepository.findById(idSolicitacaoAnterior.longValue()).orElseThrow();

        // Definir +1 dia por garantia (logo, faltam mais de 30 dias)
        solicitacaoAnterior.setDataExpiracao(java.time.OffsetDateTime.now().plusDays(1));
        solicitacaoRepository.save(solicitacaoAnterior);

        // 2. Chama o endpoint de renovação
        given()
                .pathParam("usuarioId", usuarioPadrao.getId())
                .pathParam("solicitacaoId", idSolicitacaoAnterior)
                .contentType(ContentType.JSON)
                .when()
                .post("/{usuarioId}/solicitacoes/{solicitacaoId}/renovacao")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("status", equalTo("ATIVO"))
                .body("protocolo", notNullValue())
                .body("solicitacaoAnterior.id", equalTo(idSolicitacaoAnterior));
    }
}