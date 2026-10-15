# 🍔 Lanchonete Automatizada

![Java](https://img.shields.io/badge/Java-25-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.1.1-green)
![MySQL](https://img.shields.io/badge/MySQL-Database-blue)
![Maven](https://img.shields.io/badge/Maven-Build-red)
![Status](https://img.shields.io/badge/Status-Em_Desenvolvimento-yellow)

## 📖 Sobre o projeto

A **Lanchonete Automatizada** é um projeto de backend desenvolvido
com Java e Spring Boot, cujo objetivo é simular o funcionamento de
uma lanchonete moderna, com foco na automação do atendimento,
organização de pedidos e gerenciamento de mesas.

A proposta do sistema é permitir que clientes utilizem um QR Code
para acessar uma mesa, iniciem uma sessão de atendimento e
organizem seus consumos individualmente por meio de subcomandas.

O sistema já possui funcionalidades de gerenciamento de mesas,
produtos, sessões de atendimento e subcomandas, além de um fluxo
de pagamentos individuais com confirmação administrativa simulada.

O fluxo de pagamentos permite consultar valores, iniciar o
processamento e confirmar o pagamento de uma subcomanda,
respeitando as regras de transição entre os estados
PENDENTE, PROCESSANDO e PAGO.

Entre as evoluções planejadas estão o acompanhamento de pedidos
em tempo real, a integração com sistemas automatizados de preparo
e a integração com serviços reais de pagamento.

Este repositório faz parte do meu portfólio profissional e tem
como objetivo demonstrar conhecimentos em desenvolvimento backend,
modelagem de dados, APIs REST, testes automatizados e boas
práticas de Engenharia de Software.

> **Status:** projeto em desenvolvimento. As funcionalidades
> planejadas não devem ser consideradas implementadas.

## 🎯 Objetivos

- Desenvolver uma API REST utilizando Java e Spring Boot.
- Gerenciar mesas e produtos de uma lanchonete.
- Modelar sessões de atendimento e subcomandas individuais.
- Organizar o consumo de diferentes clientes em uma mesma mesa.
- Aplicar separação de responsabilidades entre as camadas do sistema.
- Utilizar persistência de dados com Spring Data JPA e MySQL.
- Desenvolver testes automatizados.
- Evoluir o sistema de forma incremental utilizando Git e GitHub.

## 🛠️ Tecnologias utilizadas

| Tecnologia | Finalidade |
|------------|------------|
| Java 25 | Linguagem de programação |
| Spring Boot 4.1.1 | Framework principal |
| Spring Web MVC | Desenvolvimento da API REST |
| Spring Data JPA | Persistência de dados |
| Spring Validation | Validação de dados |
| MySQL | Banco de dados relacional |
| H2 Database | Banco de dados em memória para testes automatizados |
| Maven | Gerenciamento de dependências e build |
| JUnit | Testes automatizados |
| Mockito | Simulação de dependências nos testes |
| Git e GitHub | Versionamento e organização do desenvolvimento |
| Docker | Containerização da aplicação Java |
| Docker Compose | Orquestração da API e do MySQL |
| GitHub Actions | Execução automática dos testes na integração contínua (CI) |

## 🏗️ Arquitetura do projeto

O backend é organizado em camadas para separar as regras
de negócio, o acesso aos dados e a comunicação HTTP.

As principais responsabilidades são:

- **Domain:** entidades, enums e conceitos do negócio.
- **Application:** serviços e regras de negócio.
- **Interfaces:** controllers, DTOs e tratamento de requisições.
- **Infrastructure:** configurações e componentes de infraestrutura.

Essa organização busca facilitar a manutenção, os testes
e a evolução das funcionalidades.

## 🗃️ Modelo de domínio

O projeto utiliza os seguintes conceitos:

### Mesa

Representa uma mesa da lanchonete.

Contém informações como número, capacidade, status
e token utilizado na proposta de identificação por QR Code.

### Produto

Representa um item disponível no cardápio.

Contém informações como nome, descrição, categoria,
preço, tempo estimado de preparo e disponibilidade.

### SessaoMesa

Representa o período de atendimento de uma mesa.

Permite associar uma sessão a uma mesa e organizar
as subcomandas dos clientes participantes.

### Subcomanda

Representa o consumo individual de um cliente
durante uma sessão de mesa.

O modelo prevê identificação individual,
controle de pagamento e valor total.

> A existência das entidades no domínio não significa
> que todos os fluxos de negócio estejam disponíveis
> por meio da API REST.

## ✅ Funcionalidades implementadas

A versão principal do projeto disponibiliza funcionalidades
de gerenciamento de mesas, produtos e comandas, além de
um fluxo de pagamentos individuais por subcomanda.

As operações são disponibilizadas por meio de uma API REST,
com validações, regras de negócio e tratamento de erros.

### Mesas

- Consulta de mesas por número.
- Atualização do estado de ocupação de mesas.
- Liberação de mesas.
- Tratamento de situações em que uma mesa não é encontrada.

### Produtos

- Consulta de produtos disponíveis.
- Consulta de produtos por identificador.
- Cadastro de produtos.
- Desativação de produtos.
- Tratamento de situações em que um produto não é encontrado.

### Comandas e subcomandas

- Gerenciamento de sessões de atendimento.
- Organização do consumo individual por subcomandas.
- Lançamento de itens em subcomandas.
- Aplicação de regras de negócio para operações com comandas.
- Integração do módulo de comandas à branch principal (`main`).

O módulo permite organizar o atendimento de uma mesa
e controlar o consumo individual dos clientes.

### Pagamentos de subcomandas

- Consulta dos dados de pagamento de uma subcomanda.
- Início do processamento de pagamento.
- Confirmação administrativa simulada do pagamento.
- Controle dos estados PENDENTE, PROCESSANDO e PAGO.
- Validação das transições entre os estados de pagamento.
- Tratamento de operações inválidas.
- Controle de concorrência para evitar alterações
  indevidas durante o início do pagamento.

> A confirmação de pagamento é simulada para fins
> de demonstração das regras de negócio.
> O sistema ainda não possui integração com
> gateways de pagamento reais.

### Estrutura de domínio

- Modelagem de sessões de mesa.
- Modelagem de subcomandas individuais.
- Enumerações para estados de mesas, sessões e pagamentos.
- Repositórios para persistência das entidades.

## 🗺️ Roadmap

As próximas etapas planejadas incluem:

- [x] Estrutura inicial do projeto.
- [x] Modelagem de mesas e produtos.
- [x] Serviços de mesas e produtos.
- [x] Controllers de mesas e produtos.
- [x] Modelagem inicial de sessões e subcomandas.
- [x] Integração da API de comandas à `main`.
- [x] Implementação do fluxo de pagamentos de subcomandas.
- [x] Testes automatizados com H2 em memória.
- [x] Teste de concorrência com MySQL e bloqueio pessimista.
- [ ] Evolução das migrações SQL.
- [ ] Atualizações em tempo real com WebSocket.
- [ ] Integração com gateway de pagamento real.
- [ ] Mecanismos adicionais de validação e antifraude.
- [ ] Documentação interativa com OpenAPI/Swagger.
- [x] Containerização com Docker.
- [x] Integração contínua (CI) com GitHub Actions.
- [ ] Entrega contínua (CD) automatizada.

O roadmap representa o planejamento atual e
pode ser ajustado conforme a evolução do projeto.

## 🔄 Fluxo de atendimento planejado

O fluxo completo proposto para o sistema é:

1. O cliente chega à lanchonete e escolhe uma mesa.
2. Acessa o sistema por meio do QR Code da mesa.
3. Uma sessão de atendimento é iniciada.
4. Outros clientes podem participar da mesma sessão.
5. Cada cliente utiliza sua própria subcomanda.
6. Os clientes selecionam produtos do cardápio.
7. Os pedidos são encaminhados para preparo.
8. O andamento dos pedidos poderá ser acompanhado.
9. Cada cliente realiza o pagamento do próprio consumo.
10. Após o encerramento da sessão, a mesa é liberada.

**Importante:** esse fluxo descreve a visão completa
do produto e ainda não está integralmente implementado.

## 🐳 Executando com Docker Compose

O projeto utiliza Docker Compose para executar a API Spring Boot
e o banco de dados MySQL em contêineres.

O Dockerfile utiliza Multi-stage Build, permitindo compilar
a aplicação automaticamente durante a construção da imagem.

### Pré-requisitos

- Docker Engine.
- Docker Compose.

### 1. Configurar a senha do MySQL

No terminal, execute:

```bash
read -rsp "Digite a senha do MySQL Docker: " MYSQL_ROOT_PASSWORD; echo; export MYSQL_ROOT_PASSWORD
```

### 2. Construir e iniciar os contêineres

```bash
docker compose up -d --build
```

### 3. Verificar os contêineres

```bash
docker compose ps
```

A API estará disponível em:

http://localhost:8080

### 4. Encerrar os contêineres

```bash
docker compose down
```

O volume do MySQL será preservado.

## 🚀 Como executar o projeto localmente

### Pré-requisitos

- Java JDK 25.
- MySQL Server.
- Maven ou Maven Wrapper disponível no projeto.
- Git.

### 1. Clonar o repositório

```bash
git clone https://github.com/AlexSaldan/lanchonete-automatizada.git
```
### 2. Acessar a pasta

```bash
cd lanchonete-automatizada
```
### 3. Configurar o banco de dados

Crie um banco de dados MySQL para a aplicação:

```sql
CREATE DATABASE lanchonete_db;
```

Configure a conexão no arquivo:

```text
src/main/resources/application.yaml
```

Utilize as credenciais do seu ambiente local.

**Atenção:** não publique senhas reais ou outras
credenciais no repositório.

### 4. Executar a aplicação

Com o Maven Wrapper:

```bash
./mvnw spring-boot:run
```

A configuração de conexão com o MySQL deve estar
correta para que a aplicação consiga iniciar.

## 🧪 Testes automatizados

O projeto possui testes automatizados para validar
as regras de negócio, os endpoints HTTP e o comportamento
da persistência de dados.

São utilizados JUnit, Mockito, Spring Boot Test e H2.

### Testes padrão com H2

Os testes padrão utilizam o banco H2 em memória,
permitindo sua execução sem um servidor MySQL local.

Na última execução validada, 47 testes passaram
com sucesso.

Para executar:

```bash
./mvnw clean test
```

### Teste de concorrência com MySQL

O projeto também possui o teste de integração
`SubcomandaRepositoryIT`, que utiliza MySQL 8.

Esse teste valida o uso de bloqueio pessimista
para controlar operações concorrentes em subcomandas.

Requisitos:

- MySQL disponível em `localhost:3306`.
- Banco de testes `lanchonete_automatizada_test`.
- Usuário com permissões no banco de testes.
- Variáveis `DB_TEST_USER` e `DB_TEST_PASSWORD` configuradas.

Para executar somente esse teste:

```bash
./mvnw -Dtest=SubcomandaRepositoryIT test
```

> **Atenção:** o perfil `mysql-test` utiliza
> `ddl-auto: create-drop`. Execute esse teste
> exclusivamente em um banco destinado a testes.

## 🌿 Versionamento

O desenvolvimento é organizado por branches,
com alterações incrementais e commits descritivos.

O histórico utiliza convenções como:

- `feat:` novas funcionalidades.
- `fix:` correções.
- `docs:` documentação.
- `refactor:` refatorações.
- `test:` testes.
- `chore:` manutenção e configurações.

Essa abordagem busca manter as alterações
rastreáveis e facilitar a revisão do código.

### Pull Requests relevantes

O projeto evolui por meio de branches específicas
e Pull Requests, permitindo revisar as alterações
antes de integrá-las à branch principal (`main`).

Algumas entregas realizadas:

- [PR #4 — API de comandas](https://github.com/AlexSaldan/lanchonete-automatizada/pull/4)
- [PR #5 — Evolução do módulo de produtos](https://github.com/AlexSaldan/lanchonete-automatizada/pull/5)
- [PR #6 — Pagamentos de subcomandas](https://github.com/AlexSaldan/lanchonete-automatizada/pull/6)
- [PR #10 — Integração contínua com GitHub Actions](https://github.com/AlexSaldan/lanchonete-automatizada/pull/10)
- [PR #11 — Docker e Docker Compose](https://github.com/AlexSaldan/lanchonete-automatizada/pull/11)

Os Pull Requests registram a evolução das funcionalidades,
as alterações realizadas e o processo de revisão do código.

## 👨‍💻 Autor

**Alexandro Saldan Pereira**

Projeto desenvolvido para estudo, prática de
Engenharia de Software e composição de portfólio
profissional em desenvolvimento backend Java.

GitHub: [AlexSaldan](https://github.com/AlexSaldan)
