package sistemapedidos.dto;

import sistemapedidos.model.Produto;

import java.util.UUID;

public record ProdutoEstoqueResponse(
        UUID id,
        String nome,
        int quantidadeEmEstoque
) {
    public static ProdutoEstoqueResponse from(Produto produto) {
        return new ProdutoEstoqueResponse(
                produto.getId(),
                produto.getNome(),
                produto.getQuantidadeEmEstoque()
        );
    }
}
