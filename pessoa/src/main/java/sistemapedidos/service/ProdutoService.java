package sistemapedidos.service;

import sistemapedidos.exception.NaoEncontradoException;
import sistemapedidos.exception.RegraNegocioException;
import sistemapedidos.interfaces.ProdutoServiceInterface;
import sistemapedidos.model.Produto;
import sistemapedidos.model.enums.StatusProduto;
import sistemapedidos.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class ProdutoService implements ProdutoServiceInterface {

	private final ProdutoRepository produtoRepository;

	public ProdutoService(ProdutoRepository produtoRepository) {
		this.produtoRepository = produtoRepository;
	}

	@Transactional
	@Override
	public Produto cadastrar(String nome, BigDecimal preco, int quantidadeEmEstoque, StatusProduto status) {
		if (produtoRepository.existsByNomeIgnoreCase(nome)) {
			throw new RegraNegocioException("Produto já cadastrado com este nome.");
		}
		return produtoRepository.save(new Produto(nome, preco, quantidadeEmEstoque, status));
	}

	@Transactional(readOnly = true)
	@Override
	public Produto buscarPorId(UUID id) {
		return produtoRepository.findById(id)
				.orElseThrow(() -> new NaoEncontradoException("Produto não encontrado: " + id));
	}
}
