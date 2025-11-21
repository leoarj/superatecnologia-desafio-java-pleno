package com.github.leoarj.superatecnologia.desafio.domain.service;

import com.github.leoarj.superatecnologia.desafio.domain.model.Modulo;
import com.github.leoarj.superatecnologia.desafio.domain.repository.ModuloRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ModuloService {

    private final ModuloRepository repository;

    public List<Modulo> listarTodosModulos() { return repository.findAll(); }

}
