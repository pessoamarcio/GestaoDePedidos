package sistemapedidos.service;

import sistemapedidos.dto.ProdutoCreateRequest;
import sistemapedidos.exception.NaoEncontradoException;
import sistemapedidos.exception.RegraNegocioException;
import sistemapedidos.interfaces.ProdutoServiceInterface;
import sistemapedidos.model.Produto;
import sistemapedidos.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ProdutoService implements ProdutoServiceInterface {

	private final ProdutoRepository produtoRepository;

	public ProdutoService(ProdutoRepository produtoRepository) {
		this.produtoRepository = produtoRepository;
	}

	@Transactional
	@Override
	public Produto cadastrar(ProdutoCreateRequest request) {
		if (produtoRepository.existsByNomeIgnoreCase(request.nome())) {
			throw new RegraNegocioException("Produto não cadastrado com este nome.");
		}
		return produtoRepository.save(new Produto(
				request.nome(),
				request.preco(),
				request.quantidadeEmEstoque(),
				null
		));
	}

	@Transactional(readOnly = true)
	@Override
	public Produto buscarPorId(UUID id) {
		return produtoRepository.findById(id)
				.orElseThrow(() -> new NaoEncontradoException("Produto não encontrado: " + id));
	}

	@Transactional
	@Override
	public Produto adicionarEstoque(UUID id, int quantidade) {
		Produto produto = produtoRepository.findById(id)
				.orElseThrow(() -> new NaoEncontradoException("Produto não encontrado: " + id));
		produto.devolverEstoque(quantidade);
		return produtoRepository.save(produto);
	}
}
