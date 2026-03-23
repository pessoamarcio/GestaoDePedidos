package sistemapedidos.model;

import sistemapedidos.model.enums.StatusProduto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProdutoTest {

    @Test
    void construtorDeveAplicarStatusPadraoQuandoNulo() {
        Produto produto = new Produto("Notebook", new BigDecimal("4999.90"), 10, null);

        assertEquals(StatusProduto.DISPONIVEL, produto.getStatus());
    }

    @Test
    void podeVenderDeveRetornarTrueQuandoDisponivelComEstoqueSuficiente() {
        Produto produto = new Produto("Notebook", new BigDecimal("4999.90"), 10, StatusProduto.DISPONIVEL);

        assertTrue(produto.podeVender(5));
    }

    @Test
    void podeVenderDeveRetornarFalseQuandoIndisponivelOuSemEstoque() {
        Produto indisponivel = new Produto("Notebook", new BigDecimal("4999.90"), 10, StatusProduto.INDISPONIVEL);
        Produto semEstoque = new Produto("Mouse", new BigDecimal("99.90"), 2, StatusProduto.DISPONIVEL);

        assertFalse(indisponivel.podeVender(1));
        assertFalse(semEstoque.podeVender(3));
    }

    @Test
    void baixarEDevolverEstoqueDevemAlterarQuantidade() {
        Produto produto = new Produto("Notebook", new BigDecimal("4999.90"), 10, StatusProduto.DISPONIVEL);

        produto.baixarEstoque(3);
        produto.devolverEstoque(2);

        assertEquals(9, produto.getQuantidadeEmEstoque());
    }
}
