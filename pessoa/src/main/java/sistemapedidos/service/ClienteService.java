package sistemapedidos.service;

import sistemapedidos.exception.NaoEncontradoException;
import sistemapedidos.exception.RegraNegocioException;
import sistemapedidos.interfaces.ClienteServiceInterface;
import sistemapedidos.model.Cliente;
import sistemapedidos.model.enums.StatusCliente;
import sistemapedidos.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ClienteService implements ClienteServiceInterface {

	private final ClienteRepository clienteRepository;

	public ClienteService(ClienteRepository clienteRepository) {
		this.clienteRepository = clienteRepository;
	}

	@Transactional
	@Override
	public Cliente cadastrar(String nome, String email, StatusCliente status) {
		if (clienteRepository.existsByEmailIgnoreCase(email)) {
			throw new RegraNegocioException("E-mail já cadastrado.");
		}
		return clienteRepository.save(new Cliente(nome, email, status));
	}

	@Transactional(readOnly = true)
	@Override
	public Cliente buscarPorId(UUID id) {
		return clienteRepository.findById(id)
				.orElseThrow(() -> new NaoEncontradoException("Cliente não encontrado: " + id));
	}
}
