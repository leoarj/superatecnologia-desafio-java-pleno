package com.github.leoarj.superatecnologia.desafio.domain.service;

import com.github.leoarj.superatecnologia.desafio.domain.exception.NegocioException;
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

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CadastroSolicitacaoServiceTest {

    @InjectMocks
    private CadastroSolicitacaoService cadastroSolicitacaoService;

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
        Solicitacao resultado = cadastroSolicitacaoService.solicitar(1L, solicitacaoInput);

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
        Solicitacao resultado = cadastroSolicitacaoService.solicitar(1L, solicitacaoInput);

        // Assert
        assertThat(resultado.getStatus()).isEqualTo(StatusSolicitacao.NEGADO);
        assertThat(resultado.getMotivoRejeicao()).contains("incompatível com 'RH'");

        verify(usuarioRepository, never()).save(usuario);
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
            cadastroSolicitacaoService.solicitar(1L, solicitacaoInput);
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
        Solicitacao novaSolicitacao = cadastroSolicitacaoService.renovar(1L, idSolicitacaoAntiga);

        // Assert
        assertThat(novaSolicitacao).isNotNull();
        assertThat(novaSolicitacao.getProtocolo()).isNotEqualTo("SOL-ANTIGA");
        assertThat(novaSolicitacao.getSolicitacaoAnterior()).isEqualTo(solicitacaoAntiga);
        assertThat(novaSolicitacao.getStatus()).isEqualTo(StatusSolicitacao.ATIVO);

        verify(usuarioRepository).save(usuario);
    }
}