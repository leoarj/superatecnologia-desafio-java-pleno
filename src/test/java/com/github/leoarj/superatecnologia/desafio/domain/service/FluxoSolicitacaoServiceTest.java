package com.github.leoarj.superatecnologia.desafio.domain.service;

import com.github.leoarj.superatecnologia.desafio.domain.exception.NegocioException;
import com.github.leoarj.superatecnologia.desafio.domain.exception.SolicitacaoNaoEncontradaException;
import com.github.leoarj.superatecnologia.desafio.domain.model.*;
import com.github.leoarj.superatecnologia.desafio.domain.repository.ModuloRepository;
import com.github.leoarj.superatecnologia.desafio.domain.repository.SolicitacaoRepository;
import com.github.leoarj.superatecnologia.desafio.domain.repository.UsuarioRepository;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FluxoSolicitacaoServiceTest {

    @InjectMocks
    private FluxoSolicitacaoService fluxoSolicitacaoService;

    @Mock
    private SolicitacaoRepository solicitacaoRepository;

    @Mock
    private CadastroUsuarioService cadastroUsuarioService;

    @Mock
    private ModuloRepository moduloRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    private Usuario usuario;
    private Modulo moduloFinanceiro;
    private Modulo moduloRH;
    private Departamento departamentoTI;

    @BeforeEach
    void setUp() {
        // 1. Departamento
        departamentoTI = Instancio.of(Departamento.class)
                .set(field(Departamento::getId), 1L)
                .set(field(Departamento::getNome), "TI")
                .create();

        // 2. Usuário
        usuario = Instancio.of(Usuario.class)
                .set(field(Usuario::getId), 1L)
                .set(field(Usuario::getDepartamento), departamentoTI)
                .set(field(Usuario::getModulosConcedidos), new HashSet<>())
                .create();

        // 3. Módulo Financeiro
        moduloFinanceiro = Instancio.of(Modulo.class)
                .set(field(Modulo::getId), 10L)
                .set(field(Modulo::getNome), "Financeiro")
                .set(field(Modulo::getAtivo), true)
                .set(field(Modulo::getDisponivel), true)
                .set(field(Modulo::getDepartamentosPermitidos), Set.of(departamentoTI))
                .set(field(Modulo::getModulosIncompativeis), new HashSet<>())
                .create();

        // 4. Módulo RH
        moduloRH = Instancio.of(Modulo.class)
                .set(field(Modulo::getId), 20L)
                .set(field(Modulo::getNome), "RH")
                .set(field(Modulo::getAtivo), true)
                .set(field(Modulo::getDisponivel), true)
                .set(field(Modulo::getModulosIncompativeis), new HashSet<>())
                .create();
    }

    @Test
    void solicitar_DeveAprovarSolicitacao_QuandoRegrasAtendidas() {
        // Arrange
        Solicitacao solicitacaoInput = Instancio.of(Solicitacao.class)
                .set(field(Solicitacao::getJustificativa), "Preciso de acesso para fechar o mês.")
                .set(field(Solicitacao::getModulosSolicitados), Set.of(moduloFinanceiro))
                .set(field(Solicitacao::getUrgente), true)
                .create();

        // Lista exata de IDs que o service vai extrair
        List<Long> idsEsperados = List.of(moduloFinanceiro.getId());

        when(cadastroUsuarioService.buscarOuFalhar(1L)).thenReturn(usuario);
        when(moduloRepository.findAllById(idsEsperados)).thenReturn(List.of(moduloFinanceiro));

        // Passar a própria instância 'solicitacaoInput' porque o service trabalha nela
        when(solicitacaoRepository.save(solicitacaoInput)).thenAnswer(i -> i.getArgument(0));

        // Act
        Solicitacao resultado = fluxoSolicitacaoService.solicitar(1L, solicitacaoInput);

        // Assert
        assertThat(resultado.getStatus()).isEqualTo(StatusSolicitacao.ATIVO);
        assertThat(resultado.getProtocolo()).isNotNull();
        assertThat(resultado.getModulosSolicitados()).contains(moduloFinanceiro);

        verify(usuarioRepository).save(usuario);
        // Verifica se o Usuário recebeu o módulo na lista de concedidos
        assertThat(usuario.getModulosConcedidos()).contains(moduloFinanceiro);
    }

    @Test
    void solicitar_DeveNegarSolicitacao_QuandoUsuarioJaPossuiModuloIncompativel() {
        // Arrange
        moduloFinanceiro.setModulosIncompativeis(Set.of(moduloRH));
        usuario.getModulosConcedidos().add(moduloRH);

        Solicitacao solicitacaoInput = Instancio.of(Solicitacao.class)
                .set(field(Solicitacao::getJustificativa), "Solicitando acesso financeiro.")
                .set(field(Solicitacao::getModulosSolicitados), Set.of(moduloFinanceiro))
                .create();

        List<Long> idsEsperados = List.of(moduloFinanceiro.getId());

        when(cadastroUsuarioService.buscarOuFalhar(1L)).thenReturn(usuario);
        when(moduloRepository.findAllById(idsEsperados)).thenReturn(List.of(moduloFinanceiro));

        // Passar a instância exata
        when(solicitacaoRepository.save(solicitacaoInput)).thenAnswer(i -> i.getArgument(0));

        // Act
        Solicitacao resultado = fluxoSolicitacaoService.solicitar(1L, solicitacaoInput);

        // Assert
        assertThat(resultado.getStatus()).isEqualTo(StatusSolicitacao.NEGADO);
        assertThat(resultado.getMotivoRejeicao()).contains("incompatível com 'RH'");

        verify(usuarioRepository, never()).save(usuario);
    }

    @Test
    void solicitar_DeveLancarException_QuandoQuantidadeExcederLimiteDoDepartamento() {
        // 1. ARRANGE (PREPARAÇÃO)
        int limiteDoDepartamento = 2;
        int quantidadeJaPossuida = 1;
        int quantidadeSolicitada = 2; // Total será 3, estourando o limite de 2

        // Quais IDs devem ser passados para o repositório
        final List<Long> idsEsperados = List.of(2L, 3L);

        // Cria o Departamento com o limite definido
        Departamento departamento = Instancio.of(Departamento.class)
                .set(field(Departamento::getLimiteModulos), limiteDoDepartamento)
                .create();

        // Cria módulos dummy apenas para preencher as listas
        Modulo moduloExistente = new Modulo();
        moduloExistente.setId(1L);
        Modulo novoModuloA = new Modulo();
        novoModuloA.setId(2L);
        Modulo novoModuloB = new Modulo();
        novoModuloB.setId(3L);

        // Cria Usuário que já possui 1 módulo e pertence ao departamento limitado
        Usuario usuario = Instancio.of(Usuario.class)
                .set(field(Usuario::getId), 1L)
                .set(field(Usuario::getDepartamento), departamento)
                // Importante: Usar HashSet para garantir que a lista não seja nula e tenha tamanho 1
                .set(field(Usuario::getModulosConcedidos), new HashSet<>(Set.of(moduloExistente)))
                .create();

        // Cria Solicitação pedindo +2 módulos
        Solicitacao solicitacao = Instancio.of(Solicitacao.class)
                .set(field(Solicitacao::getModulosSolicitados), Set.of(novoModuloA, novoModuloB))
                .create();

//        when(moduloRepository.findAllById(List.of(2L, 3L))).thenReturn(List.of(novoModuloA, novoModuloB));

        when(moduloRepository.findAllById(argThat(ids -> {
            // Cast para Collection para usar containsAll e size
            Collection<Long> idsCollection = (Collection<Long>) ids;

            // Verifica se a lista de IDs passada pelo Service contém todos os IDs esperados
            // E se o tamanho é o mesmo (para evitar passar listas vazias ou com IDs a mais)
            return idsCollection.containsAll(idsEsperados) && idsCollection.size() == idsEsperados.size();
        })))
                .thenReturn(List.of(novoModuloA, novoModuloB)); // Retorna a lista dos módulos encontrados

        // Mock do serviço de usuário para retornar nosso usuário montado
        when(cadastroUsuarioService.buscarOuFalhar(usuario.getId())).thenReturn(usuario);

        // 2. & 3. ACT & ASSERT (AÇÃO E VERIFICAÇÃO)
//        NegocioException erro = assertThrows(NegocioException.class, () ->
//                cadastroSolicitacaoService.solicitar(usuario.getId(), solicitacao)
//        );

        // Passar a instância exata
        when(solicitacaoRepository.save(solicitacao)).thenAnswer(i -> i.getArgument(0));

        // Act
        Solicitacao resultado = fluxoSolicitacaoService.solicitar(usuario.getId(), solicitacao);

        // Assert (Verificação do fluxo de NEGAÇÃO INTERNA)
        assertThat(resultado).isNotNull();
        assertThat(resultado.getStatus()).isEqualTo(StatusSolicitacao.NEGADO);
        assertThat(resultado.getMotivoRejeicao())
                .contains("Limite de módulos excedido")
                .contains("permite " + limiteDoDepartamento); // Valida que a regra foi acionada

        // Garante que o método save foi chamado no final
        verify(solicitacaoRepository).save(resultado);
    }

    @Test
    void solicitar_DeveLancarException_QuandoJustificativaInvalida() {
        // Arrange
        Solicitacao solicitacaoInput = Instancio.of(Solicitacao.class)
                .set(field(Solicitacao::getJustificativa), "teste")
                .set(field(Solicitacao::getModulosSolicitados), Set.of(moduloFinanceiro))
                .create();

        // Act & Assert (O erro ocorre antes de chamar os repositories, então não precisa de mocks complexos)
        assertThrows(NegocioException.class, () -> {
            fluxoSolicitacaoService.solicitar(1L, solicitacaoInput);
        });
    }

    @Test
    void renovar_DeveCriarNovaSolicitacaoVinculada_QuandoSucesso() {
        // Arrange
        Long idSolicitacaoAntiga = 500L;
        Solicitacao solicitacaoAntiga = Instancio.of(Solicitacao.class)
                .set(field(Solicitacao::getId), idSolicitacaoAntiga)
                .set(field(Solicitacao::getUsuario), usuario)
                .set(field(Solicitacao::getProtocolo), "SOL-ANTIGA")
                .set(field(Solicitacao::getModulosSolicitados), Set.of(moduloFinanceiro))
                .set(field(Solicitacao::getDataExpiracao), null)
                .create();

        when(cadastroUsuarioService.buscarOuFalhar(1L)).thenReturn(usuario);
        when(solicitacaoRepository.findById(idSolicitacaoAntiga)).thenReturn(Optional.of(solicitacaoAntiga));

        // Como o objeto 'novaSolicitacao' é criado com 'new' dentro do Service, não tem a referência dele aqui.
        // Usanodo 'argThat' para dizer: "Aceite salvar qualquer objeto (s) que tenha a solicitacaoAnterior igual a configurada no arrange"
        when(solicitacaoRepository.save(argThat(s ->
                s.getSolicitacaoAnterior() != null &&
                        s.getSolicitacaoAnterior().equals(solicitacaoAntiga)
        ))).thenAnswer(i -> i.getArgument(0));

        // Act
        Solicitacao novaSolicitacao = fluxoSolicitacaoService.renovar(1L, idSolicitacaoAntiga);

        // Assert
        assertThat(novaSolicitacao).isNotNull();
        assertThat(novaSolicitacao.getProtocolo()).isNotEqualTo("SOL-ANTIGA");
        assertThat(novaSolicitacao.getSolicitacaoAnterior()).isEqualTo(solicitacaoAntiga);
        assertThat(novaSolicitacao.getStatus()).isEqualTo(StatusSolicitacao.ATIVO);

        verify(usuarioRepository).save(usuario);
    }

    @Test
    void cancelar_DeveCancelarERevogarModulos_QuandoSolicitacaoAtiva() {
        // 1. Arrange (Preparação com Instancio)
        Long usuarioId = 1L;
        Long solicitacaoId = 100L;
        String motivo = "Não preciso mais";

        // Cria Módulos
        Modulo moduloAlvo = Instancio.of(Modulo.class)
                .set(field(Modulo::getId), 10L)
                .create();

        Modulo moduloOutro = Instancio.of(Modulo.class)
                .set(field(Modulo::getId), 20L)
                .create();

        // Cria Usuário com lista mutável (HashSet) contendo os módulos
        Usuario usuario = Instancio.of(Usuario.class)
                .set(field(Usuario::getId), usuarioId)
                .set(field(Usuario::getModulosConcedidos), new HashSet<>(Set.of(moduloAlvo, moduloOutro)))
                .create();

        // Cria Solicitação ATIVA
        Solicitacao solicitacao = Instancio.of(Solicitacao.class)
                .set(field(Solicitacao::getId), solicitacaoId)
                .set(field(Solicitacao::getUsuario), usuario)
                .set(field(Solicitacao::getStatus), StatusSolicitacao.ATIVO)
                .set(field(Solicitacao::getModulosSolicitados), Set.of(moduloAlvo))
                .set(field(Solicitacao::getDataExpiracao), OffsetDateTime.now().plusDays(30))
                .create();

        when(solicitacaoRepository.findByIdComRelacionamentos(usuarioId, solicitacaoId)).thenReturn(Optional.of(solicitacao));
        when(solicitacaoRepository.save(solicitacao)).thenAnswer(i -> i.getArgument(0));

        // 2. Act
        fluxoSolicitacaoService.cancelar(usuarioId, solicitacaoId, motivo);

        // 3. Assert
        assertThat(solicitacao.getStatus()).isEqualTo(StatusSolicitacao.CANCELADO);
        assertThat(solicitacao.getMotivoCancelamento()).isEqualTo(motivo);
        assertThat(solicitacao.getDataExpiracao()).isBeforeOrEqualTo(OffsetDateTime.now());

        // Verifica a revogação na lista do usuário
        assertThat(usuario.getModulosConcedidos())
                .contains(moduloOutro)
                .doesNotContain(moduloAlvo);

        verify(solicitacaoRepository).save(solicitacao);
    }

    @Test
    void cancelar_DeveLancarException_QuandoStatusNaoPermitir() {
        // 1. Arrange
        Long usuarioId = 1L;
        Long solicitacaoId = 100L;

        // Cria Solicitação com status REJEITADA via Instancio
        Solicitacao solicitacao = Instancio.of(Solicitacao.class)
                .set(field(Solicitacao::getId), solicitacaoId)
                .set(field(Solicitacao::getStatus), StatusSolicitacao.NEGADO)
                // O usuário não importa muito aqui, o Instancio cria um aleatório
                // mas precisamos garantir que o ID bata com o parametro para passar na validação de dono
                .set(field(Solicitacao::getUsuario), Instancio.of(Usuario.class).set(field(Usuario::getId), usuarioId).create())
                .create();

        when(solicitacaoRepository.findByIdComRelacionamentos(usuarioId, solicitacaoId)).thenReturn(Optional.of(solicitacao));

        // 2. & 3. Act & Assert
        NegocioException erro = assertThrows(NegocioException.class, () ->
                fluxoSolicitacaoService.cancelar(usuarioId, solicitacaoId, "Motivo")
        );

        assertThat(erro.getMessage()).contains("não pode ser cancelada");
        verify(solicitacaoRepository, never()).save(solicitacao);
    }

    @Test
    void cancelar_DeveLancarException_QuandoUsuarioNaoForDono() {
        // 1. Arrange
        Long usuarioIdSolicitante = 1L;
        Long usuarioIdDonoReal = 2L;
        Long solicitacaoId = 100L;

        // Cria Solicitação pertencente ao Dono Real
        Solicitacao solicitacao = Instancio.of(Solicitacao.class)
                .set(field(Solicitacao::getId), solicitacaoId)
                .set(field(Solicitacao::getStatus), StatusSolicitacao.ATIVO)
                .set(field(Solicitacao::getUsuario), Instancio.of(Usuario.class).set(field(Usuario::getId), usuarioIdDonoReal).create())
                .create();

        when(solicitacaoRepository.findByIdComRelacionamentos(usuarioIdSolicitante, solicitacaoId)).thenReturn(Optional.empty());

        // 2. & 3. Act & Assert
        // Assumindo que seu método buscarOuFalhar valida o usuário e lança exception
        assertThrows(SolicitacaoNaoEncontradaException.class, () ->
                fluxoSolicitacaoService.cancelar(usuarioIdSolicitante, solicitacaoId, "Motivo")
        );

        verify(solicitacaoRepository, never()).save(eq(solicitacao));
    }
}