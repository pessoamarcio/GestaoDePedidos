package sistemapedidos.controller;

import sistemapedidos.dto.ClienteCreateRequest;
import sistemapedidos.dto.ClienteResponse;
import sistemapedidos.interfaces.ClienteServiceInterface;
import sistemapedidos.model.Cliente;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

	private final ClienteServiceInterface clienteService;

	public ClienteController(ClienteServiceInterface clienteService) {
		this.clienteService = clienteService;
	}

	@PostMapping
	public ResponseEntity<ClienteResponse> cadastrar(@RequestBody @Valid ClienteCreateRequest request) {
		Cliente cliente = clienteService.cadastrar(
				request.nome(),
				request.email(),
				request.status()
		);
		return ResponseEntity.status(HttpStatus.CREATED).body(ClienteResponse.from(cliente));
	}

	@GetMapping("/{id}")
	public ClienteResponse buscarPorId(@PathVariable UUID id) {
		return ClienteResponse.from(clienteService.buscarPorId(id));
	}
}
