package com.github.leoarj.superatecnologia.desafio.api.v1.assembler;

import com.github.leoarj.superatecnologia.desafio.api.v1.model.SolicitacaoResponse;
import com.github.leoarj.superatecnologia.desafio.domain.model.Solicitacao;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@AllArgsConstructor
@Component
public class SolicitacaoResponseAssembler {

    private final ModelMapper modelMapper;

    public SolicitacaoResponse toModel(Solicitacao solicitacao) {
        var solicitacaoResponse = modelMapper.map(solicitacao, SolicitacaoResponse.class);

        switch (solicitacao.getStatus()) {
            case ATIVO -> solicitacaoResponse.setMensagem(
                    "Solicitação criada com sucesso! Protocolo: " + solicitacao.getProtocolo() +
                            ". Seus acessos já estão disponíveis!");
            case NEGADO -> solicitacaoResponse.setMensagem("Solicitação negada. Motivo: " +
                    solicitacao.getMotivoRejeicao());
            default -> solicitacaoResponse.setMensagem(
                    "Solicitação realizada. Situação atual: " + solicitacao.getStatus().name());
        }

        return solicitacaoResponse;
    }

    public List<SolicitacaoResponse> toCollectionModel(List<Solicitacao> solicitacoes) {
        return solicitacoes.stream()
                .map(this::toModel)
                .toList();
    }

}
