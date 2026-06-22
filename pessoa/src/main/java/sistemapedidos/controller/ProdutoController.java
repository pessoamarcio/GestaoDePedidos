package sistemapedidos.controller;

import sistemapedidos.dto.ProdutoCreateRequest;
import sistemapedidos.dto.ProdutoEstoqueRequest;
import sistemapedidos.dto.ProdutoEstoqueResponse;
import sistemapedidos.dto.ProdutoResponse;
import sistemapedidos.interfaces.ProdutoServiceInterface;
import sistemapedidos.model.Produto;
import sistemapedidos.model.enums.StatusProduto;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.UUID;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

	private final ProdutoServiceInterface produtoService;

	public ProdutoController(ProdutoServiceInterface produtoService) {
		this.produtoService = produtoService;
	}

	@PostMapping
	@PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
	public ResponseEntity<ProdutoResponse> cadastrar(@RequestBody @Valid ProdutoCreateRequest request) {
		Produto produto = produtoService.cadastrar(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(ProdutoResponse.from(produto));
	}

	@GetMapping("/{id}")
	@PreAuthorize("isAuthenticated()")
	public ProdutoResponse buscarPorId(@PathVariable UUID id) {
		return ProdutoResponse.from(produtoService.buscarPorId(id));
	}

	@PatchMapping("/{id}/estoque")
	@PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
	public ProdutoEstoqueResponse adicionarEstoque(@PathVariable UUID id, @RequestBody @Valid ProdutoEstoqueRequest request) {
		return ProdutoEstoqueResponse.from(produtoService.adicionarEstoque(id, request.quantidade()));
	}

	@GetMapping
	@PreAuthorize("isAuthenticated()")
	public List<ProdutoResponse> buscarPorNomeEStatus(
			@RequestParam(required = false) String nome,
			@RequestParam(required = false) StatusProduto status
	) {
		return produtoService.buscarPorNomeEStatus(nome, status)
				.stream()
				.map(ProdutoResponse::from)
				.toList();
	}
}
