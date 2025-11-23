package com.github.leoarj.superatecnologia.desafio.core.modelmapper;

import com.github.leoarj.superatecnologia.desafio.api.v1.model.Input.SolicitacaoRequest;
import com.github.leoarj.superatecnologia.desafio.domain.model.Solicitacao;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        var modelMapper = new ModelMapper();

        // Para objetos de referência (não há necessidade)
//        modelMapper.createTypeMap(ModuloIdRequest.class, Modulo.class)
//                .addMappings(mapper -> mapper.skip(Modulo::setId));

        // Para não afetar/sobreescrever o ID ao mapear de volta para entity
        modelMapper.createTypeMap(SolicitacaoRequest.class, Solicitacao.class)
                .addMappings(mapper -> mapper.skip(Solicitacao::setId));

        return modelMapper;
    }

}
