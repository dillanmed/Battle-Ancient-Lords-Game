package com.rpgturnos.combate.service;

import com.rpgturnos.combate.model.CombatenteSnapshot;
import com.rpgturnos.combate.model.Inimigo;
import com.rpgturnos.combate.repository.InimigoRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class InimigoService {

    private final InimigoRepository inimigoRepository;

    public InimigoService(InimigoRepository inimigoRepository) {
        this.inimigoRepository = inimigoRepository;
    }

    public List<Inimigo> listarInimigosAtivos() {
        return inimigoRepository.findByAtivoTrue()
                .stream()
                .sorted(Comparator.comparing(Inimigo::getFase).thenComparing(Inimigo::getId))
                .toList();
    }

    public Inimigo buscarPorId(Long id) {
        return inimigoRepository.findById(id)
                .filter(inimigo -> Boolean.TRUE.equals(inimigo.getAtivo()))
                .orElseThrow(() -> new IllegalArgumentException("Inimigo ativo nao encontrado"));
    }

    public List<Inimigo> buscarInimigosDaFase(Integer fase) {
        return inimigoRepository.findByFaseAndAtivoTrue(fase);
    }

    public CombatenteSnapshot criarSnapshotParaFase(Integer fase) {
        Inimigo inimigo = inimigoRepository.findByFaseAndAtivoTrue(fase)
                .stream()
                .min(Comparator.comparing(Inimigo::getId))
                .orElseGet(this::buscarPrimeiroInimigo);

        return CombatenteSnapshot.builder()
                .referenciaOriginalId(inimigo.getId())
                .nome(inimigo.getNome())
                .tipo("INIMIGO")
                .vidaMaxima(inimigo.getVidaMaxima())
                .vidaAtual(inimigo.getVidaMaxima())
                .manaMaxima(0)
                .manaAtual(0)
                .ataque(inimigo.getAtaque())
                .defesa(inimigo.getDefesa())
                .nivel(inimigo.getNivel())
                .build();
    }

    public List<CombatenteSnapshot> criarSnapshotsParaFase(Integer fase) {
        List<Inimigo> inimigos = inimigoRepository.findByFaseAndAtivoTrue(fase)
                .stream()
                .sorted(Comparator.comparing(Inimigo::getId))
                .toList();

        if (inimigos.isEmpty()) {
            inimigos = List.of(buscarPrimeiroInimigo());
        }

        return inimigos.stream()
                .map(this::criarSnapshot)
                .toList();
    }

    private CombatenteSnapshot criarSnapshot(Inimigo inimigo) {
        return CombatenteSnapshot.builder()
                .referenciaOriginalId(inimigo.getId())
                .nome(inimigo.getNome())
                .tipo("INIMIGO")
                .vidaMaxima(inimigo.getVidaMaxima())
                .vidaAtual(inimigo.getVidaMaxima())
                .manaMaxima(0)
                .manaAtual(0)
                .ataque(inimigo.getAtaque())
                .defesa(inimigo.getDefesa())
                .nivel(inimigo.getNivel())
                .build();
    }

    private Inimigo buscarPrimeiroInimigo() {
        return inimigoRepository.findByAtivoTrue()
                .stream()
                .min(Comparator.comparing(Inimigo::getId))
                .orElseThrow(() -> new IllegalStateException("Nenhum inimigo cadastrado"));
    }
}
