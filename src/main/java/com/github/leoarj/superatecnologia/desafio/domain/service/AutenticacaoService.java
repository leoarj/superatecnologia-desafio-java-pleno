package com.github.leoarj.superatecnologia.desafio.domain.service;

import com.github.leoarj.superatecnologia.desafio.domain.exception.NegocioException;
import com.github.leoarj.superatecnologia.desafio.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AutenticacaoService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public String login(String email, String senha) {
        // 1. Busca o usuário (se não achar, lança erro genérico para interromper fluxo)
        var usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new NegocioException("Usuário ou senha inválidos"));

        // 2. Verifica se a senha bate (Texto Plano vs Hash no Banco)
        if (!passwordEncoder.matches(senha, usuario.getSenha())) {
            throw new NegocioException("Usuário ou senha inválidos");
        }

        // 3. Se passou, gera o token
        return tokenService.gerarToken(usuario);
    }
}