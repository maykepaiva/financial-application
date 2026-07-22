# Account System

API REST para gerenciamento de contas financeiras — depósitos, saques e transferências com validação de saldo e consistência de estado. Armazenamento em memória, sem banco de dados.

## Requisitos

- Java 17

Não é necessário instalar Maven — o projeto já inclui o wrapper (`mvnw` / `mvnw.cmd`).

## Como executar

Na raiz do projeto:

**Windows**
```
mvnw.cmd spring-boot:run
```

**Linux/Mac**
```
./mvnw spring-boot:run
```

A aplicação sobe em `http://localhost:8080`.

## Como rodar os testes

**Windows**
```
mvnw.cmd test
```

**Linux/Mac**
```
./mvnw test
```

## Endpoints

### POST /event

Body em JSON, variando pelo campo `type`.

**Depósito** (usa `destination`; cria a conta se não existir)
```json
{"type":"deposit","destination":"100","amount":10}
```

**Saque** (usa `origin`; falha se a conta não existir ou o saldo for insuficiente)
```json
{"type":"withdraw","origin":"100","amount":5}
```

**Transferência** (usa `origin` e `destination`; cria a conta destino se não existir)
```json
{"type":"transfer","origin":"100","destination":"300","amount":15}
```

Resposta: `201 Created` com o(s) saldo(s) atualizado(s).

### GET /balance?account_id={id}

Retorna o saldo da conta (`200`), ou `404` com corpo `0` se a conta não existir.

### POST /reset

Limpa todas as contas em memória. Retorna `200 OK`.

## Erros

Conta inexistente retorna `404` com corpo `0`. Demais erros de negócio (saldo insuficiente, valor inválido, tipo de evento inválido) retornam `400` com corpo `{"message": "..."}`.

## Decisões técnicas

- **Armazenamento em memória** (`ConcurrentHashMap`), sem persistência em banco — fora do escopo do desafio.
- **Consistência sob concorrência**: `deposit`, `withdraw` e `transfer` são `synchronized` no service, evitando leitura-modificação-escrita intercalada entre operações concorrentes na mesma conta.
- **Tratamento de erros centralizado** via `@RestControllerAdvice`, com exceções de negócio dedicadas (`AccountNotFoundException`, `InsufficientBalanceException`, `InvalidAmountException`, `InvalidEventTypeException`).
