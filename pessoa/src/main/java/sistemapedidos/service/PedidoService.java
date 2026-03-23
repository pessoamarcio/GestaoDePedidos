package sistemapedidos.service;

import sistemapedidos.exception.NaoEncontradoException;
import sistemapedidos.exception.RegraNegocioException;
import sistemapedidos.interfaces.PedidoServiceInterface;
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
import sistemapedidos.utils.StreamUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PedidoService implements PedidoServiceInterface {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;

    public PedidoService(
            PedidoRepository pedidoRepository,
            ClienteRepository clienteRepository,
            ProdutoRepository produtoRepository
    ) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
    }

	@Transactional
	@Override
	public Pedido criarPedido(UUID clienteId, Map<UUID, Integer> itens) {
        Map<UUID, Integer> quantidadePorProduto = validarQuantidades(itens);

        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new NaoEncontradoException("Cliente não encontrado: " + clienteId));

        if (cliente.getStatus() != StatusCliente.ATIVO) {
            throw new RegraNegocioException("Não é permitido criar pedido para cliente INATIVO.");
        }

        List<Produto> produtos = produtoRepository.findAllByIdForUpdate(quantidadePorProduto.keySet());
        validarProdutosEncontrados(quantidadePorProduto.keySet(), produtos);

        for (Produto produto : produtos) {
            int quantidade = quantidadePorProduto.get(produto.getId());
            if (produto.getStatus() != StatusProduto.DISPONIVEL) {
                throw new RegraNegocioException("Produto INDISPONÍVEL: " + produto.getId());
            }
            if (!produto.podeVender(quantidade)) {
                throw new RegraNegocioException("Produto sem estoque: " + produto.getId());
            }
        }

        for (Produto produto : produtos) {
            produto.baixarEstoque(quantidadePorProduto.get(produto.getId()));
        }

        Map<UUID, Produto> produtoPorId = produtos.stream()
                .collect(Collectors.toMap(Produto::getId, Function.identity()));

        Pedido pedido = new Pedido(cliente);
        for (var entry : quantidadePorProduto.entrySet()) {
            Produto produto = produtoPorId.get(entry.getKey());
            pedido.adicionarItem(new ItemPedido(produto, entry.getValue(), produto.getPreco()));
        }

        return pedidoRepository.save(pedido);
    }

	@Transactional(readOnly = true)
	@Override
	public Pedido buscarPorId(UUID id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new NaoEncontradoException("Pedido não encontrado: " + id));
    }

	@Transactional
	@Override
	public Pedido substituirItens(UUID pedidoId, Map<UUID, Integer> itens) {
        Map<UUID, Integer> novaQuantidadePorProduto = validarQuantidades(itens);

        Pedido pedido = buscarPorId(pedidoId);

        if (pedido.getStatus() == StatusPedido.PAGO) {
            throw new RegraNegocioException("Pedido PAGO não pode ser alterado.");
        }
        if (pedido.getStatus() == StatusPedido.CANCELADO) {
            throw new RegraNegocioException("Pedido CANCELADO não pode ser alterado.");
        }

        Map<UUID, Integer> quantidadeAtualPorProduto = pedido.getItens().stream()
                .collect(Collectors.toMap(
                        item -> item.getProduto().getId(),
                        ItemPedido::getQuantidade,
                        Integer::sum
                ));

        Set<UUID> idsAfetados = StreamUtils.unionKeys(quantidadeAtualPorProduto, novaQuantidadePorProduto);
        List<Produto> produtos = produtoRepository.findAllByIdForUpdate(idsAfetados);
        validarProdutosEncontrados(idsAfetados, produtos);

        Map<UUID, Produto> produtoPorId = produtos.stream()
                .collect(Collectors.toMap(Produto::getId, Function.identity()));

        for (var entry : quantidadeAtualPorProduto.entrySet()) {
            produtoPorId.get(entry.getKey()).devolverEstoque(entry.getValue());
        }

        for (var entry : novaQuantidadePorProduto.entrySet()) {
            Produto produto = produtoPorId.get(entry.getKey());
            int quantidade = entry.getValue();
            if (produto.getStatus() != StatusProduto.DISPONIVEL) {
                throw new RegraNegocioException("Produto INDISPONÍVEL: " + produto.getId());
            }
            if (!produto.podeVender(quantidade)) {
                throw new RegraNegocioException("Produto sem estoque: " + produto.getId());
            }
        }

        for (var entry : novaQuantidadePorProduto.entrySet()) {
            produtoPorId.get(entry.getKey()).baixarEstoque(entry.getValue());
        }

        List<ItemPedido> novosItens = novaQuantidadePorProduto.entrySet().stream()
                .map(entry -> {
                    Produto produto = produtoPorId.get(entry.getKey());
                    return new ItemPedido(produto, entry.getValue(), produto.getPreco());
                })
                .toList();

        pedido.substituirItens(novosItens);
        return pedidoRepository.save(pedido);
    }

	@Transactional
	@Override
	public Pedido pagar(UUID pedidoId) {
        Pedido pedido = buscarPorId(pedidoId);
        if (pedido.getStatus() == StatusPedido.CANCELADO) {
            throw new RegraNegocioException("Pedido CANCELADO não pode ser pago.");
        }
        pedido.pagar();
        return pedidoRepository.save(pedido);
    }

	@Transactional
	@Override
	public Pedido cancelar(UUID pedidoId) {
        Pedido pedido = buscarPorId(pedidoId);
        if (pedido.getStatus() == StatusPedido.PAGO) {
            throw new RegraNegocioException("Pedido PAGO não pode ser alterado.");
        }
        if (pedido.getStatus() == StatusPedido.CANCELADO) {
            return pedido;
        }

        Map<UUID, Integer> quantidadePorProduto = pedido.getItens().stream()
                .collect(Collectors.toMap(
                        item -> item.getProduto().getId(),
                        ItemPedido::getQuantidade,
                        Integer::sum
                ));

        List<Produto> produtos = produtoRepository.findAllByIdForUpdate(quantidadePorProduto.keySet());
        validarProdutosEncontrados(quantidadePorProduto.keySet(), produtos);

        Map<UUID, Produto> produtoPorId = produtos.stream()
                .collect(Collectors.toMap(Produto::getId, Function.identity()));

        for (var entry : quantidadePorProduto.entrySet()) {
            produtoPorId.get(entry.getKey()).devolverEstoque(entry.getValue());
        }

        pedido.cancelar();
        return pedidoRepository.save(pedido);
    }

    private static Map<UUID, Integer> validarQuantidades(Map<UUID, Integer> itens) {
        if (itens == null || itens.isEmpty()) {
            throw new RegraNegocioException("Pedido deve conter ao menos 1 produto.");
        }
        for (var entry : itens.entrySet()) {
            if (entry.getKey() == null) {
                throw new RegraNegocioException("Produto é obrigatório.");
            }
            Integer quantidade = entry.getValue();
            if (quantidade == null || quantidade <= 0) {
                throw new RegraNegocioException("Quantidade deve ser maior que zero.");
            }
        }
        return itens;
    }

    private static void validarProdutosEncontrados(Set<UUID> idsEsperados, List<Produto> produtosEncontrados) {
        Set<UUID> encontrados = produtosEncontrados.stream().map(Produto::getId).collect(Collectors.toSet());
        for (UUID id : idsEsperados) {
            if (!encontrados.contains(id)) {
                throw new NaoEncontradoException("Produto não encontrado: " + id);
            }
        }
    }
}
