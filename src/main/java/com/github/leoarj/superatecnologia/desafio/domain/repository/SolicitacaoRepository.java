package com.github.leoarj.superatecnologia.desafio.domain.repository;

import com.github.leoarj.superatecnologia.desafio.domain.model.Solicitacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolicitacaoRepository extends JpaRepository<Solicitacao, Long> {

    @Query("SELECT DISTINCT s FROM Solicitacao s " +
        "LEFT JOIN FETCH s.modulosSolicitados " +
        "LEFT JOIN FETCH s.usuario u " +
        "LEFT JOIN FETCH u.departamento " +
        "WHERE s.usuario.id = :usuarioId and s.id = :id")
    Optional<Solicitacao> findByIdComRelacionamentos(Long usuarioId, Long id);

}
