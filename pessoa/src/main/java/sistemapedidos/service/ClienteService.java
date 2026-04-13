package sistemapedidos.service;

import sistemapedidos.dto.ClienteCreateRequest;
import sistemapedidos.exception.NaoEncontradoException;
import sistemapedidos.exception.RegraNegocioException;
import sistemapedidos.interfaces.ClienteServiceInterface;
import sistemapedidos.model.Cliente;
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
	public Cliente cadastrar(ClienteCreateRequest request) {
		if (clienteRepository.existsByEmailIgnoreCase(request.email())) {
			throw new RegraNegocioException("E-mail já cadastrado.");
		}
		if (clienteRepository.existsByCpf(request.cpf())){
			throw new RegraNegocioException("CPF já cadastrado.");
		}

		return clienteRepository.save(
				new Cliente(request.nome(), request.email(), request.cpf(), request.status())
		);
	}

	@Transactional(readOnly = true)
	@Override
	public Cliente buscarPorId(UUID id) {
		return clienteRepository.findById(id)
				.orElseThrow(() -> new NaoEncontradoException("Cliente não encontrado: " + id));
	}
}
