package com.github.leoarj.superatecnologia.desafio.domain.service;

import com.github.leoarj.superatecnologia.desafio.domain.exception.UsuarioNaoEncontradoException;
import com.github.leoarj.superatecnologia.desafio.domain.model.Usuario;
import com.github.leoarj.superatecnologia.desafio.domain.repository.UsuarioRepository;
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
class CadastroUsuarioServiceTest {

    @InjectMocks
    private CadastroUsuarioService cadastroUsuarioService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Test
    void listar_DeveRetornarTodosUsuarios_QuandoExistirem() {
        // Arrange
        // Gera usuário 1 com ID e Nome fixos
        Usuario u1 = Instancio.of(Usuario.class)
                .set(field(Usuario::getId), 1L)
                .set(field(Usuario::getNome), "João")
                .create();

        // Gera usuário 2 com ID e Nome fixos
        Usuario u2 = Instancio.of(Usuario.class)
                .set(field(Usuario::getId), 2L)
                .set(field(Usuario::getNome), "Maria")
                .create();

        Mockito.when(usuarioRepository.findAll()).thenReturn(List.of(u1, u2));

        // Act
        List<Usuario> resultado = cadastroUsuarioService.listar();

        // Assert
        assertThat(resultado).hasSize(2);
        assertThat(resultado).contains(u1, u2);
    }

    @Test
    void buscarOuFalhar_DeveRetornarUsuario_QuandoIdExistir() {
        // Arrange
        Long usuarioId = 1L;

        // Precisa somente do ID fixado
        Usuario usuarioMock = Instancio.of(Usuario.class)
                .set(field(Usuario::getId), usuarioId)
                .create();

        Mockito.when(usuarioRepository.findById(usuarioId))
                .thenReturn(Optional.of(usuarioMock));

        // Act
        Usuario resultado = cadastroUsuarioService.buscarOuFalhar(usuarioId);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(usuarioId);
    }

    @Test
    void buscarOuFalhar_DeveLancarException_QuandoIdNaoExistir() {
        // Arrange
        Long usuarioId = 999L;
        Mockito.when(usuarioRepository.findById(usuarioId))
                .thenReturn(Optional.empty());

        // Act & Assert
        Assertions.assertThrows(UsuarioNaoEncontradoException.class, () -> {
            cadastroUsuarioService.buscarOuFalhar(usuarioId);
        });
    }
}