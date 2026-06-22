package sistemapedidos.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sistemapedidos.dto.auth.AuthLoginRequest;
import sistemapedidos.dto.auth.AuthForgotPasswordRequest;
import sistemapedidos.dto.auth.AuthMessageResponse;
import sistemapedidos.dto.auth.AuthRegisterRequest;
import sistemapedidos.dto.auth.AuthRegisterResponse;
import sistemapedidos.dto.auth.AuthUserCreateRequest;
import sistemapedidos.dto.auth.AuthTokenResponse;
import sistemapedidos.model.enums.PerfilUsuario;
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
	@Operation(
			summary = "Criar usuário público",
			description = "Cadastro aberto que cria sempre um usuário com perfil CLIENTE."
	)
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Cliente criado"),
			@ApiResponse(responseCode = "400", description = "Requisição inválida")
	})
	public ResponseEntity<AuthRegisterResponse> register(@RequestBody @Valid AuthRegisterRequest request) {
		AuthRegisterResponse response = usuarioAuthService.register(request);
		return ResponseEntity.created(URI.create("/auth/users/" + response.id())).body(response);
	}

	@PostMapping("/users")
	@PreAuthorize("hasRole('ADMIN')")
	@SecurityRequirement(name = "bearerAuth")
	@Operation(
			summary = "Criar usuário com perfil",
			description = "Cadastro administrativo. O perfil deve ser informado explicitamente."
	)
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Usuário criado"),
			@ApiResponse(responseCode = "400", description = "Requisição inválida"),
			@ApiResponse(responseCode = "401", description = "Não autenticado"),
			@ApiResponse(responseCode = "403", description = "Sem permissão")
	})
	public ResponseEntity<AuthRegisterResponse> createUser(@RequestBody @Valid AuthUserCreateRequest request) {
		AuthRegisterResponse response = usuarioAuthService.createUser(request);
		return ResponseEntity.created(URI.create("/auth/users/" + response.id())).body(response);
	}

	@PostMapping("/forgot-password")
	@Operation(summary = "Redefinir senha do usuário ")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Senha redefinida com sucesso"),
			@ApiResponse(responseCode = "404", description = "usuário não encontrado"),
			@ApiResponse(responseCode = "400", description = "Requisicao invalida")
	})
	public ResponseEntity<AuthMessageResponse> forgotPassword(@RequestBody @Valid AuthForgotPasswordRequest request) {
		return ResponseEntity.ok(usuarioAuthService.forgotPassword(request));
	}

	@PostMapping("/logout")
	@Operation(summary = "Encerrar a sessão local do usuário ")
	@SecurityRequirement(name = "bearerAuth")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Logout local executado"),
			@ApiResponse(responseCode = "401", description = "Token ausente, inválido ou sessão revogada")
	})
	public ResponseEntity<AuthMessageResponse> logout(Authentication authentication) {
		var token = (org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken) authentication;
		authService.logout(token.getToken().getClaimAsString("sid"));
		return ResponseEntity.ok(AuthMessageResponse.of("Logout realizado com sucesso."));
	}
}
