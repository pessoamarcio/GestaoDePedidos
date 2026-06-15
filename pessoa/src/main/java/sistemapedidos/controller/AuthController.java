package sistemapedidos.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sistemapedidos.dto.auth.AuthLoginRequest;
import sistemapedidos.dto.auth.AuthRegisterRequest;
import sistemapedidos.dto.auth.AuthRegisterResponse;
import sistemapedidos.dto.auth.AuthTokenResponse;
import sistemapedidos.service.AuthService;
import sistemapedidos.service.UsuarioAuthService;

import java.net.URI;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth")
public class AuthController {

	private final AuthService authService;
	private final UsuarioAuthService usuarioAuthService;

	public AuthController(AuthService authService, UsuarioAuthService usuarioAuthService) {
		this.authService = authService;
		this.usuarioAuthService = usuarioAuthService;
	}

	@PostMapping("/login")
	@Operation(summary = "Autenticar e obter token JWT")
	public ResponseEntity<AuthTokenResponse> login(@RequestBody @Valid AuthLoginRequest request) {
		return ResponseEntity.ok(authService.login(request));
	}

	@PostMapping("/register")
	@Operation(summary = "Criar usuario (login)")
	public ResponseEntity<AuthRegisterResponse> register(@RequestBody @Valid AuthRegisterRequest request) {
		AuthRegisterResponse response = usuarioAuthService.register(request);
		return ResponseEntity.created(URI.create("/auth/users/" + response.id())).body(response);
	}
}

