package sistemapedidos.interfaces;

import sistemapedidos.model.Produto;
import sistemapedidos.model.enums.StatusProduto;
import java.math.BigDecimal;
import java.util.UUID;

public interface ProdutoServiceInterface {
	Produto cadastrar(String nome, BigDecimal preco, int quantidadeEmEstoque, StatusProduto status);
	Produto buscarPorId(UUID id);
}
