package sistemapedidos.model;

import sistemapedidos.model.enums.StatusCliente;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "clientes")
public class Cliente {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(nullable = false)
	private String nome;

	@Column(nullable = false, unique = true)
	private String email;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private StatusCliente status = StatusCliente.ATIVO;

	protected Cliente() {
	}

	public Cliente(String nome, String email) {
		this(nome, email, StatusCliente.ATIVO);
	}

	public Cliente(String nome, String email, StatusCliente status) {
		this.nome = nome;
		this.email = email;
		this.status = status == null ? StatusCliente.ATIVO : status;
	}

	public UUID getId() {
		return id;
	}

	public String getNome() {
		return nome;
	}

	public String getEmail() {
		return email;
	}

	public StatusCliente getStatus() {
		return status;
	}
}
