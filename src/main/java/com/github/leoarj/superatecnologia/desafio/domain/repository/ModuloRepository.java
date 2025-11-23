package com.github.leoarj.superatecnologia.desafio.domain.repository;

import com.github.leoarj.superatecnologia.desafio.domain.model.Modulo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModuloRepository extends JpaRepository<Modulo, Long> {

    /***
     * findAll() customizado com query para evitar N+1,
     * já que pelos requisitos todos os dados devem ser trazidos a listagem no collection resource (listagem geral).
     * */
    @Query("select distinct m from Modulo m left join fetch m.departamentosPermitidos left join fetch m.modulosIncompativeis")
    List<Modulo> findAll();
}
