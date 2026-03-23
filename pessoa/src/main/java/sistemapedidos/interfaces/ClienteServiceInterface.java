package sistemapedidos.interfaces;

import sistemapedidos.model.Cliente;
import sistemapedidos.model.enums.StatusCliente;

import java.util.UUID;

public interface ClienteServiceInterface {
	Cliente cadastrar(String nome, String email, StatusCliente status);
	Cliente buscarPorId(UUID id);
}
