package sistemapedidos.dto;

import sistemapedidos.model.enums.StatusProduto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record ProdutoCreateRequest(
		@NotBlank(message = "nome é obrigatório")
		String nome,
		@NotNull(message = "preço é obrigatório")
		BigDecimal preco,
		@PositiveOrZero(message = "Quantidade em estoque deve ser maior ou igual a 0")
		int quantidadeEmEstoque,
		StatusProduto status
) {
}
