package sistemapedidos.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import sistemapedidos.config.security.JwtProperties;
import sistemapedidos.dto.auth.AuthLoginRequest;
import sistemapedidos.dto.auth.AuthTokenResponse;
import sistemapedidos.model.Usuario;
import sistemapedidos.repository.UsuarioRepository;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@Mock
	private JwtEncoder jwtEncoder;

	@Mock
	private JwtProperties jwtProperties;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private UsuarioRepository usuarioRepository;

	@Mock
	private UserSessionService userSessionService;

	@InjectMocks
	private AuthService authService;

	@Test
	void loginDeveRetornarAccessESessionToken() {
		Usuario usuario = new Usuario("admin", "hash");
		when(usuarioRepository.findByLogin("admin")).thenReturn(Optional.of(usuario));
		when(passwordEncoder.matches("Senha@@123", "hash")).thenReturn(true);
		when(jwtProperties.expiresInSeconds()).thenReturn(300L);
		when(jwtProperties.issuer()).thenReturn("issuer");
		var session = new sistemapedidos.model.UserSession(usuario, Instant.now(), Instant.now().plusSeconds(300));
		when(userSessionService.issue(usuario)).thenReturn(session);
		when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(
				Jwt.withTokenValue("access-1")
						.header("alg", "HS256")
						.claim("sub", "admin")
						.claim("sid", "session-1")
						.issuedAt(Instant.now())
						.expiresAt(Instant.now().plusSeconds(300))
						.build()
		);

		AuthTokenResponse response = authService.login(new AuthLoginRequest("admin", "Senha@@123"));

		assertEquals("Bearer", response.tokenType());
		assertEquals("access-1", response.accessToken());
		assertEquals(300L, response.expiresInSeconds());
		assertEquals("Login realizado com sucesso.", response.mensagem());
		verify(userSessionService).issue(usuario);
	}

	@Test
	void logoutDeveRevogarSessao() {
		authService.logout("session-1");

		verify(userSessionService).revoke("session-1");
	}

	@Test
	void loginDeveFalharComMensagemEspecificaQuandoSenhaEstiverIncorreta() {
		Usuario usuario = new Usuario("admin", "hash");
		when(usuarioRepository.findByLogin("admin")).thenReturn(Optional.of(usuario));
		when(passwordEncoder.matches("senha-errada", "hash")).thenReturn(false);

		ResponseStatusException ex = assertThrows(ResponseStatusException.class,
				() -> authService.login(new AuthLoginRequest("admin", "senha-errada")));

		assertEquals("Senha incorreta.", ex.getReason());
	}

	@Test
	void loginDeveFalharComMensagemEspecificaQuandoUsuarioNaoExistir() {
		when(usuarioRepository.findByLogin("inexistente")).thenReturn(Optional.empty());

		ResponseStatusException ex = assertThrows(ResponseStatusException.class,
				() -> authService.login(new AuthLoginRequest("inexistente", "senha")));

		assertEquals("Usuario incorreto.", ex.getReason());
	}
}
