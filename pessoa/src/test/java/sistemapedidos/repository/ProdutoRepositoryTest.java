package sistemapedidos.repository;

import sistemapedidos.model.Produto;
import sistemapedidos.model.enums.StatusProduto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProdutoRepositoryTest {

    @Mock
    private ProdutoRepositoryJpa produtoRepositoryJpa;

    @InjectMocks
    private ProdutoRepository produtoRepository;

    @Test
    void saveDeveDelegarParaJpa() {
        Produto produto = new Produto("Notebook", new BigDecimal("4999.90"), 10, StatusProduto.DISPONIVEL);
        when(produtoRepositoryJpa.save(produto)).thenReturn(produto);

        Produto resultado = produtoRepository.save(produto);

        assertSame(produto, resultado);
    }

    @Test
    void findByIdDeveDelegarParaJpa() {
        UUID id = UUID.randomUUID();
        Produto produto = new Produto("Notebook", new BigDecimal("4999.90"), 10, StatusProduto.DISPONIVEL);
        when(produtoRepositoryJpa.findById(id)).thenReturn(Optional.of(produto));

        Optional<Produto> resultado = produtoRepository.findById(id);

        assertSame(produto, resultado.orElseThrow());
    }

    @Test
    void existsByNomeIgnoreCaseDeveDelegarParaJpa() {
        when(produtoRepositoryJpa.existsByNomeIgnoreCase("Notebook")).thenReturn(true);

        boolean resultado = produtoRepository.existsByNomeIgnoreCase("Notebook");

        assertTrue(resultado);
    }

    @Test
    void findAllByIdForUpdateDeveDelegarParaJpa() {
        UUID id = UUID.randomUUID();
        List<UUID> ids = List.of(id);
        List<Produto> produtos = List.of(new Produto("Notebook", new BigDecimal("4999.90"), 10, StatusProduto.DISPONIVEL));
        when(produtoRepositoryJpa.findAllByIdForUpdate(ids)).thenReturn(produtos);

        List<Produto> resultado = produtoRepository.findAllByIdForUpdate(ids);

        assertSame(produtos, resultado);
    }
}
