package sistemapedidos.config.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import sistemapedidos.model.UserSession;
import sistemapedidos.model.Usuario;
import sistemapedidos.model.enums.PerfilUsuario;
import sistemapedidos.service.UserSessionService;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SessionJwtAuthenticationConverterTest {

	@Mock
	private UserSessionService userSessionService;

	@InjectMocks
	private SessionJwtAuthenticationConverter converter;

	@Test
	void convertDeveAceitarRolesComoLista() {
		Jwt jwt = Jwt.withTokenValue("token")
				.header("alg", "HS256")
				.claim("sid", "session-1")
				.claim("roles", List.of("ADMIN"))
				.issuedAt(Instant.now())
				.expiresAt(Instant.now().plusSeconds(300))
				.build();

		when(userSessionService.validate("session-1")).thenReturn(
				new UserSession(new Usuario("admin", "hash", PerfilUsuario.ADMIN), Instant.now(), Instant.now().plusSeconds(300))
		);

		AbstractAuthenticationToken authentication = converter.convert(jwt);

		assertEquals(1, authentication.getAuthorities().size());
		assertEquals("ROLE_ADMIN", authentication.getAuthorities().iterator().next().getAuthority());
		verify(userSessionService).validate("session-1");
	}
}
