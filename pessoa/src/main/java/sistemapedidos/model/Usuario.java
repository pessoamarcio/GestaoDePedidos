package sistemapedidos.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuarios")
public class Usuario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "login", nullable = false, unique = true, length = 100)
	private String login;

	// Hash unidirecional (Argon2id). Nunca armazenar senha em texto puro.
	@Column(name = "password_hash", nullable = false, length = 255)
	private String passwordHash;

	protected Usuario() {
	}

	public Usuario(String login, String passwordHash) {
		this.login = login;
		this.passwordHash = passwordHash;
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
}

