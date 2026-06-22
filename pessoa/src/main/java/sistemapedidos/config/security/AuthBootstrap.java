package sistemapedidos.config.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import sistemapedidos.config.SystemAuditorContext;
import sistemapedidos.model.Usuario;
import sistemapedidos.model.enums.PerfilUsuario;
import sistemapedidos.repository.UsuarioRepository;

import java.util.UUID;

/**
 * Cria usuários iniciais para permitir obter token em um ambiente novo.
 * A senha nunca é armazenada: apenas o hash Argon2id.
 */
@Configuration
@ConditionalOnProperty(prefix = "app.auth.bootstrap", name = "enabled", havingValue = "true")
public class AuthBootstrap {

	@Bean
	CommandLineRunner bootstrapUsers(
			UsuarioRepository usuarioRepository,
			PasswordEncoder passwordEncoder,
			AuthProperties authProperties,
			SystemAuditorContext systemAuditorContext
	) {
		return args -> {
			ensureSystemUser(usuarioRepository, passwordEncoder, systemAuditorContext);
			bootstrapUsuario(
					usuarioRepository,
					passwordEncoder,
					authProperties.username(),
					authProperties.password(),
					authProperties.passwordHash(),
					PerfilUsuario.ADMIN
			);
		};
	}

	private void ensureSystemUser(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, SystemAuditorContext systemAuditorContext) {
		Usuario systemUser = usuarioRepository.findByLogin("system").orElse(null);
		if (systemUser != null) {
			systemAuditorContext.setSystemUserId(systemUser.getId());
			return;
		}
		String hash = passwordEncoder.encode("system-" + UUID.randomUUID());
		Usuario saved = usuarioRepository.save(new Usuario("system", hash, PerfilUsuario.ADMIN));
		systemAuditorContext.setSystemUserId(saved.getId());
	}

	private void bootstrapUsuario(
			UsuarioRepository usuarioRepository,
			PasswordEncoder passwordEncoder,
			String login,
			String senha,
			String hashConfigurado,
			PerfilUsuario perfil
	) {
		if (login == null || login.isBlank()) {
			return;
		}
		boolean hasSenha = senha != null && !senha.isBlank();
		boolean hasHash = hashConfigurado != null && !hashConfigurado.isBlank();
		if (hasSenha && hasHash) {
			throw new IllegalStateException("Configure apenas um entre senha e password-hash para " + perfil + ".");
		}
		if (!hasSenha && !hasHash) {
			return;
		}
		if (usuarioRepository.existsByLogin(login)) {
			return;
		}
		String hashParaSalvar = hasHash ? hashConfigurado : passwordEncoder.encode(validateAndReturn(senha, perfil));
		usuarioRepository.save(new Usuario(login, hashParaSalvar, perfil));
	}

	private String validateAndReturn(String senha, PerfilUsuario perfil) {
		PasswordPolicy.validateOrThrow(senha, "app.auth.password." + perfil.name().toLowerCase());
		return senha;
	}
}
