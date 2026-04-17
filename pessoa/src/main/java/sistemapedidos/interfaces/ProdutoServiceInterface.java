package sistemapedidos.interfaces;

import sistemapedidos.dto.ProdutoCreateRequest;
import sistemapedidos.model.enums.StatusProduto;
import sistemapedidos.model.Produto;
import java.util.UUID;
import java.util.List;

public interface ProdutoServiceInterface {
	Produto cadastrar(ProdutoCreateRequest request);
	Produto buscarPorId(UUID id);
	Produto adicionarEstoque(UUID id, int quantidade);
	List<Produto> buscarPorNomeEStatus(String nome, StatusProduto status);
}
