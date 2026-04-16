package sistemapedidos.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sistemapedidos.dto.PedidoCreateRequest;
import sistemapedidos.dto.PedidoItemRequest;
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

import java.util.HashMap;
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
    public Pedido criarPedido(PedidoCreateRequest request) {
        Map<UUID, Integer> quantidadePorProduto = validarQuantidades(toQuantidades(request.itens()));

        Cliente cliente = clienteRepository.findById(request.clienteId())
                .orElseThrow(() -> new NaoEncontradoException("Cliente não encontrado: " + request.clienteId()));

        if (cliente.getStatus() != StatusCliente.ATIVO) {
            throw new RegraNegocioException("Não é permitido criar pedido para cliente INATIVO.");
        }

        List<Produto> produtos = produtoRepository.findAllByIdForUpdate(quantidadePorProduto.keySet());
        validarProdutosEncontrados(quantidadePorProduto.keySet(), produtos);

        produtos.stream()
                .filter(produto -> produto.getStatus() != StatusProduto.DISPONIVEL)
                .findFirst()
                .ifPresent(produto -> {
                    throw new RegraNegocioException("Produto INDISPONÍVEL: " + produto.getId());
                });

        produtos.stream()
                .filter(produto -> !produto.podeVender(quantidadePorProduto.get(produto.getId())))
                .findFirst()
                .ifPresent(produto -> {
                    throw new RegraNegocioException("Produto sem estoque: " + produto.getId());
                });

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

    @Transactional(readOnly = true)
    @Override
    public List<Pedido> buscarPorCpfCliente(String cpf) {
        return pedidoRepository.findAllByClienteCpfOrderByCriadoEmDesc(cpf);
    }

    @Transactional
    @Override
    public Pedido atualizarStatus(UUID pedidoId, StatusPedido status) {
        Pedido pedido = buscarPorId(pedidoId);
        if (status == null) {
            throw new RegraNegocioException("Status do pedido é obrigatório.");
        }
        if (pedido.getStatus() == status) {
            return pedido;
        }
        if (pedido.getStatus() == StatusPedido.PAGO) {
            throw new RegraNegocioException("Pedido PAGO não pode ter o status alterado.");
        }
        if (pedido.getStatus() == StatusPedido.CANCELADO) {
            throw new RegraNegocioException("Pedido CANCELADO não pode ter o status alterado.");
        }

        return switch (status) {
            case AGUARDANDO_PAGAMENTO -> throw new RegraNegocioException("Pedido já está aguardando pagamento e não pode retornar para esse status.");
            case PAGO -> pagarPedido(pedido);
            case CANCELADO -> cancelarPedido(pedido);
        };
    }

    private Pedido pagarPedido(Pedido pedido) {
        pedido.pagar();
        return pedidoRepository.save(pedido);
    }

    private Pedido cancelarPedido(Pedido pedido) {
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

    private static Map<UUID, Integer> toQuantidades(List<PedidoItemRequest> itens) {
        Map<UUID, Integer> quantidades = new HashMap<>();
        if (itens == null) {
            return quantidades;
        }
        for (PedidoItemRequest item : itens) {
            if (item == null || item.produtoId() == null) {
                throw new RegraNegocioException("Produto é obrigatório.");
            }
            quantidades.merge(item.produtoId(), item.quantidade(), Integer::sum);
        }
        return quantidades;
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
