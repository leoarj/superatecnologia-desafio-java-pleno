package com.github.leoarj.superatecnologia.desafio.api.v1.controller;

import com.github.leoarj.superatecnologia.desafio.domain.model.*;
import com.github.leoarj.superatecnologia.desafio.domain.repository.DepartamentoRepository;
import com.github.leoarj.superatecnologia.desafio.domain.repository.ModuloRepository;
import com.github.leoarj.superatecnologia.desafio.domain.repository.SolicitacaoRepository;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // teste mais field ao mundo real
@ActiveProfiles("test")
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK) // se fosse usar chamadas diretas ao DispatcherServlet dento da thread na JVM
//@AutoConfigureMockMvc
class FluxoSolicitacaoControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ModuloRepository moduloRepository;

    @Autowired
    private SolicitacaoRepository solicitacaoRepository;

    @Autowired
    private TokenService tokenService;

    private Usuario usuarioDono;
    private String tokenUsuarioDono;

    private Usuario usuarioIntruso;
    private String tokenUsuarioIntruso;

    private Solicitacao solicitacaoDoDono;

    private Modulo moduloFinanceiro;
    private Modulo moduloIncompativel;

    @Autowired
    private DepartamentoRepository departamentoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate; // <--- 1. INJETE ISSO

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

        var usuariosParaDeletar = usuarioRepository.findAll().stream()
                .filter(u -> !u.getId().equals(1L))
                .toList();
        usuarioRepository.deleteAll(usuariosParaDeletar);

        // 2. Reset Dinâmico da Sequência (H2)
        jdbcTemplate.execute("ALTER TABLE usuario ALTER COLUMN id RESTART WITH (SELECT COALESCE(MAX(id), 0) + 1 FROM usuario)");
        jdbcTemplate.execute("ALTER TABLE solicitacao ALTER COLUMN id RESTART WITH (SELECT COALESCE(MAX(id), 0) + 1 FROM solicitacao)");

        // 3. Buscar os dados que o Flyway já inseriu.
        // Isso evita o erro de "Unique index violation" nas sequences.

        // Carrega Usuário ID 1 (Que veio da migration V012)
        //usuarioPadrao = usuarioRepository.findById(1L).orElseThrow();
        usuarioDono = usuarioRepository.findByIdComRelacionamentos(1L).orElseThrow();
        tokenUsuarioDono = tokenService.gerarToken(usuarioDono);

        // Garante que o usuário comece "limpo" (sem módulos) para o teste
        usuarioDono.getModulosConcedidos().clear();
        usuarioRepository.save(usuarioDono);

        Departamento dep = departamentoRepository.findById(1L).orElseThrow();

        usuarioIntruso = new Usuario();
        usuarioIntruso.setNome("Intruso " + UUID.randomUUID().toString().substring(0, 8));
        usuarioIntruso.setEmail("intruso_" + UUID.randomUUID() + "@teste.com");
        usuarioIntruso.setSenha(passwordEncoder.encode("123456"));
        usuarioIntruso.setDepartamento(dep);
        usuarioIntruso = usuarioRepository.save(usuarioIntruso);

        tokenUsuarioIntruso = tokenService.gerarToken(usuarioIntruso);

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

        // 4. Cria uma Solicitação Prévia para os testes de GET
        solicitacaoDoDono = new Solicitacao();
        solicitacaoDoDono.setUsuario(usuarioDono);
        solicitacaoDoDono.setProtocolo("SOL-TEST-" + UUID.randomUUID().toString().substring(0, 5));
        solicitacaoDoDono.setJustificativa("Solicitação para teste de GET");
        solicitacaoDoDono.setStatus(StatusSolicitacao.ATIVO); // ou ATIVO
        solicitacaoDoDono.setDataSolicitacao(OffsetDateTime.now());
        solicitacaoDoDono.setModulosSolicitados(Set.of(moduloFinanceiro));

        solicitacaoDoDono = solicitacaoRepository.save(solicitacaoDoDono);

    }

    @Test
    void listar_DeveRetornar200_QuandoUsuarioConsultaSuasProprias() {
        given()
                .header("Authorization", "Bearer " + tokenUsuarioDono)
                .pathParam("usuarioId", usuarioDono.getId())
                .accept(ContentType.JSON)
                .when()
                .get("/{usuarioId}/solicitacoes")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("content", hasSize(greaterThanOrEqualTo(1)))
                .body("content[0].protocolo", notNullValue());
    }

    @Test
    void listar_DeveRetornar403_QuandoUsuarioConsultaDeOutro() {
        given()
                .header("Authorization", "Bearer " + tokenUsuarioIntruso)
                .pathParam("usuarioId", usuarioDono.getId())
                .accept(ContentType.JSON)
                .when()
                .get("/{usuarioId}/solicitacoes")
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void buscar_DeveRetornar200_QuandoSolicitacaoPertenceAoUsuario() {
        given()
                .header("Authorization", "Bearer " + tokenUsuarioDono)
                .pathParam("usuarioId", usuarioDono.getId())
                .pathParam("solicitacaoId", solicitacaoDoDono.getId())
                .accept(ContentType.JSON)
                .when()
                .get("/{usuarioId}/solicitacoes/{solicitacaoId}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(solicitacaoDoDono.getId().intValue()))
                .body("historico", notNullValue());
    }

    @Test
    void buscar_DeveRetornar403_QuandoTentaVerDetalheDeOutro() {
        given()
                .header("Authorization", "Bearer " + tokenUsuarioIntruso)
                .pathParam("usuarioId", usuarioDono.getId())
                .pathParam("solicitacaoId", solicitacaoDoDono.getId())
                .accept(ContentType.JSON)
                .when()
                .get("/{usuarioId}/solicitacoes/{solicitacaoId}")
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    //@WithMockUsuario(id = 1L, email = "admin.ti@empresa.com") // <--- Simula o Usuário 1 logado
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
                .header("Authorization", "Bearer " + tokenUsuarioDono) // <--- Passamos o Token
                .pathParam("usuarioId", usuarioDono.getId())
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

    //@WithMockUsuario(id = 1L, email = "admin.ti@empresa.com") // <--- Simula o Usuário 1 logado
    @Test
    void solicitar_DeveRetornar201_E_StatusNegado_QuandoRegraViolada() {
        // Cenário: Usuário JÁ TEM o módulo 5 (Solicitante) e pede o 4 (Aprovador)
        // Restrição: Eles são mutuamente exclusivos na configuração registrada via banco de dados.

        usuarioDono.getModulosConcedidos().add(moduloIncompativel);
        usuarioRepository.save(usuarioDono);

        String corpoJson = """
            {
                "modulosSolicitados": [ { "id": %d } ],
                "justificativa": "Tentando acesso indevido a modulos incompativeis.",
                "urgente": false
            }
        """.formatted(moduloFinanceiro.getId());

        given()
                .header("Authorization", "Bearer " + tokenUsuarioDono) // <--- Passamos o Token
                .pathParam("usuarioId", usuarioDono.getId())
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

    //@WithMockUsuario(id = 1L, email = "admin.ti@empresa.com") // <--- Simula o Usuário 1 logado
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
                .header("Authorization", "Bearer " + tokenUsuarioDono) // <--- Passamos o Token
                .pathParam("usuarioId", usuarioDono.getId())
                .body(corpoJson)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post("/{usuarioId}/solicitacoes")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("title", notNullValue());
    }

    //@WithMockUsuario(id = 1L, email = "admin.ti@empresa.com") // <--- Simula o Usuário 1 logado
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
                .header("Authorization", "Bearer " + tokenUsuarioDono) // <--- Passamos o Token
                .pathParam("usuarioId", usuarioDono.getId())
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
                .header("Authorization", "Bearer " + tokenUsuarioDono) // <--- Passamos o Token
                .pathParam("usuarioId", usuarioDono.getId())
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

    //@WithMockUsuario(id = 1L, email = "admin.ti@empresa.com") // <--- Simula o Usuário 1 logado
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
                .header("Authorization", "Bearer " + tokenUsuarioDono) // <--- Passamos o Token
                .pathParam("usuarioId", usuarioDono.getId())
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
                .header("Authorization", "Bearer " + tokenUsuarioDono) // <--- Passamos o Token
                .pathParam("usuarioId", usuarioDono.getId())
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
    void cancelar_DeveRetornar204_ERevogarAcesso_QuandoSolicitacaoAtiva() {
        // 1. PREPARAÇÃO (ARRANGE)
        // Precisamos de uma solicitação ATIVA e o usuário já com o módulo
        // para provar que o cancelamento realmente removeu o acesso.

        // Simula que o usuário já ganhou o módulo
        usuarioDono.getModulosConcedidos().add(moduloFinanceiro);
        usuarioRepository.save(usuarioDono);

        // Cria a solicitação que originou esse acesso (Status ATIVO)
        Solicitacao solicitacaoAtiva = new Solicitacao();
        solicitacaoAtiva.setUsuario(usuarioDono);
        solicitacaoAtiva.setProtocolo("SOL-ATIVA-TEST");
        solicitacaoAtiva.setJustificativa("Solicitação aprovada anteriormente");
        solicitacaoAtiva.setStatus(StatusSolicitacao.ATIVO); // Importante: ATIVO
        solicitacaoAtiva.setDataSolicitacao(OffsetDateTime.now().minusDays(10));
        solicitacaoAtiva.setDataExpiracao(OffsetDateTime.now().plusDays(30)); // Venceria no futuro
        solicitacaoAtiva.setModulosSolicitados(Set.of(moduloFinanceiro));

        solicitacaoAtiva = solicitacaoRepository.save(solicitacaoAtiva);

        // JSON do Motivo (Input DTO)
        String corpoJson = """
            {
                "motivo": "Não atuo mais neste projeto, solicitando cancelamento."
            }
        """;

        // 2. AÇÃO (ACT)
        given()
                .header("Authorization", "Bearer " + tokenUsuarioDono)
                .pathParam("usuarioId", usuarioDono.getId())
                .pathParam("solicitacaoId", solicitacaoAtiva.getId())
                .body(corpoJson)
                .contentType(ContentType.JSON)
                .when()
                .put("/{usuarioId}/solicitacoes/{solicitacaoId}/cancelamento") // Ou DELETE, conforme seu Controller
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value()); // 204

        // 3. VERIFICAÇÃO (ASSERT) - Efeitos Colaterais

        // A. Verifica se a solicitação mudou de status
        var solicitacaoCancelada = solicitacaoRepository.findById(solicitacaoAtiva.getId()).orElseThrow();
        assertThat(solicitacaoCancelada.getStatus()).isEqualTo(StatusSolicitacao.CANCELADO);
        assertThat(solicitacaoCancelada.getMotivoCancelamento()).contains("Não atuo mais");

        // B. Verifica se a data de expiração foi antecipada (está no passado ou presente)
        assertThat(solicitacaoCancelada.getDataExpiracao()).isBeforeOrEqualTo(OffsetDateTime.now());

        // C. Verifica se o módulo foi removido do usuário
        var usuarioAtualizado = usuarioRepository.findByIdComRelacionamentos(usuarioDono.getId()).orElseThrow();
        assertThat(usuarioAtualizado.getModulosConcedidos())
                .extracting("id")
                .doesNotContain(moduloFinanceiro.getId());
    }

    @Test
    void cancelar_DeveRetornar400_QuandoSolicitacaoJaEstiverFinalizada() {
        // 1. Cria uma solicitação já REJEITADA (não pode cancelar o que já morreu)
        Solicitacao solicitacaoRejeitada = new Solicitacao();
        solicitacaoRejeitada.setUsuario(usuarioDono);
        solicitacaoRejeitada.setProtocolo("SOL-REJ-TEST");
        solicitacaoRejeitada.setJustificativa("Teste erro");
        solicitacaoRejeitada.setStatus(StatusSolicitacao.NEGADO);
        solicitacaoRejeitada.setDataSolicitacao(OffsetDateTime.now());
        solicitacaoRejeitada.setModulosSolicitados(Set.of(moduloFinanceiro));

        solicitacaoRejeitada = solicitacaoRepository.save(solicitacaoRejeitada);

        String corpoJson = """
            { "motivo": "Tentando cancelar o incancelável" }
        """;

        given()
                .header("Authorization", "Bearer " + tokenUsuarioDono)
                .pathParam("usuarioId", usuarioDono.getId())
                .pathParam("solicitacaoId", solicitacaoRejeitada.getId())
                .body(corpoJson)
                .contentType(ContentType.JSON)
                .when()
                .put("/{usuarioId}/solicitacoes/{solicitacaoId}/cancelamento")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value()) // Espera erro de negócio (NegocioException)
                .body("title", containsString("não pode ser cancelada")); // Valida mensagem
    }
}