package com.github.leoarj.superatecnologia.desafio.api.v1.assembler;

import com.github.leoarj.superatecnologia.desafio.api.v1.model.SolicitacaoAnteriorResumoResponse;
import com.github.leoarj.superatecnologia.desafio.api.v1.model.SolicitacaoComDetalhamentoResponse;
import com.github.leoarj.superatecnologia.desafio.domain.model.Solicitacao;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Component
public class SolicitacaoComDetalhamentoAssembler {

    private final ModelMapper modelMapper;

    public SolicitacaoComDetalhamentoResponse toModel(Solicitacao solicitacao) {
        var solicitacaoResponse = modelMapper.map(solicitacao, SolicitacaoComDetalhamentoResponse.class);

        // Constrói lista de histórico a partir da cadeia de solicitações anteriores
        List<SolicitacaoAnteriorResumoResponse> historico = new ArrayList<>();

        Solicitacao solicitacaoAnterior = solicitacao.getSolicitacaoAnterior();

        while (solicitacaoAnterior != null) {
            var historicoResumo = new SolicitacaoAnteriorResumoResponse();
            historicoResumo.setId(solicitacaoAnterior.getId());
            historicoResumo.setProtocolo(solicitacaoAnterior.getProtocolo());
            historicoResumo.setDataSolicitacao(solicitacaoAnterior.getDataSolicitacao());
            historicoResumo.setStatus(solicitacaoAnterior.getStatus().name());

            historico.add(historicoResumo);

            solicitacaoAnterior = solicitacaoAnterior.getSolicitacaoAnterior();
        }

        solicitacaoResponse.setHistorico(historico);

        return solicitacaoResponse;
    }


}
