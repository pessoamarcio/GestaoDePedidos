package sistemapedidos.interfaces;

import sistemapedidos.dto.ClienteCreateRequest;
import sistemapedidos.model.Cliente;

import java.util.UUID;

public interface ClienteServiceInterface {
	Cliente cadastrar(ClienteCreateRequest request);
	Cliente buscarPorId(UUID id);
}
