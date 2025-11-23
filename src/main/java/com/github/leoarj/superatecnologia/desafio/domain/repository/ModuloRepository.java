package com.github.leoarj.superatecnologia.desafio.domain.repository;

import com.github.leoarj.superatecnologia.desafio.domain.model.Modulo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModuloRepository extends JpaRepository<Modulo, Long> {

    /***
     * findAll() customizado com query para evitar N+1,
     * já que pelos requisitos todos os dados devem ser trazidos na listagem no collection resource (listagem geral).
     * */
    @Query("select distinct m from Modulo m left join fetch m.departamentosPermitidos left join fetch m.modulosIncompativeis")
    List<Modulo> findAllComRelacionamentos();

    @Query("from Modulo m left join fetch m.departamentosPermitidos left join fetch m.modulosIncompativeis where m.id = :id")
    Optional<Modulo> findByIdComRelacionamentos(Long id);
}
