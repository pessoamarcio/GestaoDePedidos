package sistemapedidos.config.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import sistemapedidos.model.Usuario;
import sistemapedidos.repository.UsuarioRepository;

/**
 * Cria um usuario admin inicial (se ainda não existir) para permitir obter token
 * em um ambiente novo. A senha nunca é armazenada: apenas o hash Argon2id.
 */
@Configuration
@ConditionalOnProperty(prefix = "app.auth.bootstrap", name = "enabled", havingValue = "true")
public class AuthBootstrap {

	@Bean
	CommandLineRunner bootstrapAdminUser(
			UsuarioRepository usuarioRepository,
			PasswordEncoder passwordEncoder,
			AuthProperties authProperties
	) {
		return args -> {
			String login = authProperties.username();
			String senha = authProperties.password();
			String hashConfigurado = authProperties.passwordHash();

			if (login == null || login.isBlank()) {
				return;
			}

			boolean hasSenha = senha != null && !senha.isBlank();
			boolean hasHash = hashConfigurado != null && !hashConfigurado.isBlank();

			if (hasSenha && hasHash) {
				throw new IllegalStateException("Configure apenas um entre app.auth.password e app.auth.password-hash.");
			}

			if (!hasSenha && !hasHash) {
				return;
			}

			if (usuarioRepository.existsByLogin(login)) {
				return;
			}

			String hashParaSalvar;
			if (hasHash) {
				hashParaSalvar = hashConfigurado;
			} else {
				PasswordPolicy.validateOrThrow(senha, "app.auth.password");
				hashParaSalvar = passwordEncoder.encode(senha);
			}

			usuarioRepository.save(new Usuario(login, hashParaSalvar));
		};
	}
}
