package com.github.leoarj.superatecnologia.desafio.api.v1.openapi;

import com.github.leoarj.superatecnologia.desafio.api.v1.model.Input.SolicitacaoCancelamentoRequest;
import com.github.leoarj.superatecnologia.desafio.api.v1.model.Input.SolicitacaoRequest;
import com.github.leoarj.superatecnologia.desafio.api.v1.model.SolicitacaoResponse;
import com.github.leoarj.superatecnologia.desafio.api.v1.model.SolicitacaoComDetalhamentoResponse;
import com.github.leoarj.superatecnologia.desafio.core.springdoc.PageableParameter;
import com.github.leoarj.superatecnologia.desafio.domain.filter.SolicitacaoFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Solicitações")
@SecurityRequirement(name = "bearer-key")
public interface FluxoSolicitacaoControllerOpenApi {

    @Operation(summary = "Lista as solicitações com paginação e filtro",
            description = "Por padrão ordena por data de solicitação decrescente.",

            parameters = {
                    @Parameter(
                            in = ParameterIn.QUERY,
                            name = "protocolo",
                            description = "Protocolo da solicitação para filtro da pesquisa",
                            example = "SOL-TEST-0001",
                            schema = @Schema(type = "string")
                    ),
                    @Parameter(
                            in = ParameterIn.QUERY,
                            name = "justificativa",
                            description = "Justificativa da solicitação para filtro da pesquisa",
                            example = "Preciso de acesso para fechamento mensal da folha",
                            schema = @Schema(type = "string")
                    ),
                    @Parameter(
                            in = ParameterIn.QUERY,
                            name = "status",
                            description = "Status da solicitação para filtro da pesquisa",
                            example = "ATIVO",
                            schema = @Schema(type = "string")
                    ),
                    @Parameter(
                            in = ParameterIn.QUERY,
                            name = "urgente",
                            description = "Indicador de urgência da solicitação para filtro da pesquisa",
                            example = "true",
                            schema = @Schema(type = "boolean")
                    ),
                    @Parameter(
                            in = ParameterIn.QUERY,
                            name = "dataCriacaoInicio",
                            description = "Data/hora de criação inicial para filtro da pesquisa",
                            example = "2025-11-13",
                            schema = @Schema(type = "string", format = "date")
                    ),
                    @Parameter(
                            in = ParameterIn.QUERY,
                            name = "dataCriacaoFim",
                            description = "Data/hora de criação final para filtro da pesquisa",
                            example = "2025-11-18",
                            schema = @Schema(type = "string", format = "date")
                    )
            }
    )
    @PageableParameter
    Page<SolicitacaoResponse> listar(
            @Parameter(description = "ID do usuário", example = "1", required = true) 
            Long usuarioId,
            
            @Parameter(hidden = true)
            SolicitacaoFilter filtro,
            
            @Parameter(hidden = true) // Esconder Pageable nativo
            Pageable pageable);

    @Operation(summary = "Lista uma solicitação específica",
            description = "Lista uma solicitação específica.")
    SolicitacaoComDetalhamentoResponse buscar(
            @Parameter(description = "ID do usuário", example = "1", required = true)
            Long usuarioId,

            @Parameter(description = "ID da solicitação", example = "1", required = true)
            Long solicitacaoId);

    @Operation(summary = "Registra uma nova solicitação", responses = {
            @ApiResponse(responseCode = "201", description = "Solicitação registrada (Ativa ou Negada)"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", 
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    SolicitacaoResponse solicitar(@PathVariable Long usuarioId,
                                         @RequestBody @Valid SolicitacaoRequest solicitacaoRequest);

    @Operation(summary = "Renova uma nova solicitação", responses = {
            @ApiResponse(responseCode = "201", description = "Solicitação registrada (Ativa ou Negada)"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    SolicitacaoResponse renovar(@PathVariable Long usuarioId,
                                @PathVariable Long solicitacaoId);

    @Operation(summary = "Cancela uma solicitação", responses = {
            @ApiResponse(responseCode = "201", description = "Solicitação registrada (Ativa ou Negada)"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    void cancelar(@PathVariable Long usuarioId,
                  @PathVariable Long solicitacaoId,
                  @RequestBody @Valid SolicitacaoCancelamentoRequest solicitacaoCancelamentoRequest);
}