package com.github.leoarj.superatecnologia.desafio.api.v1.openapi;

import com.github.leoarj.superatecnologia.desafio.api.v1.model.ModuloResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "Módulos")
@SecurityRequirement(name = "bearer-key")
public interface ModuloControllerOpenApi {

    @Operation(summary = "Lista os todos os módulos disponíveis",
            description = "Lista os módulos para que seja possível realizar a multi-seleção no front-end.")
    List<ModuloResponse> listarTodos();

    @Operation(summary = "Lista um módulo específico",
            description = "Lista um módulo específico.")
    ModuloResponse buscar(
            @Parameter(description = "ID do módulo", example = "1", required = true)
            Long moduloId);

}