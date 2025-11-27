package com.github.leoarj.superatecnologia.desafio.infrastructure.repository.spec;

import com.github.leoarj.superatecnologia.desafio.domain.filter.SolicitacaoFilter;
import com.github.leoarj.superatecnologia.desafio.domain.model.Solicitacao;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;

public class SolicitacaoSpecs {

    public static Specification<Solicitacao> usandoFiltro(SolicitacaoFilter filtro, Long usuarioId) {
        return (root, query, builder) -> {
            // Garante que o fetch (load) dos relacionamentos seja feito para evitar N+1 no Pageable
            if (Long.class.equals(query.getResultType())) {
                // Se for query de count(*), não faz fetch para evitar erro
                root.fetch("usuario"); 
            } else {
                 root.fetch("usuario");
                 root.fetch("modulosSolicitados", jakarta.persistence.criteria.JoinType.LEFT); // Fetch nos módulos
            }

            var predicates = new ArrayList<Predicate>();

            // Filtro Obrigatório: Apenas do usuário solicitado
            predicates.add(builder.equal(root.get("usuario").get("id"), usuarioId));

            if (filtro != null) {
                if (StringUtils.hasText(filtro.getProtocolo())) {
                    predicates.add(builder.like(builder.upper(root.get("protocolo")), 
                        "%" + filtro.getProtocolo().toUpperCase() + "%"));
                }
                
                if (StringUtils.hasText(filtro.getJustificativa())) {
                    predicates.add(builder.like(builder.upper(root.get("justificativa")), 
                        "%" + filtro.getJustificativa().toUpperCase() + "%"));
                }

                if (filtro.getStatus() != null) {
                    predicates.add(builder.equal(root.get("status"), filtro.getStatus()));
                }

                if (filtro.getUrgente() != null) {
                    predicates.add(builder.equal(root.get("urgente"), filtro.getUrgente()));
                }

                if (filtro.getDataCriacaoInicio() != null) {
                    predicates.add(builder.greaterThanOrEqualTo(root.get("dataSolicitacao"), 
                        filtro.getDataCriacaoInicio().atStartOfDay()));
                }

                if (filtro.getDataCriacaoFim() != null) {
                    predicates.add(builder.lessThanOrEqualTo(root.get("dataSolicitacao"), 
                        filtro.getDataCriacaoFim().atTime(23, 59, 59)));
                }
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
}