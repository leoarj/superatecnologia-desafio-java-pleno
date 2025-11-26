package com.github.leoarj.superatecnologia.desafio.api.v1.model.Input;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModuloIdRequest {

    @Schema(description = "ID do Módulo", example = "1")
    @NotNull
    private Long id;

}
