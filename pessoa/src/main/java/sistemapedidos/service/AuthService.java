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

	public AuthService(
			JwtEncoder jwtEncoder,
			JwtProperties jwtProperties,
			PasswordEncoder passwordEncoder,
			UsuarioRepository usuarioRepository
	) {
		this.jwtEncoder = jwtEncoder;
		this.jwtProperties = jwtProperties;
		this.passwordEncoder = passwordEncoder;
		this.usuarioRepository = usuarioRepository;
	}

	public AuthTokenResponse login(AuthLoginRequest request) {
		Usuario usuario = usuarioRepository.findByLogin(request.username())
				.orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "Credenciais inválidas"));

		boolean ok = passwordEncoder.matches(request.password(), usuario.getPasswordHash());

		if (!ok) {
			throw new ResponseStatusException(UNAUTHORIZED, "Credenciais inválidas");
		}

		Instant now = Instant.now();
		Instant exp = now.plusSeconds(jwtProperties.expiresInSeconds());

		JwtClaimsSet claims = JwtClaimsSet.builder()
				.issuer(jwtProperties.issuer())
				.issuedAt(now)
				.expiresAt(exp)
				.subject(usuario.getLogin())
				.claim("roles", new String[]{"ADMIN"})
				.build();

		JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
		String token = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
		return AuthTokenResponse.bearer(token, jwtProperties.expiresInSeconds());
	}
}
