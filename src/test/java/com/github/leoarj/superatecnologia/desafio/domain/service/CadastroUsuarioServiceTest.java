package com.github.leoarj.superatecnologia.desafio.domain.service;

import com.github.leoarj.superatecnologia.desafio.domain.exception.UsuarioNaoEncontradoException;
import com.github.leoarj.superatecnologia.desafio.domain.model.Usuario;
import com.github.leoarj.superatecnologia.desafio.domain.repository.UsuarioRepository;
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

@ExtendWith(MockitoExtension.class)
class CadastroUsuarioServiceTest {

    @InjectMocks
    private CadastroUsuarioService cadastroUsuarioService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Test
    void listar_DeveRetornarTodosUsuarios_QuandoExistirem() {
        // 1. Arrange
        Usuario u1 = new Usuario();
        u1.setId(1L);
        u1.setNome("João");

        Usuario u2 = new Usuario();
        u2.setId(2L);
        u2.setNome("Maria");

        Mockito.when(usuarioRepository.findAll()).thenReturn(List.of(u1, u2));

        // 2. Act
        List<Usuario> resultado = cadastroUsuarioService.listar();

        // 3. Assert
        assertThat(resultado).hasSize(2);
        assertThat(resultado).contains(u1, u2);
    }

    @Test
    void buscarOuFalhar_DeveRetornarUsuario_QuandoIdExistir() {
        // 1. Arrange
        Long usuarioId = 1L;
        Usuario usuarioMock = new Usuario();
        usuarioMock.setId(usuarioId);
        usuarioMock.setNome("Teste");

        Mockito.when(usuarioRepository.findById(usuarioId))
                .thenReturn(Optional.of(usuarioMock));

        // 2. Act
        Usuario resultado = cadastroUsuarioService.buscarOuFalhar(usuarioId);

        // 3. Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(usuarioId);
    }

    @Test
    void buscarOuFalhar_DeveLancarException_QuandoIdNaoExistir() {
        // 1. Arrange
        Long usuarioId = 999L;
        Mockito.when(usuarioRepository.findById(usuarioId))
                .thenReturn(Optional.empty());

        // 2 & 3. Act & Assert
        Assertions.assertThrows(UsuarioNaoEncontradoException.class, () -> {
            cadastroUsuarioService.buscarOuFalhar(usuarioId);
        });
    }
}