package sistemapedidos.dto;

import sistemapedidos.model.Cliente;

import java.util.UUID;

public record ClienteResponse(UUID id, String nome, String cpf, String email) {
	public static ClienteResponse from(Cliente cliente) {
		return new ClienteResponse(
				cliente.getId(),
				cliente.getNome(),
				cliente.getCpf(),
				cliente.getEmail()
		);
	}
}
