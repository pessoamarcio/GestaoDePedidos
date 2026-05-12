package sistemapedidos.controller;

import sistemapedidos.TestReflectionUtils;
import sistemapedidos.dto.ProdutoCreateRequest;
import sistemapedidos.dto.ProdutoEstoqueRequest;
import sistemapedidos.dto.ProdutoEstoqueResponse;
import sistemapedidos.dto.ProdutoResponse;
import sistemapedidos.interfaces.ProdutoServiceInterface;
import sistemapedidos.model.Produto;
import sistemapedidos.model.enums.StatusProduto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProdutoControllerTest {

    @Mock
    private ProdutoServiceInterface produtoService;

    @InjectMocks
    private ProdutoController produtoController;

    @Test
    void cadastrarDeveRetornarCreatedComProdutoResponse() {
        ProdutoCreateRequest request = new ProdutoCreateRequest(
                "Notebook",
                new BigDecimal("4999.90"),
                10
        );
        Produto produto = new Produto(
                request.nome(),
                request.preco(),
                request.quantidadeEmEstoque(),
                null
        );
        UUID id = UUID.randomUUID();
        TestReflectionUtils.setField(produto, "id", id);
        when(produtoService.cadastrar(request)).thenReturn(produto);

        ResponseEntity<ProdutoResponse> response = produtoController.cadastrar(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertInstanceOf(ProdutoResponse.class, response.getBody());
        assertEquals(id, response.getBody().id());
    }

    @Test
    void buscarPorIdDeveRetornarProdutoResponse() {
        UUID id = UUID.randomUUID();
        Produto produto = new Produto("Notebook", new BigDecimal("4999.90"), 10, StatusProduto.DISPONIVEL);
        TestReflectionUtils.setField(produto, "id", id);
        when(produtoService.buscarPorId(id)).thenReturn(produto);

        ProdutoResponse response = produtoController.buscarPorId(id);

        assertEquals(id, response.id());
        assertEquals("Notebook", response.nome());
        assertEquals(StatusProduto.DISPONIVEL, response.status());
    }

    @Test
    void adicionarEstoqueDeveRetornarResumoDoProdutoComNovaQuantidade() {
        UUID id = UUID.randomUUID();
        Produto produto = new Produto("Notebook", new BigDecimal("4999.90"), 0, StatusProduto.INDISPONIVEL);
        TestReflectionUtils.setField(produto, "id", id);
        produto.devolverEstoque(5);
        ProdutoEstoqueRequest request = new ProdutoEstoqueRequest(5);
        when(produtoService.adicionarEstoque(id, request.quantidade())).thenReturn(produto);

        ProdutoEstoqueResponse response = produtoController.adicionarEstoque(id, request);

        assertEquals(id, response.id());
        assertEquals("Notebook", response.nome());
        assertEquals(5, response.quantidadeEmEstoque());
    }

    @Test
    void buscarPorNomeEStatusDeveRetornarListaFiltrada() {
        UUID id = UUID.randomUUID();
        Produto produto = new Produto("Notebook", new BigDecimal("4999.90"), 10, StatusProduto.DISPONIVEL);
        TestReflectionUtils.setField(produto, "id", id);
        when(produtoService.buscarPorNomeEStatus("Note", StatusProduto.DISPONIVEL)).thenReturn(List.of(produto));

        List<ProdutoResponse> response = produtoController.buscarPorNomeEStatus("Note", StatusProduto.DISPONIVEL);

        assertEquals(1, response.size());
        assertEquals(id, response.get(0).id());
        assertEquals(StatusProduto.DISPONIVEL, response.get(0).status());
    }
}
