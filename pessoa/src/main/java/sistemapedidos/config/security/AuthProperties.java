package sistemapedidos.config.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.auth")
public record AuthProperties(
		String username,
		String password,
		String passwordHash
) {
}
