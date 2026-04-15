package sistemapedidos.interfaces;

import sistemapedidos.dto.ProdutoCreateRequest;
import sistemapedidos.model.Produto;
import java.util.UUID;

public interface ProdutoServiceInterface {
	Produto cadastrar(ProdutoCreateRequest request);
	Produto buscarPorId(UUID id);
	Produto adicionarEstoque(UUID id, int quantidade);
}
