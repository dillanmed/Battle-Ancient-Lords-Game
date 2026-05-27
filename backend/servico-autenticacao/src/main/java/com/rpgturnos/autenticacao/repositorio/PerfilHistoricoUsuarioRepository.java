package com.rpgturnos.autenticacao.repositorio;

import com.rpgturnos.autenticacao.modelo.PerfilHistoricoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerfilHistoricoUsuarioRepository extends JpaRepository<PerfilHistoricoUsuario, Long> {
}
