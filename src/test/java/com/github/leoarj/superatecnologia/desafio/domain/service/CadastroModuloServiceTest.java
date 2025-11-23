package com.github.leoarj.superatecnologia.desafio.domain.service;

import com.github.leoarj.superatecnologia.desafio.domain.exception.ModuloNaoEncontradoException;
import com.github.leoarj.superatecnologia.desafio.domain.model.Modulo;
import com.github.leoarj.superatecnologia.desafio.domain.repository.ModuloRepository;
import org.instancio.Instancio;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

@ExtendWith(MockitoExtension.class)
class CadastroModuloServiceTest {

    @InjectMocks
    private CadastroModuloService cadastroModuloService;

    @Mock
    private ModuloRepository moduloRepository;

    @Test
    void listar_DeveRetornarListaDeModulos_QuandoExistiremModulos() {
        // Arrange
        // Cria moduloFinanceiro com ID e Nome fixos, o resto aleatório
        Modulo moduloFinanceiro = Instancio.of(Modulo.class)
                .set(field(Modulo::getId), 1L)
                .set(field(Modulo::getNome), "Financeiro")
                .create();

        // Cria moduloRh com ID e Nome fixos
        Modulo moduloRh = Instancio.of(Modulo.class)
                .set(field(Modulo::getId), 2L)
                .set(field(Modulo::getNome), "RH")
                .create();

        Mockito.when(moduloRepository.findAll())
                .thenReturn(List.of(moduloFinanceiro, moduloRh));

        // Act
        List<Modulo> modulosExistentes = cadastroModuloService.listar();

        // Assert
        assertThat(modulosExistentes).isNotEmpty();
        assertThat(modulosExistentes).hasSize(2);
        assertThat(modulosExistentes)
                .extracting(Modulo::getNome)
                .containsExactly("Financeiro", "RH");
    }

    @Test
    void buscarOuFalhar_DeveRetornarModulo_QuandoExistir() {
        // Arrange
        Long moduloId = 1L;

        // Gera um módulo aleatório, garantindo apenas que o ID seja o esperado
        Modulo moduloMock = Instancio.of(Modulo.class)
                .set(field(Modulo::getId), moduloId)
                .create();

        Mockito.when(moduloRepository.findById(moduloId))
                .thenReturn(Optional.of(moduloMock));

        // Act
        Modulo moduloProcurado = cadastroModuloService.buscarOuFalhar(moduloId);

        // Assert
        assertThat(moduloProcurado).isNotNull();
        assertThat(moduloProcurado.getId()).isEqualTo(moduloId);
    }

    @Test
    void buscarOuFalhar_DeveLancarException_QuandoIdNaoExistir() {
        // Arrange
        Long moduloId = 99L;
        Mockito.when(moduloRepository.findById(moduloId)).thenReturn(Optional.empty());

        // Act & Assert
        Assertions.assertThrows(ModuloNaoEncontradoException.class, () -> {
            cadastroModuloService.buscarOuFalhar(moduloId);
        });
    }
}