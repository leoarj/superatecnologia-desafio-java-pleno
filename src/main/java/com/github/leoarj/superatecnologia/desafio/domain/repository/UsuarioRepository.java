package com.github.leoarj.superatecnologia.desafio.domain.repository;

import com.github.leoarj.superatecnologia.desafio.domain.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    // Opcional: query para buscar usuário já com os relacionamentos carregados (evitar N+1)
    // @Query("from Usuario u join fetch u.departamento left join fetch u.modulosConcedidos where u.email = :email")
    // Optional<Usuario> findByEmailComRelacionamentos OuComFetch(String email);

    @Query("from Usuario u join fetch u.departamento left join fetch u.modulosConcedidos where u.id = :id")
    Optional<Usuario> findByIdComRelacionamentos(Long id);
}
