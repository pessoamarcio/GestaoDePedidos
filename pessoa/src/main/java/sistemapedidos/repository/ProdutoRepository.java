package sistemapedidos.repository;

import sistemapedidos.model.Produto;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ProdutoRepository {

	private final ProdutoRepositoryJpa produtoRepositoryJpa;

	public ProdutoRepository(ProdutoRepositoryJpa produtoRepositoryJpa) {
		this.produtoRepositoryJpa = produtoRepositoryJpa;
	}

	public Produto save(Produto produto) {
		return produtoRepositoryJpa.save(produto);
	}

	public Optional<Produto> findById(UUID id) {
		return produtoRepositoryJpa.findById(id);
	}

	public boolean existsByNomeIgnoreCase(String nome) {
		return produtoRepositoryJpa.existsByNomeIgnoreCase(nome);
	}

	public List<Produto> findAllByIdForUpdate(Collection<UUID> ids) {
		return produtoRepositoryJpa.findAllByIdForUpdate(ids);
	}
}
