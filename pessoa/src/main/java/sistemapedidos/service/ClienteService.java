package sistemapedidos.service;

import sistemapedidos.dto.ClienteCreateRequest;
import sistemapedidos.dto.EnderecoRequest;
import sistemapedidos.exception.NaoEncontradoException;
import sistemapedidos.exception.RegraNegocioException;
import sistemapedidos.interfaces.ClienteServiceInterface;
import sistemapedidos.model.Cliente;
import sistemapedidos.model.Endereco;
import sistemapedidos.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
				new Cliente(request.nome(), request.email(), request.cpf(), toEndereco(request.endereco()))
		);
	}

	private Endereco toEndereco(EnderecoRequest request) {
		return new Endereco(
				request.logradouro(),
				request.numero(),
				request.complemento(),
				request.bairro(),
				request.cidade(),
				request.estado(),
				request.cep()
		);
	}

	@Transactional(readOnly = true)
	@Override
	public Cliente buscarPorId(UUID id) {
		return clienteRepository.findById(id)
				.orElseThrow(() -> new NaoEncontradoException("Cliente não encontrado: " + id));
	}
	@Transactional(readOnly = true)
	@Override
	public List<Cliente> buscarPorCpfOuNome(String cpf, String nome) {
		if (cpf != null && !cpf.isBlank()) {
			return clienteRepository.findByCpf(cpf)
					.map(List::of)
					.orElseGet(List::of);
		}
		if (nome != null && !nome.isBlank()) {
			return clienteRepository.findByNomeContainingIgnoreCase(nome);
		}
		return clienteRepository.findAll();
	}
}
