package com.rpgturnos.autenticacao.servico;

import com.rpgturnos.autenticacao.dto.AuthResponse;
import com.rpgturnos.autenticacao.dto.CadastroRequest;
import com.rpgturnos.autenticacao.dto.LoginRequest;
import com.rpgturnos.autenticacao.dto.RecuperacaoSenhaResponse;
import com.rpgturnos.autenticacao.excecao.EmailJaCadastradoException;
import com.rpgturnos.autenticacao.modelo.Usuario;
import com.rpgturnos.autenticacao.repositorio.UsuarioRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public AuthResponse cadastrar(CadastroRequest request) {
        String email = normalizarEmail(request.email());

        if (usuarioRepository.existsByEmail(email)) {
            throw new EmailJaCadastradoException(email);
        }

        Usuario usuario = new Usuario(
                request.login().trim(),
                email,
                passwordEncoder.encode(request.senha())
        );

        Usuario salvo = usuarioRepository.save(usuario);
        return toResponse(salvo);
    }

    public AuthResponse login(LoginRequest request) {
        String email = resolverEmailLogin(request.email());

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, request.senha()));
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow();

        return toResponse(usuario);
    }

    public RecuperacaoSenhaResponse solicitarRecuperacaoSenha(String emailInformado) {
        String email = normalizarEmail(emailInformado);

        usuarioRepository.findByEmail(email).ifPresent(usuario -> {
            // Ponto de integracao com envio de email ou Supabase Edge Function.
        });

        return new RecuperacaoSenhaResponse("Se o email existir, enviaremos as instrucoes de recuperacao.");
    }

    public String extrairEmailDoToken(String token) {
        return jwtService.extrairEmail(token);
    }

    public boolean tokenValido(String token, UserDetails userDetails) {
        return jwtService.tokenValido(token, userDetails);
    }

    private AuthResponse toResponse(Usuario usuario) {
        return new AuthResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                "Bearer",
                jwtService.gerarToken(usuario)
        );
    }

    private String normalizarEmail(String email) {
        return email.trim().toLowerCase();
    }

    private String resolverEmailLogin(String loginOuEmail) {
        String valor = loginOuEmail.trim();
        if (valor.contains("@")) {
            return normalizarEmail(valor);
        }

        return usuarioRepository.findByNome(valor)
                .map(Usuario::getEmail)
                .orElseThrow(() -> new BadCredentialsException("Login inexistente"));
    }
}
