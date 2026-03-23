package sistemapedidos.controller;

import sistemapedidos.dto.ProdutoCreateRequest;
import sistemapedidos.dto.ProdutoResponse;
import sistemapedidos.interfaces.ProdutoServiceInterface;
import sistemapedidos.model.Produto;
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
@RequestMapping("/api/produtos")
public class ProdutoController {

	private final ProdutoServiceInterface produtoService;

	public ProdutoController(ProdutoServiceInterface produtoService) {
		this.produtoService = produtoService;
	}

	@PostMapping
	public ResponseEntity<ProdutoResponse> cadastrar(@RequestBody @Valid ProdutoCreateRequest request) {
		Produto produto = produtoService.cadastrar(
				request.nome(),
				request.preco(),
				request.quantidadeEmEstoque(),
				request.status()
		);
		return ResponseEntity.status(HttpStatus.CREATED).body(ProdutoResponse.from(produto));
	}

	@GetMapping("/{id}")
	public ProdutoResponse buscarPorId(@PathVariable UUID id) {
		return ProdutoResponse.from(produtoService.buscarPorId(id));
	}
}
