package com.github.leoarj.superatecnologia.desafio.domain.service;

import com.github.leoarj.superatecnologia.desafio.domain.exception.ModuloNaoEncontradoException;
import com.github.leoarj.superatecnologia.desafio.domain.model.Modulo;
import com.github.leoarj.superatecnologia.desafio.domain.repository.ModuloRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CadastroModuloService {

    private final ModuloRepository moduloRepository;

    public List<Modulo> listar() {
        return moduloRepository.findAll();
    }

    public List<Modulo> listarComRelacionamentos() {
        return moduloRepository.findAllComRelacionamentos();
    }

    public Modulo buscarOuFalhar(Long moduloId) {
        return moduloRepository.findById(moduloId)
                .orElseThrow(() -> new ModuloNaoEncontradoException(moduloId));
    }

}
