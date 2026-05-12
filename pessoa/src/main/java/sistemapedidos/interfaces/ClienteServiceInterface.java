package sistemapedidos.interfaces;

import sistemapedidos.dto.ClienteCreateRequest;
import sistemapedidos.model.Cliente;

import java.util.List;
import java.util.UUID;

public interface ClienteServiceInterface {
	Cliente cadastrar(ClienteCreateRequest request);
	Cliente buscarPorId(UUID id);
	List<Cliente> buscarPorCpfOuNome(String cpf, String nome);
}
