package sistemapedidos.config.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import sistemapedidos.service.UserSessionService;

import java.util.Collection;
import java.util.List;

@Component
public class SessionJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

	private final UserSessionService userSessionService;

	public SessionJwtAuthenticationConverter(UserSessionService userSessionService) {
		this.userSessionService = userSessionService;
	}

	@Override
	public AbstractAuthenticationToken convert(Jwt jwt) {
		String sessionId = jwt.getClaimAsString("sid");
		if (sessionId == null || sessionId.isBlank()) {
			throw invalidSession();
		}

		try {
			userSessionService.validate(sessionId);
		} catch (RuntimeException ex) {
			throw invalidSession();
		}

		Collection<?> rolesClaim = jwt.getClaim("roles");
		List<SimpleGrantedAuthority> authorities = (rolesClaim == null ? List.<String>of() : rolesClaim.stream()
						.map(String::valueOf)
						.toList())
				.stream()
				.map(role -> new SimpleGrantedAuthority("ROLE_" + role))
				.toList();

		return new org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken(jwt, authorities);
	}

	private OAuth2AuthenticationException invalidSession() {
		return new OAuth2AuthenticationException(new OAuth2Error("invalid_token"), "Sessao inválida.");
	}
}
