package com.rpgturnos.autenticacao.controlador;

import com.rpgturnos.autenticacao.dto.AuthResponse;
import com.rpgturnos.autenticacao.dto.CadastroRequest;
import com.rpgturnos.autenticacao.dto.LoginRequest;
import com.rpgturnos.autenticacao.dto.PartidaHistoricoResponse;
import com.rpgturnos.autenticacao.dto.PerfilResponse;
import com.rpgturnos.autenticacao.modelo.Usuario;
import com.rpgturnos.autenticacao.servico.AuthService;
import com.rpgturnos.autenticacao.servico.HistoricoUsuarioService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final HistoricoUsuarioService historicoUsuarioService;

    public AuthController(AuthService authService, HistoricoUsuarioService historicoUsuarioService) {
        this.authService = authService;
        this.historicoUsuarioService = historicoUsuarioService;
    }

    @PostMapping("/cadastro")
    public ResponseEntity<AuthResponse> cadastrar(@Valid @RequestBody CadastroRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.cadastrar(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<PerfilResponse> perfil(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(historicoUsuarioService.montarPerfil(usuario));
    }

    @GetMapping("/me/historico")
    public ResponseEntity<List<PartidaHistoricoResponse>> historico(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(historicoUsuarioService.listarHistorico(usuario.getId()));
    }
}
