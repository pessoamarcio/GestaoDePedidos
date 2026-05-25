package sistemapedidos.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sistemapedidos.config.security.AuthProperties;
import sistemapedidos.config.security.JwtProperties;
import sistemapedidos.dto.auth.AuthLoginRequest;
import sistemapedidos.dto.auth.AuthTokenResponse;

import java.time.Instant;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Service
public class AuthService {

	private final JwtEncoder jwtEncoder;
	private final JwtProperties jwtProperties;
	private final AuthProperties authProperties;
	private final PasswordEncoder passwordEncoder;

	public AuthService(
			JwtEncoder jwtEncoder,
			JwtProperties jwtProperties,
			AuthProperties authProperties,
			PasswordEncoder passwordEncoder
	) {
		this.jwtEncoder = jwtEncoder;
		this.jwtProperties = jwtProperties;
		this.authProperties = authProperties;
		this.passwordEncoder = passwordEncoder;
	}

	public AuthTokenResponse login(AuthLoginRequest request) {
		if (!authProperties.username().equals(request.username())) {
			throw new ResponseStatusException(UNAUTHORIZED, "Credenciais invalidas");
		}

		boolean ok;
		if (authProperties.passwordBcrypt() != null && !authProperties.passwordBcrypt().isBlank()) {
			ok = passwordEncoder.matches(request.password(), authProperties.passwordBcrypt());
		} else {
			ok = authProperties.password() != null && authProperties.password().equals(request.password());
		}

		if (!ok) {
			throw new ResponseStatusException(UNAUTHORIZED, "Credenciais invalidas");
		}

		Instant now = Instant.now();
		Instant exp = now.plusSeconds(jwtProperties.expiresInSeconds());

		JwtClaimsSet claims = JwtClaimsSet.builder()
				.issuer(jwtProperties.issuer())
				.issuedAt(now)
				.expiresAt(exp)
				.subject(request.username())
				.claim("roles", new String[]{"ADMIN"})
				.build();

		JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
		String token = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
		return AuthTokenResponse.bearer(token, jwtProperties.expiresInSeconds());
	}
}
