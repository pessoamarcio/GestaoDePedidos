# Sistema de Gestão de Pedidos da Empresa Pessoa (API REST)

Backend em Spring Boot + Spring Data JPA para gestão de **clientes**, **produtos** e **pedidos**, com regras de negócio (estoque, status e imutabilidade de pedido pago) aplicadas no service.

## Tecnologias

- Java 21
- Spring Boot (Web, Data JPA, Validation)
- MySQL (runtime)
- H2 (apenas testes)
- OpenAPI/Swagger UI

## Requisitos

- Java 21
- Maven
- MySQL Server (ou ajuste para outro banco, se desejar)

## Executar

```bash
./mvnw spring-boot:run
```

Swagger UI:

- `http://localhost:8080/swagger-ui.html`

## Endpoints

### Clientes

`POST /api/clientes`  
Cria um cliente.

Request:
```json
{
  "nome": "Maria Silva",
  "email": "maria@exemplo.com",
  "cpf": "12345678901",
  "status": "ATIVO"
}
```

Response 201:
```json
{
  "id": "uuid",
  "nome": "Maria Silva",
  "email": "maria@exemplo.com",
  "cpf": "12345678901",
  "status": "ATIVO"
}
```

`GET /api/clientes/{id}`  
Busca cliente por ID.

Response 200:
```json
{
  "id": "uuid",
  "nome": "Maria Silva",
  "email": "maria@exemplo.com",
  "cpf": "12345678901",
  "status": "ATIVO"
}
```

### Produtos

`POST /api/produtos`  
Cria um produto.

Request:
```json
{
  "nome": "Notebook",
  "preco": 3999.90,
  "quantidadeEmEstoque": 10,
  "status": "DISPONIVEL"
}
```

Response 201:
```json
{
  "id": "uuid",
  "nome": "Notebook",
  "preco": 3999.90,
  "quantidadeEmEstoque": 10,
  "status": "DISPONIVEL"
}
```

`GET /api/produtos/{id}`  
Busca produto por ID.

Response 200:
```json
{
  "id": "uuid",
  "nome": "Notebook",
  "preco": 3999.90,
  "quantidadeEmEstoque": 10,
  "status": "DISPONIVEL"
}
```

### Pedidos

`POST /api/pedidos`  
Cria um pedido.

Request:
```json
{
  "clienteId": "uuid",
  "itens": [
    { "produtoId": "uuid", "quantidade": 2 }
  ]
}
```

Response 201:
```json
{
  "id": "uuid",
  "clienteId": "uuid",
  "status": "CRIADO",
  "criadoEm": "2026-03-17T00:00:00-03:00",
  "valorTotal": 7999.80,
  "itens": [
    {
      "produtoId": "uuid",
      "produtoNome": "Notebook",
      "quantidade": 2,
      "precoNoMomentoDaCompra": 3999.90,
      "totalItem": 7999.80
    }
  ]
}
```

`GET /api/pedidos/{id}`  
Busca pedido por ID.

`PUT /api/pedidos/{id}`  
Substitui todos os itens do pedido.

Request:
```json
{
  "itens": [
    { "produtoId": "uuid", "quantidade": 1 }
  ]
}
```

`POST /api/pedidos/{id}/pagar`  
Marca o pedido como PAGO.

`POST /api/pedidos/{id}/cancelar`  
Marca o pedido como CANCELADO.

## Regras de negócio (resumo)

- Cliente: CPF e e-mail devem ser únicos.
- Produto: nome deve ser único.
- Pedido: deve ter ao menos 1 produto.
- Estoque: não permite vender acima da quantidade disponível.
- Status: pedido PAGO não pode ser alterado; pedido CANCELADO não pode ser pago ou alterado.
- Cliente INATIVO não pode criar pedido.

## Banco de dados

Tabelas principais:

- `clientes` (id UUID, nome, email, cpf, status)
- `produtos` (id UUID, nome, preco, quantidade_em_estoque, status)
- `pedidos` (id UUID, cliente_id, status, criado_em)
- `itens_pedido` (id BIGINT, pedido_id, produto_id, quantidade, preco_no_momento_da_compra)

Relacionamentos:

- `clientes` 1:N `pedidos`
- `pedidos` 1:N `itens_pedido`
- `produtos` 1:N `itens_pedido`

Observações:

- `cpf` e `email` são únicos no banco.
- `nome` do produto é validado como único na camada de serviço.
- `itens_pedido` guarda o preço do produto no momento da compra.

## Padrão de repositórios

O projeto separa a classe de repositório (ex: `ClienteRepository`) da interface Spring Data (`ClienteRepositoryJpa`).
As classes concretas delegam para as interfaces `*Jpa` geradas automaticamente pelo Spring Data JPA.

## Erros e validação

Erros de negócio e validação retornam HTTP 400 com payload simples:

```json
{
  "mensagem": "Mensagem de erro."
}
```

Para erros de validação (Bean Validation), o payload inclui os campos inválidos:

```json
{
  "mensagem": "Requisicao invalida.",
  "campos": [
    { "campo": "email", "mensagem": "email inválido" }
  ]
}

```