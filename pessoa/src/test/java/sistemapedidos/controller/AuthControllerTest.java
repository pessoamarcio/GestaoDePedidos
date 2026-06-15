package sistemapedidos.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import sistemapedidos.dto.auth.AuthForgotPasswordRequest;
import sistemapedidos.dto.auth.AuthMessageResponse;
import sistemapedidos.service.AuthService;
import sistemapedidos.service.UsuarioAuthService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

	@Mock
	private AuthService authService;

	@Mock
	private UsuarioAuthService usuarioAuthService;

	@InjectMocks
	private AuthController authController;

	@Test
	void logoutDeveRetornarMensagem() {
		Jwt jwt = Jwt.withTokenValue("token")
				.header("alg", "HS256")
				.claim("sid", "session-1")
				.build();
		ResponseEntity<AuthMessageResponse> response = authController.logout(new JwtAuthenticationToken(jwt));

		assertEquals(200, response.getStatusCode().value());
		assertEquals("Logout realizado com sucesso.", response.getBody().mensagem());
	}

	@Test
	void forgotPasswordDeveRetornarMensagem() {
		when(usuarioAuthService.forgotPassword(new AuthForgotPasswordRequest("admin", "Nova@@123")))
				.thenReturn(AuthMessageResponse.of("Senha alterada com sucesso."));
		ResponseEntity<AuthMessageResponse> response = authController.forgotPassword(new AuthForgotPasswordRequest("admin", "Nova@@123"));

		assertEquals(200, response.getStatusCode().value());
		assertEquals("Senha alterada com sucesso.", response.getBody().mensagem());
		verify(usuarioAuthService).forgotPassword(new AuthForgotPasswordRequest("admin", "Nova@@123"));
	}
}
