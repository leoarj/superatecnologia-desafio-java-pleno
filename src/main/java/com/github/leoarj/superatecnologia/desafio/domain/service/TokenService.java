package com.github.leoarj.superatecnologia.desafio.domain.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.github.leoarj.superatecnologia.desafio.domain.exception.TokenInvalidoException;
import com.github.leoarj.superatecnologia.desafio.domain.model.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;
    
    @Value("${api.security.token.expiration-minutes}")
    private Integer expirationMinutes;

    public String gerarToken(Usuario usuario) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("SuperaTecnologiaAPI")
                    .withSubject(usuario.getEmail())
                    .withClaim("id", usuario.getId())
                    .withClaim("nome", usuario.getNome())
                    .withExpiresAt(dataExpiracao())
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar token JWT", exception);
        }
    }

    public String getSubject(String tokenJWT) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("SuperaTecnologiaAPI")
                    .build()
                    .verify(tokenJWT)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            throw new TokenInvalidoException("Token JWT inválido ou expirado!");
        }
    }

    private Instant dataExpiracao() {
//        return LocalDateTime.now().plusMinutes(expirationMinutes).toInstant(ZoneOffset.of("-03:00"));
        return Instant.now().plusSeconds(expirationMinutes * 60);
    }
}