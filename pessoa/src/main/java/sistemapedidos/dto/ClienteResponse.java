package sistemapedidos.dto;

import sistemapedidos.model.Cliente;
import sistemapedidos.model.enums.StatusCliente;

import java.util.UUID;

public record ClienteResponse(UUID id, String nome, String cpf, String email, StatusCliente status, EnderecoResponse endereco) {
	public static ClienteResponse from(Cliente cliente) {
		return new ClienteResponse(
				cliente.getId(),
				cliente.getNome(),
				cliente.getCpf(),
				cliente.getEmail(),
				cliente.getStatus(),
				cliente.getEndereco() == null ? null : EnderecoResponse.from(cliente.getEndereco())
		);
	}
}
