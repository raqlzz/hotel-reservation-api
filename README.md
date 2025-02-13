# Hotel Reservation API 🚪🏨

Esta API é uma aplicação para gerenciamento de reservas de hotel, implementada em **Scala** com **Play Framework**, **Slick** e **H2 Database**. O banco H2 é utilizado exclusivamente para o período de teste, por sua praticidade e facilidade de configuração, dispensando a necessidade de um banco externo durante o desenvolvimento.

A API permite gerenciar quartos (*rooms*) e reservas (*reservations*), garantindo que não haja sobreposição de reservas e considerando uma janela de 4 horas para limpeza após cada reserva.

---

## Funcionalidades ✨

- **CRUD de Rooms:**  
  Adição, listagem e remoção de quartos.
- **CRUD de Reservations:**  
  Criação e listagem de reservas, com validação de disponibilidade.
- **Endpoint de Ocupação:**  
  Consulta de reservas que se sobrepõem a um determinado dia.
- **Validações:**  
  Verificação de existência do quarto e disponibilidade (incluindo janela de limpeza).
- **Criação Automática de Schema:**  
  O esquema do banco (H2, em memória) é criado automaticamente na inicialização, conforme configuração.

---

## Dependências e Versões Utilizadas 🔧

- **Scala:** 2.13.16
- **Play Framework:** 2.9.6
- **Slick:** 3.3.3
- **Play Slick:** 5.0.0
- **H2 Database:** 2.1.214
- **Guice:** (fornecido pelo Play Framework)
- **sbt-giter8-scaffold:** 0.17.0 (para scaffolding do projeto)

---

# Como executar a aplicação

Para executar a aplicação, siga os passos abaixo:

### 1. **Instale o SBT**
Caso ainda não tenha o SBT instalado, siga as instruções no site oficial: [sbt.org](https://www.scala-sbt.org/).

### 2. **Navegue até o Diretório do Projeto**
Abra um terminal e vá até a pasta raiz do projeto.

### 3. Compile o Projeto
```bash
sbt compile
```
### 3. Execute a Aplicação
```bash
sbt run
```
### O servidor iniciará na porta 9000
```bash
http://localhost:9000
```

## Estrutura do Projeto 📂

```bash
hotel-reservation-api/
├── app/
│   ├── controllers/         # Contém RoomController e ReservationController
│   ├── models/              # Contém as classes Room e Reservation
│   ├── modules/             # Contém DatabaseModule e DatabaseInitializer (para criação automática do schema)
│   ├── repositories/        # Contém RoomRepository e ReservationRepository
├── conf/
│   ├── application.conf     # Arquivo de configuração da aplicação
│   └── routes               # Definição das rotas da API
├── project/
│   └── plugins.sbt          # Configuração dos plugins SBT (inclui o plugin do Play)
└── build.sbt                # Configuração do build e dependências
````

# Exemplo de Fluxo Completo

Este exemplo demonstra o fluxo completo utilizando os endpoints da API para gerenciar quartos e reservas.

## Passo 1: Criar um Room

- **Endpoint:** `POST /rooms`
- **Payload de Envio:**

```json
{
  "number": 101,
  "roomType": "SINGLE"
}
```

## Passo 2: Listar um Rooms

Listar Rooms

- **Endpoint:** `GET /rooms`
- **Resposta Esperada:**

```json
[
  {
    "id": 1,
    "number": 101,
    "roomType": "SINGLE"
  },
  {
    "id": 2,
    "number": 102,
    "roomType": "DOUBLE"
  }
]
```

## 3. Deletar um Room

**Endpoint:** `DELETE /rooms/:id`

**Exemplo de URL:** `DELETE /rooms/1`

**Respostas Possíveis:**

- **Room Removida com Sucesso:**

```json
{
  "message": "Room 1 removida"
}
```

## 4. Listar Reservations

**Endpoint:** `GET /reservations`

**Resposta Esperada:**

```json
[
  {
    "id": 1,
    "roomId": 1,
    "guest": "John Doe",
    "checkin": "2025-02-15T14:00:00",
    "checkout": "2025-02-16T10:00:00"
  }
]
```

## 5. Criar uma Reservation

**Endpoint:** `POST /reservations`

**Payload de Envio:**

```json
{
  "roomId": 1,
  "guest": "John Doe",
  "checkin": "2025-02-15T14:00:00",
  "checkout": "2025-02-16T10:00:00"
}
```
**Respostas Possíveis:**
```json
{
  "id": 1,
  "roomId": 1,
  "guest": "John Doe",
  "checkin": "2025-02-15T14:00:00",
  "checkout": "2025-02-16T10:00:00"
}
```

###Dados Inválidos (400 Bad Request)
```json
{
  "message": "Invalid data for Reservation"
}
```

### Quarto Não Encontrado (404 Not Found)
```json
{
  "message": "Room 1 not found"
}
```
### Quarto Não Disponível (400 Bad Request)
```json
{
  "message": "Room not available for the requested period"
}
```

