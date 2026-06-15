package sistemapedidos.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.proc.SecurityContext;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableConfigurationProperties({JwtProperties.class, AuthProperties.class})
public class SecurityConfig {

	@Bean
	public PasswordEncoder passwordEncoder() {
		// Argon2id: hash especifico para senha (unidirecional, lento e memory-hard).
		// Parametros: salt=16, hash=32, parallelism=1, memory=64MB, iterations=3.
		return new Argon2PasswordEncoder(16, 32, 1, 65536, 3);
	}

	@Bean
	public SecretKey jwtSecretKey(JwtProperties props) {
		if (props.secret() == null || props.secret().isBlank()) {
			throw new IllegalStateException("JWT secret não pode ser vazio.");
		}
		byte[] bytes = props.secret().getBytes(StandardCharsets.UTF_8);
		if (bytes.length < 32) {
			throw new IllegalStateException("JWT secret precisa ter pelo menos 32 caracteres.");
		}
		return new SecretKeySpec(bytes, "HmacSHA256");
	}

	@Bean
	public JwtEncoder jwtEncoder(SecretKey secretKey) {
		OctetSequenceKey jwk;
		try {
			jwk = new OctetSequenceKey.Builder(secretKey)
					.keyUse(KeyUse.SIGNATURE)
					.algorithm(JWSAlgorithm.HS256)
					.keyID("hmac-key")
					.build();
		} catch (Exception e) {
			throw new IllegalStateException("Falha ao criar JWK.", e);
		}

		ImmutableJWKSet<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(jwk));
		return new NimbusJwtEncoder(jwkSource);
	}

	@Bean
	public JwtDecoder jwtDecoder(SecretKey secretKey) {
		return NimbusJwtDecoder.withSecretKey(secretKey).build();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, SessionJwtAuthenticationConverter sessionJwtAuthenticationConverter) throws Exception {
		return http
				.csrf(csrf -> csrf.disable())
				.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(HttpMethod.POST, "/auth/logout").authenticated()
						.requestMatchers(
								"/auth/**",
								"/swagger-ui.html",
								"/swagger-ui/**",
								"/v3/api-docs/**"
						).permitAll()
						.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
						.requestMatchers("/api/**").authenticated()
						.anyRequest().permitAll()
				)
				.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(sessionJwtAuthenticationConverter)))
				.build();
	}
}
