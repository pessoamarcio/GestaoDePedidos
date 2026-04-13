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

	@Column(nullable = false, unique = true)
	private String cpf;

	@Column(nullable = false)
	private String nome;

	@Column(nullable = false, unique = true)
	private String email;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private StatusCliente status = StatusCliente.ATIVO;

	protected Cliente() {
	}

	public Cliente(String nome, String email, String cpf) {
		this(nome, email, cpf, StatusCliente.ATIVO);
	}

	public Cliente(String nome, String email, String cpf, StatusCliente status) {
		this.nome = nome;
		this.email = email;
		this.cpf = cpf;
		this.status = status == null ? StatusCliente.ATIVO : status;
	}

	public UUID getId() {
		return id;
	}

	public String getCpf() {
		return cpf;
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
