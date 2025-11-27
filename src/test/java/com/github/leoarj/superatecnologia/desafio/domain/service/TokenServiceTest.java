package com.github.leoarj.superatecnologia.desafio.domain.service;

import com.github.leoarj.superatecnologia.desafio.domain.model.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @InjectMocks
    private TokenService tokenService;

    @Test
    void gerarToken_DeveRetornarStringValida() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("teste@teste.com");
        
        // Injeta os valores do application.yml via Reflection
        ReflectionTestUtils.setField(tokenService, "secret", "123456");
        ReflectionTestUtils.setField(tokenService, "expirationMinutes", 30);

        // Act
        String token = tokenService.gerarToken(usuario);

        // Assert
        assertThat(token).isNotBlank();
    }
}