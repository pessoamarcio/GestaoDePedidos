package sistemapedidos.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import sistemapedidos.model.enums.PerfilUsuario;

@Entity
@Table(name = "usuarios")
public class Usuario extends AuditoriaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "login", nullable = false, unique = true, length = 100)
	private String login;

	// Hash unidirecional (Argon2id). Nunca armazenar senha em texto puro.
	@Column(name = "password_hash", nullable = false, length = 255)
	private String passwordHash;

	@Enumerated(EnumType.STRING)
	@Column(name = "perfil", nullable = false, length = 20)
	private PerfilUsuario perfil;

	protected Usuario() {
	}

	public Usuario(String login, String passwordHash, PerfilUsuario perfil) {
		this.login = login;
		this.passwordHash = passwordHash;
		this.perfil = perfil == null ? PerfilUsuario.CLIENTE : perfil;
	}

	public Long getId() {
		return id;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public PerfilUsuario getPerfil() {
		return perfil;
	}

	public void setPerfil(PerfilUsuario perfil) {
		this.perfil = perfil;
	}
}
