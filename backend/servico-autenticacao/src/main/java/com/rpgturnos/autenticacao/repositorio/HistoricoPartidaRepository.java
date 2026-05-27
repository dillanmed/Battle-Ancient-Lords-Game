package com.rpgturnos.autenticacao.repositorio;

import com.rpgturnos.autenticacao.modelo.HistoricoPartida;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoricoPartidaRepository extends JpaRepository<HistoricoPartida, Long> {

    List<HistoricoPartida> findByUsuarioIdOrderByFinalizadaEmDesc(Long usuarioId);

    Optional<HistoricoPartida> findByBatalhaId(Long batalhaId);
}
