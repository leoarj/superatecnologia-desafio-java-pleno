package com.github.leoarj.superatecnologia.desafio.domain.service;

import com.github.leoarj.superatecnologia.desafio.domain.exception.ModuloNaoEncontradoException;
import com.github.leoarj.superatecnologia.desafio.domain.model.Modulo;
import com.github.leoarj.superatecnologia.desafio.domain.repository.ModuloRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class CadastroModuloServiceTest {

    @InjectMocks
    private CadastroModuloService cadastroModuloService;

    @Mock
    private ModuloRepository moduloRepository;

    @Test
    void listar_DeveRetornarListaDeModulos_QuantoExistiremModulos() {
        Modulo moduloFinanceiro = new Modulo();
        moduloFinanceiro.setId(1L);
        moduloFinanceiro.setNome("Financeiro");

        Modulo moduloRh = new Modulo();
        moduloRh.setId(2L);
        moduloRh.setNome("RH");

        Mockito.when(
                moduloRepository.findAll())
                .thenReturn(List.of(moduloFinanceiro, moduloRh));

        List<Modulo> modulosExistentes = cadastroModuloService.listar();

        assertThat(modulosExistentes).isNotEmpty();
        assertThat(modulosExistentes).hasSize(2);
        assertThat(modulosExistentes.get(0).getNome()).isEqualTo("Financeiro");
    }

    @Test
    void buscarOuFalhar_DeveRetornarModulo_QuantoExistir() {
        Long moduloId = 1L;
        Modulo moduloMock = new Modulo();
        moduloMock.setId(1L);

        Mockito.when(moduloRepository.findById(moduloId))
                .thenReturn(Optional.of(moduloMock));

        Modulo moduloProcurado = cadastroModuloService.buscarOuFalhar(moduloId);

        assertThat(moduloProcurado).isNotNull();
        assertThat(moduloProcurado.getId()).isEqualTo(moduloId);
    }

    @Test
    void buscarOuFalhar_DeveLancarException_QuandoIdNaoExistir() {
        Long moduloId = 99L;
        Mockito.when(moduloRepository.findById(moduloId)).thenReturn(Optional.empty());

        Assertions.assertThrows(ModuloNaoEncontradoException.class, () -> {
            cadastroModuloService.buscarOuFalhar(moduloId);
        });
    }
}
