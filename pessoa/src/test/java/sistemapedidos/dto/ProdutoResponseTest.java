package sistemapedidos.dto;

import sistemapedidos.TestReflectionUtils;
import sistemapedidos.model.Produto;
import sistemapedidos.model.enums.StatusProduto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProdutoResponseTest {

    @Test
    void fromDeveMapearProduto() {
        Produto produto = new Produto("Notebook", new BigDecimal("4999.90"), 10, StatusProduto.DISPONIVEL);
        UUID id = UUID.randomUUID();
        TestReflectionUtils.setField(produto, "id", id);

        ProdutoResponse response = ProdutoResponse.from(produto);

        assertEquals(id, response.id());
        assertEquals("Notebook", response.nome());
        assertEquals(new BigDecimal("4999.90"), response.preco());
        assertEquals(10, response.quantidadeEmEstoque());
        assertEquals(StatusProduto.DISPONIVEL, response.status());
    }
}
