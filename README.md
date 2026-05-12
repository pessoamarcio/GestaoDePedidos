# Sistema de Gestao de Pedidos da Empresa Pessoa (API REST)

Backend em Spring Boot + Spring Data JPA para gestao de clientes, produtos e pedidos, com regras de negocio de estoque, status e imutabilidade de pedido pago aplicadas na camada de serviço.

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

`GET /api/clientes?cpf=12345678901`  
Busca cliente por CPF.

Response 200:
```json
[
  {
    "id": "uuid",
    "nome": "Maria Silva",
    "email": "maria@exemplo.com",
    "cpf": "12345678901",
    "status": "ATIVO"
  }
]
```

`GET /api/clientes?nome=Maria`  
Busca clientes por nome.

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

`PATCH /api/produtos/{id}/estoque`  
Adiciona estoque ao produto.

Request:
```json
{
  "quantidade": 5
}
```

`GET /api/produtos?nome=Note`  
Busca produtos por nome.

`GET /api/produtos?status=DISPONIVEL`  
Busca produtos por status.

`GET /api/produtos?nome=Note&status=DISPONIVEL`  
Busca produtos combinando nome e status.

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
  "clienteNome": "Maria Silva",
  "status": "AGUARDANDO_PAGAMENTO",
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
Atualiza o status do pedido.

Request:
```json
{
  "status": "PAGO"
}
```

## Regras de negocio

- Cliente: CPF e e-mail devem ser únicos.
- Cliente: busca pode ser feita por CPF ou nome.
- Produto: nome deve ser único.
- Produto: busca pode ser feita por nome, status ou combinando ambos.
- Pedido: deve ter ao menos 1 produto.
- Estoque: não permite vender acima da quantidade disponível.
- Status: pedido nasce como `AGUARDANDO_PAGAMENTO` e so pode ir para `PAGO` ou `CANCELADO`; pedidos `PAGO` e `CANCELADO` nao podem ser alterados.
- Cliente `INATIVO` não pode criar pedido.

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

Observacoes:

- `cpf` e `email` sao únicos no banco.
- `nome` do produto e validado como unico na camada de serviço.
- `itens_pedido` guarda o preço do produto no momento da compra.

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
    { "campo": "email", "mensagem": "email invalido" }
  ]
}
```
# Docker

## Comandos

Criar (buildar) as imagens:

```bash
docker compose build
```

Start (subir a aplicacao e um MySQL local):

```bash
docker compose up -d
```

Stop (parar os containers):

```bash
docker compose stop
```

Atualizar (atualizar a imagem do container):

```bash
docker compose pull
```

Drop (derrubar e remover containers, rede e volumes do projeto):

```bash
docker compose down -v
```


Endpoints Swagger:
- Swagger: http://localhost:8080/swagger-ui.html
