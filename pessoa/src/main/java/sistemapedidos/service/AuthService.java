package sistemapedidos.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sistemapedidos.config.security.JwtProperties;
import sistemapedidos.dto.auth.AuthLoginRequest;
import sistemapedidos.dto.auth.AuthTokenResponse;
import sistemapedidos.model.UserSession;
import sistemapedidos.model.Usuario;
import sistemapedidos.repository.UsuarioRepository;

import java.time.Instant;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Service
public class AuthService {

	private final JwtEncoder jwtEncoder;
	private final JwtProperties jwtProperties;
	private final PasswordEncoder passwordEncoder;
	private final UsuarioRepository usuarioRepository;
	private final UserSessionService userSessionService;

	public AuthService(
			JwtEncoder jwtEncoder,
			JwtProperties jwtProperties,
			PasswordEncoder passwordEncoder,
			UsuarioRepository usuarioRepository,
			UserSessionService userSessionService
	) {
		this.jwtEncoder = jwtEncoder;
		this.jwtProperties = jwtProperties;
		this.passwordEncoder = passwordEncoder;
		this.usuarioRepository = usuarioRepository;
		this.userSessionService = userSessionService;
	}

	public AuthTokenResponse login(AuthLoginRequest request) {
		Usuario usuario = usuarioRepository.findByLogin(request.username())
				.orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "Usuário incorreto."));

		boolean ok = passwordEncoder.matches(request.password(), usuario.getPasswordHash());
		if (!ok) {
			throw new ResponseStatusException(UNAUTHORIZED, "Senha incorreta.");
		}

		UserSession session = userSessionService.issue(usuario);
		return issueTokens(usuario, session.getSessionId());
	}

	public void logout(String sessionId) {
		userSessionService.revoke(sessionId);
	}

	private AuthTokenResponse issueTokens(Usuario usuario, String sessionId) {
		Instant now = Instant.now();
		Instant exp = now.plusSeconds(jwtProperties.expiresInSeconds());

		JwtClaimsSet claims = JwtClaimsSet.builder()
				.issuer(jwtProperties.issuer())
				.issuedAt(now)
				.expiresAt(exp)
				.subject(usuario.getLogin())
				.id(sessionId)
				.claim("sid", sessionId)
				.claim("roles", new String[]{"ADMIN"})
				.build();

		JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
		String accessToken = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
		return AuthTokenResponse.bearer(accessToken, jwtProperties.expiresInSeconds());
	}
}
