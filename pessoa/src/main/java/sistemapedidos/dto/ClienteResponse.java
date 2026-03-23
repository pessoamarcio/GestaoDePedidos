package sistemapedidos.dto;

import sistemapedidos.model.Cliente;
import sistemapedidos.model.enums.StatusCliente;

import java.util.UUID;

public record ClienteResponse(UUID id, String nome, String email, StatusCliente status) {
	public static ClienteResponse from(Cliente cliente) {
		return new ClienteResponse(
				cliente.getId(),
				cliente.getNome(),
				cliente.getEmail(),
				cliente.getStatus()
		);
	}
}
