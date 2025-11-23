package com.github.leoarj.superatecnologia.desafio.api.v1.assembler;

import com.github.leoarj.superatecnologia.desafio.api.v1.model.ModuloResponse;
import com.github.leoarj.superatecnologia.desafio.api.v1.model.SolicitacaoResponse;
import com.github.leoarj.superatecnologia.desafio.domain.model.Modulo;
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
        return modelMapper.map(solicitacao, SolicitacaoResponse.class);
    }

    public List<SolicitacaoResponse> toCollectionModel(List<Solicitacao> solicitacoes) {
        return solicitacoes.stream()
                .map(this::toModel)
                .toList();
    }

}
