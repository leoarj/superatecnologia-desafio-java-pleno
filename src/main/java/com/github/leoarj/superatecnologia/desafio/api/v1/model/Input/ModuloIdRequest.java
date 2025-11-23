package com.github.leoarj.superatecnologia.desafio.api.v1.model.Input;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModuloIdRequest {

    @NotNull
    private Long id;

}
