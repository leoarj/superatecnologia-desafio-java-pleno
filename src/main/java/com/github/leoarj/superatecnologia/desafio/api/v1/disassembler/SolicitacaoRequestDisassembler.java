package com.github.leoarj.superatecnologia.desafio.api.v1.disassembler;

import com.github.leoarj.superatecnologia.desafio.api.v1.model.Input.SolicitacaoRequest;
import com.github.leoarj.superatecnologia.desafio.domain.model.Solicitacao;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class SolicitacaoRequestDisassembler {

    private final ModelMapper modelMapper;

    public Solicitacao toDomainObject(SolicitacaoRequest solicitacaoRequest) {
        return modelMapper.map(solicitacaoRequest, Solicitacao.class);
    }

}
