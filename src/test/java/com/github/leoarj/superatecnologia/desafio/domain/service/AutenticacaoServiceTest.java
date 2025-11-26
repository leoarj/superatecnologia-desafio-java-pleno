package com.github.leoarj.superatecnologia.desafio.domain.service;

import com.github.leoarj.superatecnologia.desafio.domain.exception.NegocioException;
import com.github.leoarj.superatecnologia.desafio.domain.model.Usuario;
import com.github.leoarj.superatecnologia.desafio.domain.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AutenticacaoServiceTest {

    @InjectMocks
    private AutenticacaoService autenticacaoService;

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private TokenService tokenService;

    @Test
    void login_DeveRetornarToken_QuandoCredenciaisValidas() {
        Usuario usuario = new Usuario();
        usuario.setSenha("hash_senha");

        when(usuarioRepository.findByEmail("email@teste.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("123", "hash_senha")).thenReturn(true);
        when(tokenService.gerarToken(usuario)).thenReturn("token_jwt_valido");

        String token = autenticacaoService.login("email@teste.com", "123");

        assertThat(token).isEqualTo("token_jwt_valido");
    }

    @Test
    void login_DeveLancarErro_QuandoSenhaInvalida() {
        Usuario usuario = new Usuario();
        usuario.setSenha("hash_senha");

        when(usuarioRepository.findByEmail("email@teste.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("errada", "hash_senha")).thenReturn(false);

        assertThrows(NegocioException.class, () -> 
            autenticacaoService.login("email@teste.com", "errada"));
    }
}