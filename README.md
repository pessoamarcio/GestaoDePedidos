# Sistema de Gestão de Pedidos da Empresa Pessoa (API REST)

Backend em Spring Boot + Spring Data JPA para gestão de clientes, produtos e pedidos, com regras de negócio de estoque, status e imutabilidade de pedido pago aplicadas na camada de serviço.

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
Cria um cliente com endereço embutido no request.

Request:
```json
{
  "nome": "Maria Silva",
  "email": "maria@exemplo.com",
  "cpf": "12345678901",
  "endereco": {
    "logradouro": "Rua A",
    "numero": "10",
    "complemento": "Apto 12",
    "bairro": "Centro",
    "cidade": "São Paulo",
    "estado": "SP",
    "cep": "01001000"
  }
}
```

Response 201:
```json
{
  "id": "uuid",
  "nome": "Maria Silva",
  "email": "maria@exemplo.com",
  "cpf": "12345678901",
  "status": "ATIVO",
  "endereco": {
    "id": "uuid",
    "logradouro": "Rua A",
    "numero": "10",
    "complemento": "Apto 12",
    "bairro": "Centro",
    "cidade": "São Paulo",
    "estado": "SP",
    "cep": "01001000"
  }
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
  "status": "ATIVO",
  "endereco": {
    "id": "uuid",
    "logradouro": "Rua A",
    "numero": "10",
    "complemento": "Apto 12",
    "bairro": "Centro",
    "cidade": "São Paulo",
    "estado": "SP",
    "cep": "01001000"
  }
}
```

`GET /api/clientes?cpf=12345678901`
Busca cliente por CPF.

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
  "quantidadeEmEstoque": 10
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

`GET /api/produtos?nome=Notebook`
Busca produtos por nome.

`GET /api/produtos?status=DISPONIVEL`
Busca produtos por status.

`GET /api/produtos?nome=Notebook&status=DISPONIVEL`
Busca produtos combinando nome e status.

`PATCH /api/produtos/{id}/estoque`
Adiciona quantidade ao estoque do produto.

Request:
```json
{
  "quantidade": 5
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
  "status": "AGUARDANDO_PAGAMENTO",
  "itens": [
    { "produtoId": "uuid", "quantidade": 2 }
  ]
}
```

`GET /api/pedidos/{id}`
Busca pedido por ID.

`GET /api/pedidos?cpf=12345678901`
Lista pedidos de um cliente pelo CPF.

`PUT /api/pedidos/{id}?status=PAGO`
Atualiza o status do pedido para `PAGO` ou `CANCELADO`.

## Regras de negócio

- Cliente: CPF e e-mail devem ser únicos.
- Cliente: endereço é obrigatório no cadastro e é persistido em tabela separada.
- Cliente: busca pode ser feita por CPF ou nome.
- Produto: nome deve ser único.
- Produto: busca pode ser feita por nome, status ou combinando ambos.
- Produto: estoque pode ser incrementado por endpoint próprio.
- Pedido: deve ter ao menos 1 produto.
- Estoque: não permite vender acima da quantidade disponível.
- Status: pedido nasce como `AGUARDANDO_PAGAMENTO` e só pode ir para `PAGO` ou `CANCELADO`; pedidos `PAGO` e `CANCELADO` não podem ser alterados.
- Cliente `INATIVO` não pode criar pedido.

## Banco de dados

Tabelas principais:

- `clientes` (id UUID, nome, email, cpf, status, endereco_id)
- `enderecos` (id UUID, logradouro, numero, complemento, bairro, cidade, estado, cep)
- `produtos` (id UUID, nome, preco, quantidade_em_estoque, status)
- `pedidos` (id UUID, cliente_id, status, criado_em)
- `itens_pedido` (id BIGINT, pedido_id, produto_id, quantidade, preco_no_momento_da_compra)

Relacionamentos:

- `clientes` 1:1 `enderecos`
- `clientes` 1:N `pedidos`
- `pedidos` 1:N `itens_pedido`
- `produtos` 1:N `itens_pedido`

Observações:

- `cpf` e `email` são únicos no banco.
- `nome` do produto é validado como único na camada de serviço.
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
  "mensagem": "Requisição inválida.",
  "campos": [
    { "campo": "email", "mensagem": "email inválido" }
  ]
}
```

## Docker

### Comandos

Criar as imagens:

```bash
docker compose build
```

Subir a aplicação e um MySQL local:

```bash
docker compose up -d
```

Parar os containers:

```bash
docker compose stop
```

Derrubar e remover containers, rede e volumes do projeto:

```bash
docker compose down -v
```

Rebuild sem cache:

```bash
docker compose build --no-cache app
docker compose up -d
```

Endpoints Swagger:

- `http://localhost:8080/swagger-ui.html`
