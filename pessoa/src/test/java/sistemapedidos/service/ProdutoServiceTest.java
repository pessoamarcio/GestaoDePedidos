package sistemapedidos.service;

import sistemapedidos.dto.ProdutoCreateRequest;
import sistemapedidos.exception.NaoEncontradoException;
import sistemapedidos.exception.RegraNegocioException;
import sistemapedidos.model.Produto;
import sistemapedidos.model.enums.StatusProduto;
import sistemapedidos.repository.ProdutoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private ProdutoService produtoService;

    @Test
    void cadastrarDeveSalvarProdutoQuandoNomeNaoExiste() {
        ProdutoCreateRequest request = new ProdutoCreateRequest(
                "Notebook",
                new BigDecimal("4999.90"),
                10
        );
        Produto salvo = new Produto(request.nome(), request.preco(), request.quantidadeEmEstoque(), null);
        when(produtoRepository.existsByNomeIgnoreCase(request.nome())).thenReturn(false);
        when(produtoRepository.save(any(Produto.class))).thenReturn(salvo);

        Produto resultado = produtoService.cadastrar(request);

        assertSame(salvo, resultado);
        ArgumentCaptor<Produto> captor = ArgumentCaptor.forClass(Produto.class);
        verify(produtoRepository).save(captor.capture());
        assertEquals(request.nome(), captor.getValue().getNome());
        assertEquals(request.preco(), captor.getValue().getPreco());
        assertEquals(request.quantidadeEmEstoque(), captor.getValue().getQuantidadeEmEstoque());
        assertEquals(StatusProduto.DISPONIVEL, captor.getValue().getStatus());
    }

    @Test
    void cadastrarDeveLancarExcecaoQuandoNomeJaExiste() {
        ProdutoCreateRequest request = new ProdutoCreateRequest(
                "Notebook",
                new BigDecimal("4999.90"),
                10
        );
        when(produtoRepository.existsByNomeIgnoreCase(request.nome())).thenReturn(true);

        assertThrows(RegraNegocioException.class,
                () -> produtoService.cadastrar(request));
    }

    @Test
    void buscarPorIdDeveRetornarProdutoQuandoEncontrado() {
        UUID id = UUID.randomUUID();
        Produto produto = new Produto("Notebook", new BigDecimal("4999.90"), 10, StatusProduto.DISPONIVEL);
        when(produtoRepository.findById(id)).thenReturn(Optional.of(produto));

        Produto resultado = produtoService.buscarPorId(id);

        assertSame(produto, resultado);
    }

    @Test
    void buscarPorIdDeveLancarExcecaoQuandoNaoEncontrado() {
        UUID id = UUID.randomUUID();
        when(produtoRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NaoEncontradoException.class, () -> produtoService.buscarPorId(id));
    }
}
