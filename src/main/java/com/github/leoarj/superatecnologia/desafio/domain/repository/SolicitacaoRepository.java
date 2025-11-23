package com.github.leoarj.superatecnologia.desafio.domain.repository;

import com.github.leoarj.superatecnologia.desafio.domain.model.Solicitacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolicitacaoRepository extends JpaRepository<Solicitacao, Long> {

    // Usuário: Consultar apenas suas próprias solicitações"
    // TODO: Depois externalizar (META-INF/XML) consultas JPQ grandes (+3 linhas)
    @Query("SELECT DISTINCT s FROM Solicitacao s " +
            "LEFT JOIN FETCH s.modulos " +
            "WHERE s.usuario.id = :usuarioId")
    List<Solicitacao> findByUsuarioId(Long usuarioId);

}
