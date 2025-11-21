package com.github.leoarj.superatecnologia.desafio.api.v1.assembler;

import com.github.leoarj.superatecnologia.desafio.api.v1.model.ModuloResponse;
import com.github.leoarj.superatecnologia.desafio.domain.model.Modulo;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@AllArgsConstructor
@Component
public class ModuloResponseAssembler {

    private final ModelMapper modelMapper;

    public ModuloResponse toModel(Modulo modulo) {
        return modelMapper.map(modulo, ModuloResponse.class);
    }

    public List<ModuloResponse> toCollectionModel(List<Modulo> modulos) {
        return modulos.stream()
                .map(this::toModel)
                .toList();
    }

}
