package com.github.leoarj.superatecnologia.desafio.domain.service;

import com.github.leoarj.superatecnologia.desafio.domain.exception.NegocioException;
import com.github.leoarj.superatecnologia.desafio.domain.exception.SolicitacaoNaoEncontradaException;
import com.github.leoarj.superatecnologia.desafio.domain.filter.SolicitacaoFilter;
import com.github.leoarj.superatecnologia.desafio.domain.model.Modulo;
import com.github.leoarj.superatecnologia.desafio.domain.model.Solicitacao;
import com.github.leoarj.superatecnologia.desafio.domain.model.StatusSolicitacao;
import com.github.leoarj.superatecnologia.desafio.domain.model.Usuario;
import com.github.leoarj.superatecnologia.desafio.domain.repository.ModuloRepository;
import com.github.leoarj.superatecnologia.desafio.domain.repository.SolicitacaoRepository;
import com.github.leoarj.superatecnologia.desafio.domain.repository.UsuarioRepository;
import com.github.leoarj.superatecnologia.desafio.infrastructure.repository.spec.SolicitacaoSpecs;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;

/**
 * Fluxo de Solicitação.
 *
 * Melhorias: Extrair regras espalhadas para classes separadas.
 * */
@Service
@RequiredArgsConstructor
public class FluxoSolicitacaoService {

    private final SolicitacaoRepository solicitacaoRepository;
    private final CadastroUsuarioService cadastroUsuarioService;
    private final ModuloRepository moduloRepository;
    private final UsuarioRepository usuarioRepository; // Para atualizar permissões

    public Page<Solicitacao> listar(Long usuarioId, SolicitacaoFilter filtro, Pageable pageable) {
        return solicitacaoRepository.findAll(SolicitacaoSpecs.usandoFiltro(filtro, usuarioId), pageable);
    }

    public Solicitacao buscarOuFalhar(Long usuarioId, Long solicitacaoId) {
        return solicitacaoRepository.findByIdComRelacionamentos(usuarioId, solicitacaoId)
                .orElseThrow(() -> new SolicitacaoNaoEncontradaException(solicitacaoId));
    }

    @Transactional
    public Solicitacao solicitar(Long usuarioId, Solicitacao solicitacao) {
        // 1. Converter ModuloIdInput -> List<Long>
        List<Long> idsModulos = solicitacao.getModulosSolicitados().stream()
                .map(Modulo::getId)
                .toList();

        // 2. Validar e Carregar Dados
        validarJustificativa(solicitacao.getJustificativa());
        Usuario usuario = cadastroUsuarioService.buscarOuFalhar(usuarioId);

        List<Modulo> modulosSolicitados = moduloRepository.findAllById(idsModulos);

        if (modulosSolicitados.size() != idsModulos.size()) {
            throw new NegocioException("Um ou mais módulos informados não existem");
        }

        solicitacao.setUsuario(usuario);
        solicitacao.setProtocolo(gerarProtocoloUnico());
        solicitacao.setModulosSolicitados(new HashSet<>(modulosSolicitados)); // Substitui com Módulos em estado gerenciado pelo EntityManager

        // 4. Processar e Salvar
        return processarRegrasESalvar(solicitacao, usuario, modulosSolicitados);
    }

    @Transactional
    public Solicitacao renovar(Long usuarioId, Long solicitacaoAnteriorId) {
        Usuario usuario = cadastroUsuarioService.buscarOuFalhar(usuarioId);

        Solicitacao solicitacaoAnterior = solicitacaoRepository.findById(solicitacaoAnteriorId)
                .orElseThrow(() -> new SolicitacaoNaoEncontradaException("Solicitação anterior não encontrada"));

        // Validação básica: O usuário só pode renovar a própria solicitação
        if (!solicitacaoAnterior.getUsuario().equals(usuario)) {
            throw new NegocioException("Você não pode renovar uma solicitação de outro usuário");
        }

        // Validação de tempo: Só pode renovar se faltar menos de 30 dias para a expiração
        validarPeriodoParaRenovacao(solicitacaoAnterior.getDataExpiracao());

        // Cria nova solicitação baseada na antiga
        Solicitacao novaSolicitacao = instanciarNovaSolicitacao(
                usuario,
                "Renovação da solicitação " + solicitacaoAnterior.getProtocolo(),
                solicitacaoAnterior.getUrgente()
        );

        // Copia os módulos e vincula com solicitação anterior
        novaSolicitacao.setModulosSolicitados(new HashSet<>(solicitacaoAnterior.getModulosSolicitados()));
        novaSolicitacao.setSolicitacaoAnterior(solicitacaoAnterior);

        // Processa regras novamente (pois o cenário pode ter mudado: módulo inativado, etc)
        return processarRegrasESalvar(novaSolicitacao, usuario, List.copyOf(novaSolicitacao.getModulosSolicitados()));
    }

    @Transactional
    public void cancelar(Long usuarioId, Long solicitacaoId, String motivo) {
        // 1. Busca a solicitação e garante que é do usuário logado
        Solicitacao solicitacao = buscarOuFalhar(usuarioId, solicitacaoId);

        // 2. Valida Status (Só pode cancelar se estiver ATIVA)
        if (!StatusSolicitacao.ATIVO.equals(solicitacao.getStatus())) {
            throw new NegocioException(
                    String.format("Solicitação com status %s não pode ser cancelada.", solicitacao.getStatus()));
        }

        // 3. Aplica o Cancelamento
        solicitacao.setStatus(StatusSolicitacao.CANCELADO);
        solicitacao.setMotivoCancelamento(motivo); // Lembre de adicionar o campo na Entidade Solicitacao

        // 4. Revogação Imediata
        // Proteger também filtro por data
        solicitacao.setDataExpiracao(OffsetDateTime.now());

        // Revoga o acesso a módulos feitos na soliciação
        // TODO Passar comportamento para o domain de usuário
        solicitacao.getUsuario()
                .getModulosConcedidos()
                .removeAll(solicitacao.getModulosSolicitados());

        solicitacaoRepository.save(solicitacao);
    }

    private Solicitacao instanciarNovaSolicitacao(Usuario usuario, String justificativa, boolean urgente) {
        Solicitacao solicitacao = new Solicitacao();
        solicitacao.setUsuario(usuario);
        solicitacao.setJustificativa(justificativa);
        solicitacao.setUrgente(urgente);
        solicitacao.setProtocolo(gerarProtocoloUnico());
        return solicitacao;
    }

    private Solicitacao processarRegrasESalvar(Solicitacao solicitacao, Usuario usuario, List<Modulo> modulos) {
        analisarSolicitacao(solicitacao, usuario, modulos);

        if (StatusSolicitacao.ATIVO.equals(solicitacao.getStatus())) {
            solicitacao.setDataExpiracao(OffsetDateTime.now().plusDays(180));
            usuario.getModulosConcedidos().addAll(modulos);
            usuarioRepository.save(usuario);
        }

        solicitacao = solicitacaoRepository.save(solicitacao);

        return solicitacao;
    }

    private void validarJustificativa(String justificativa) {
        // TODO: Poderia ser carregado de uma definição no banco de dados (solicitacao_justificativa_termo...).
        List<String> termosProibidos = List.of("teste", "aaa", "preciso", "acesso");
        String justificativaLowerCase = justificativa.toLowerCase();
        
        // Verifica se a justificativa contém APENAS o termo proibido (exatidão) ou se é muito curta/genérica
        if (termosProibidos.contains(justificativaLowerCase.trim())) {
            throw new NegocioException("Justificativa inválida. Por favor, detalhe o motivo do acesso.");
        }
    }

    private void validarPeriodoParaRenovacao(OffsetDateTime dataExpiracaoAtual) {
        // Pode ser uma nova solicitação, não tendo ainda data de expiração
        if (dataExpiracaoAtual == null) {
            return;
        }

        // Obs.: ChronoUnit porque calcula dias absolutos (não unidades separadas igual Period)
       long diasRestantes = ChronoUnit.DAYS.between(LocalDate.now(), dataExpiracaoAtual.toLocalDate());

        if (diasRestantes > 30) {
            throw new NegocioException(
                    String.format("A renovação só é permitida quando faltarem 30 dias ou menos. Faltam %d dias.", diasRestantes));
        }
    }

    private void validarQuantidadeLimiteModulos(Usuario usuario, Solicitacao solicitacao) {
        int usuarioQuantidadeAtualModulos = usuario.getModulosConcedidos().size();
        int solicitacaoQuantidadeModulos = solicitacao.getModulosSolicitados().size();
        int departamentoQuantidadeLimiteModulos = usuario.getDepartamento().getLimiteModulos();

        if (usuarioQuantidadeAtualModulos + solicitacaoQuantidadeModulos > departamentoQuantidadeLimiteModulos) {
            throw new NegocioException(
                    String.format("Limite de módulos excedido. Seu departamento permite %d, você tem %d e solicitou mais %d.",
                            departamentoQuantidadeLimiteModulos, usuarioQuantidadeAtualModulos, solicitacaoQuantidadeModulos));
        }
    }

    private void analisarSolicitacao(Solicitacao solicitacao, Usuario usuario, List<Modulo> modulos) {
        // Por padrão, aprovada. Se falhar em algo, negada.
        solicitacao.setStatus(StatusSolicitacao.ATIVO);

        // Validar quantidade limite de módulos de acordo com o Departamento do Usuário e Módulos da Solicitação.
        // Validação geral.
        try {
            validarQuantidadeLimiteModulos(usuario, solicitacao);
        } catch (NegocioException e) {
            negar(solicitacao, e.getMessage());
        }

        // Verifica se é um contexto de renovação baseado no estado do objeto
        boolean isRenovacao = solicitacao.getSolicitacaoAnterior() != null;

        for (Modulo modulo : modulos) {
            // Regra: Módulo deve estar ativo/disponível (Usando Boolean.TRUE porque na entity está usando tipo Wrapper)
            if (!Boolean.TRUE.equals(modulo.getAtivo()) || !Boolean.TRUE.equals(modulo.getDisponivel())) {
                negar(solicitacao, "O módulo '" + modulo.getNome() + "' não está disponível para solicitação.");
                return;
            }

            // Regra: Usuário já possui o módulo?
            if (!isRenovacao && usuario.getModulosConcedidos().contains(modulo)) {
                negar(solicitacao, "Você já possui acesso ao módulo '" + modulo.getNome() + "'.");
                return;
            }

            // Regra: Incompatibilidade com Departamento do Usuário (Se houver restrição)
            // Lógica: Se o módulo tem lista de departamentos permitidos E o departamento do usuário NÃO está nela.
            if (!modulo.getDepartamentosPermitidos().isEmpty() 
                    && !modulo.getDepartamentosPermitidos().contains(usuario.getDepartamento())) {
                negar(solicitacao, "O módulo '" + modulo.getNome() + "' não é permitido para o departamento " + usuario.getDepartamento().getNome());
                return;
            }
            
            // Regra : Incompatibilidade entre Módulos (Mutualidade)
            // Lógica: Verifica se o usuário JÁ TEM algum módulo incompatível com o que está pedindo
            for (Modulo jaPossuido : usuario.getModulosConcedidos()) {
                if (!jaPossuido.equals(modulo) && modulo.getModulosIncompativeis().contains(jaPossuido)) {
                    negar(solicitacao, "O módulo '" + modulo.getNome() + "' é incompatível com '" + jaPossuido.getNome() + "' que você já possui.");
                    return;
                }
            }
        }
    }

    private void negar(Solicitacao solicitacao, String motivo) {
        solicitacao.setStatus(StatusSolicitacao.NEGADO);
        solicitacao.setMotivoRejeicao(motivo);
    }

    private String gerarProtocoloUnico() {
        // Formato: SOL-YYYYMMDD-NNNN (4 letras/números aleatórios)
        String data = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String aleatorio = RandomStringUtils.secure().nextAlphanumeric(4);
        return String.format("SOL-%s-%s", data, aleatorio);
    }
}