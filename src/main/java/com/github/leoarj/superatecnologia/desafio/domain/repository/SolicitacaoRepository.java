package com.github.leoarj.superatecnologia.desafio.domain.repository;

import com.github.leoarj.superatecnologia.desafio.domain.model.Solicitacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SolicitacaoRepository extends JpaRepository<Solicitacao, Long>,
        JpaSpecificationExecutor<Solicitacao> {

    // Usuário: Consultar apenas suas próprias solicitações (foi passado para a specification)"
//    // TODO: Depois externalizar (META-INF/XML) consultas JPQ grandes (+3 linhas)
//    @Query("SELECT DISTINCT s FROM Solicitacao s " +
//            "LEFT JOIN FETCH s.modulosSolicitados " +
//            "WHERE s.usuario.id = :usuarioId")
//    Page<Solicitacao> findByUsuarioId(Long usuarioId, Pageable pageable);

    @Query("SELECT DISTINCT s FROM Solicitacao s " +
        "LEFT JOIN FETCH s.modulosSolicitados " +
        "LEFT JOIN FETCH s.usuario u " +
        "LEFT JOIN FETCH u.departamento " +
        "WHERE s.usuario.id = :usuarioId and s.id = :id")
    Optional<Solicitacao> findByIdComRelacionamentos(Long usuarioId, Long id);

}
