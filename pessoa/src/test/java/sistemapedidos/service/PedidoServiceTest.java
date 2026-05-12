package sistemapedidos.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sistemapedidos.TestReflectionUtils;
import sistemapedidos.dto.PedidoCreateRequest;
import sistemapedidos.dto.PedidoItemRequest;
import sistemapedidos.exception.NaoEncontradoException;
import sistemapedidos.exception.RegraNegocioException;
import sistemapedidos.model.Cliente;
import sistemapedidos.model.ItemPedido;
import sistemapedidos.model.Pedido;
import sistemapedidos.model.Produto;
import sistemapedidos.model.enums.StatusCliente;
import sistemapedidos.model.enums.StatusPedido;
import sistemapedidos.model.enums.StatusProduto;
import sistemapedidos.repository.ClienteRepository;
import sistemapedidos.repository.PedidoRepository;
import sistemapedidos.repository.ProdutoRepository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private PedidoService pedidoService;

    @Test
    void criarPedidoDeveAgruparItensEBaixarEstoque() {
        UUID clienteId = UUID.randomUUID();
        UUID produtoId = UUID.randomUUID();
        Cliente cliente = cliente(clienteId, StatusCliente.ATIVO);
        Produto produto = produto(produtoId, 10, StatusProduto.DISPONIVEL);
        PedidoCreateRequest request = new PedidoCreateRequest(
                clienteId,
                List.of(new PedidoItemRequest(produtoId, 2), new PedidoItemRequest(produtoId, 3))
        );
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(produtoRepository.findAllByIdForUpdate(Set.of(produtoId))).thenReturn(List.of(produto));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Pedido resultado = pedidoService.criarPedido(request);

        assertSame(cliente, resultado.getCliente());
        assertEquals(1, resultado.getItens().size());
        assertEquals(5, resultado.getItens().getFirst().getQuantidade());
        assertEquals(5, produto.getQuantidadeEmEstoque());
        assertEquals(StatusPedido.AGUARDANDO_PAGAMENTO, resultado.getStatus());
        verify(pedidoRepository).save(any(Pedido.class));
    }

    @Test
    void criarPedidoDeveMarcarProdutoComoIndisponivelQuandoEstoqueZerar() {
        UUID clienteId = UUID.randomUUID();
        UUID produtoId = UUID.randomUUID();
        Cliente cliente = cliente(clienteId, StatusCliente.ATIVO);
        Produto produto = produto(produtoId, 5, StatusProduto.DISPONIVEL);
        PedidoCreateRequest request = new PedidoCreateRequest(clienteId, List.of(new PedidoItemRequest(produtoId, 5)));
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(produtoRepository.findAllByIdForUpdate(Set.of(produtoId))).thenReturn(List.of(produto));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        pedidoService.criarPedido(request);

        assertEquals(0, produto.getQuantidadeEmEstoque());
        assertEquals(StatusProduto.INDISPONIVEL, produto.getStatus());
    }

    @Test
    void criarPedidoDeveFalharQuandoClienteInativo() {
        UUID clienteId = UUID.randomUUID();
        UUID produtoId = UUID.randomUUID();
        PedidoCreateRequest request = new PedidoCreateRequest(clienteId, List.of(new PedidoItemRequest(produtoId, 1)));
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente(clienteId, StatusCliente.INATIVO)));

        assertThrows(RegraNegocioException.class, () -> pedidoService.criarPedido(request));
    }

    @Test
    void buscarPorIdDeveRetornarPedidoQuandoEncontrado() {
        UUID pedidoId = UUID.randomUUID();
        Pedido pedido = pedido(pedidoId, cliente(UUID.randomUUID(), StatusCliente.ATIVO), produto(UUID.randomUUID(), 10, StatusProduto.DISPONIVEL), 2);
        when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.of(pedido));

        Pedido resultado = pedidoService.buscarPorId(pedidoId);

        assertSame(pedido, resultado);
    }

    @Test
    void buscarPorIdDeveFalharQuandoNaoEncontrado() {
        UUID pedidoId = UUID.randomUUID();
        when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.empty());

        assertThrows(NaoEncontradoException.class, () -> pedidoService.buscarPorId(pedidoId));
    }

    @Test
    void buscarPorCpfClienteDeveRetornarPedidosDoCliente() {
        String cpf = "12345678901";
        Pedido pedidoMaisNovo = pedido(UUID.randomUUID(), cliente(UUID.randomUUID(), StatusCliente.ATIVO), produto(UUID.randomUUID(), 10, StatusProduto.DISPONIVEL), 2);
        Pedido pedidoMaisAntigo = pedido(UUID.randomUUID(), cliente(UUID.randomUUID(), StatusCliente.ATIVO), produto(UUID.randomUUID(), 10, StatusProduto.DISPONIVEL), 1);
        TestReflectionUtils.setField(pedidoMaisNovo, "criadoEm", OffsetDateTime.now());
        TestReflectionUtils.setField(pedidoMaisAntigo, "criadoEm", OffsetDateTime.now().minusDays(1));
        when(pedidoRepository.findAllByClienteCpfOrderByCriadoEmDesc(cpf)).thenReturn(List.of(pedidoMaisNovo, pedidoMaisAntigo));

        List<Pedido> resultado = pedidoService.buscarPorCpfCliente(cpf);

        assertEquals(2, resultado.size());
        assertSame(pedidoMaisNovo, resultado.getFirst());
        assertSame(pedidoMaisAntigo, resultado.get(1));
    }

    @Test
    void atualizarStatusDeveAlterarParaPagoESalvar() {
        UUID pedidoId = UUID.randomUUID();
        Pedido pedido = pedido(pedidoId, cliente(UUID.randomUUID(), StatusCliente.ATIVO), produto(UUID.randomUUID(), 10, StatusProduto.DISPONIVEL), 2);
        when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(pedido)).thenReturn(pedido);

        Pedido resultado = pedidoService.atualizarStatus(pedidoId, StatusPedido.PAGO);

        assertSame(pedido, resultado);
        assertEquals(StatusPedido.PAGO, resultado.getStatus());
        verify(pedidoRepository).save(pedido);
    }

    @Test
    void atualizarStatusDeveCancelarPedidoDevolverEstoqueEAtualizarStatus() {
        UUID pedidoId = UUID.randomUUID();
        UUID produtoId = UUID.randomUUID();
        Produto produto = produto(produtoId, 0, StatusProduto.INDISPONIVEL);
        Pedido pedido = pedido(pedidoId, cliente(UUID.randomUUID(), StatusCliente.ATIVO), produto, 2);
        when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.of(pedido));
        when(produtoRepository.findAllByIdForUpdate(Set.of(produtoId))).thenReturn(List.of(produto));
        when(pedidoRepository.save(pedido)).thenReturn(pedido);

        Pedido resultado = pedidoService.atualizarStatus(pedidoId, StatusPedido.CANCELADO);

        assertSame(pedido, resultado);
        assertEquals(StatusPedido.CANCELADO, resultado.getStatus());
        assertEquals(2, produto.getQuantidadeEmEstoque());
        assertEquals(StatusProduto.DISPONIVEL, produto.getStatus());
    }

    @Test
    void atualizarStatusDeveRetornarMesmoPedidoQuandoStatusForIgual() {
        UUID pedidoId = UUID.randomUUID();
        Pedido pedido = pedido(pedidoId, cliente(UUID.randomUUID(), StatusCliente.ATIVO), produto(UUID.randomUUID(), 10, StatusProduto.DISPONIVEL), 2);
        TestReflectionUtils.setField(pedido, "status", StatusPedido.PAGO);
        when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.of(pedido));

        Pedido resultado = pedidoService.atualizarStatus(pedidoId, StatusPedido.PAGO);

        assertSame(pedido, resultado);
    }

    @Test
    void atualizarStatusDeveFalharQuandoPedidoJaEstiverPagoEStatusForOutro() {
        UUID pedidoId = UUID.randomUUID();
        Pedido pedido = pedido(pedidoId, cliente(UUID.randomUUID(), StatusCliente.ATIVO), produto(UUID.randomUUID(), 10, StatusProduto.DISPONIVEL), 2);
        TestReflectionUtils.setField(pedido, "status", StatusPedido.PAGO);
        when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.of(pedido));

        assertThrows(RegraNegocioException.class, () -> pedidoService.atualizarStatus(pedidoId, StatusPedido.CANCELADO));
    }

    @Test
    void atualizarStatusDeveFalharQuandoPedidoJaEstiverCanceladoEStatusForOutro() {
        UUID pedidoId = UUID.randomUUID();
        Pedido pedido = pedido(pedidoId, cliente(UUID.randomUUID(), StatusCliente.ATIVO), produto(UUID.randomUUID(), 10, StatusProduto.DISPONIVEL), 2);
        TestReflectionUtils.setField(pedido, "status", StatusPedido.CANCELADO);
        when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.of(pedido));

        assertThrows(RegraNegocioException.class, () -> pedidoService.atualizarStatus(pedidoId, StatusPedido.PAGO));
    }

    @Test
    void atualizarStatusDeveRetornarMesmoPedidoQuandoStatusJaForAguardandoPagamento() {
        UUID pedidoId = UUID.randomUUID();
        Pedido pedido = pedido(pedidoId, cliente(UUID.randomUUID(), StatusCliente.ATIVO), produto(UUID.randomUUID(), 10, StatusProduto.DISPONIVEL), 2);
        when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.of(pedido));

        Pedido resultado = pedidoService.atualizarStatus(pedidoId, StatusPedido.AGUARDANDO_PAGAMENTO);

        assertSame(pedido, resultado);
    }

    @Test
    void criarPedidoDeveFalharQuandoProdutoNaoForEncontrado() {
        UUID clienteId = UUID.randomUUID();
        UUID produtoId = UUID.randomUUID();
        Cliente cliente = cliente(clienteId, StatusCliente.ATIVO);
        PedidoCreateRequest request = new PedidoCreateRequest(clienteId, List.of(new PedidoItemRequest(produtoId, 1)));
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(produtoRepository.findAllByIdForUpdate(Set.of(produtoId))).thenReturn(List.of());

        assertThrows(NaoEncontradoException.class, () -> pedidoService.criarPedido(request));
    }

    @Test
    void criarPedidoDeveFalharQuandoSemItensValidos() {
        UUID clienteId = UUID.randomUUID();
        PedidoCreateRequest request = new PedidoCreateRequest(clienteId, List.of());

        assertThrows(RegraNegocioException.class, () -> pedidoService.criarPedido(request));
    }

    @Test
    void criarPedidoDevePersistirQuantidadeAgrupadaNoItem() {
        UUID clienteId = UUID.randomUUID();
        UUID produtoId = UUID.randomUUID();
        Cliente cliente = cliente(clienteId, StatusCliente.ATIVO);
        Produto produto = produto(produtoId, 10, StatusProduto.DISPONIVEL);
        PedidoCreateRequest request = new PedidoCreateRequest(
                clienteId,
                List.of(new PedidoItemRequest(produtoId, 1), new PedidoItemRequest(produtoId, 2))
        );
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(produtoRepository.findAllByIdForUpdate(Set.of(produtoId))).thenReturn(List.of(produto));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        pedidoService.criarPedido(request);

        ArgumentCaptor<Pedido> captor = ArgumentCaptor.forClass(Pedido.class);
        verify(pedidoRepository).save(captor.capture());
        assertEquals(3, captor.getValue().getItens().getFirst().getQuantidade());
    }

    private static Cliente cliente(UUID id, StatusCliente status) {
        Cliente cliente = new Cliente("Maria", "maria@email.com", "12345678901", status);
        TestReflectionUtils.setField(cliente, "id", id);
        return cliente;
    }

    private static Produto produto(UUID id, int estoque, StatusProduto status) {
        Produto produto = new Produto("Notebook", new BigDecimal("10.00"), estoque, status);
        TestReflectionUtils.setField(produto, "id", id);
        return produto;
    }

    private static Pedido pedido(UUID pedidoId, Cliente cliente, Produto produto, int quantidade) {
        Pedido pedido = new Pedido(cliente);
        TestReflectionUtils.setField(pedido, "id", pedidoId);
        pedido.adicionarItem(new ItemPedido(produto, quantidade, produto.getPreco()));
        return pedido;
    }
}
