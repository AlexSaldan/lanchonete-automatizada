# 🍔 Lanchonete Automatizada

![Java](https://img.shields.io/badge/Java-25-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.1.1-green)
![MySQL](https://img.shields.io/badge/MySQL-Database-blue)
![Maven](https://img.shields.io/badge/Maven-Build-red)
![Status](https://img.shields.io/badge/Status-Em_Desenvolvimento-yellow)
[![CI - Lanchonete Automatizada](https://github.com/AlexSaldan/lanchonete-automatizada/actions/workflows/ci.yml/badge.svg)](https://github.com/AlexSaldan/lanchonete-automatizada/actions/workflows/ci.yml)

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

O projeto já conta com um módulo de cozinha automatizada,
responsável pelo acompanhamento dos itens dos pedidos,
controle dos estados de preparo e entrega,
além de mecanismos de controle de concorrência para garantir a consistência das operações.

Entre as próximas evoluções planejadas estão o acompanhamento de pedidos em tempo real,
a integração com equipamentos físicos de preparo e a integração com serviços reais de pagamento.

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
| Hibernate | Mapeamento objeto-relacional (ORM) e gerenciamento de entidades |
| Spring Transaction Management | Gerenciamento de transações com `@Transactional` |
| Spring Validation | Validação de dados |
| MySQL | Banco de dados relacional |
| MySQL InnoDB | Controle de concorrência com bloqueio pessimista (`PESSIMISTIC_WRITE`) |
| H2 Database | Banco de dados em memória para testes automatizados |
| Maven | Gerenciamento de dependências e build |
| JUnit | Testes automatizados |
| Mockito | Simulação de dependências nos testes |
| Git e GitHub | Versionamento e organização do desenvolvimento |
| Docker | Containerização da aplicação Java |
| Docker Compose | Orquestração da API e do MySQL |
| GitHub Actions | Execução automática dos testes na integração contínua (CI) |
| Swagger / OpenAPI | Documentação interativa dos endpoints da API REST |
| Spring Tools for Eclipse (STS) 5.4.0.RELEASE | IDE utilizada no desenvolvimento, depuração e execução dos testes |

### Tecnologias planejadas

As próximas etapas do projeto poderão incorporar novas tecnologias para ampliar a segurança,
a experiência dos usuários e a automação dos processos.

| Tecnologia | Finalidade planejada |
|------------|----------------------|
| Spring WebSocket | Atualização de pedidos em tempo real |
| Spring Security | Autenticação e autorização |
| Tokens de acesso (formato a definir) | Credenciais temporárias para clientes e sessões |
| Flyway | Versionamento e evolução do esquema do banco de dados |
| React | Desenvolvimento da interface web |
| TypeScript | Desenvolvimento de interfaces com tipagem estática |
| HTML5 e CSS3 | Estrutura e estilização das interfaces |
| Provedor de serviços de pagamento (PSP) | Cobranças Pix dinâmicas e confirmação por webhook |
| GitHub Actions (CD) | Automatização do processo de entrega |

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

### Cozinha automatizada

O módulo de cozinha permite acompanhar e controlar o preparo
dos itens lançados nas subcomandas.

Funcionalidades implementadas:

- Consulta da fila de itens da cozinha.
- Início do preparo de um item.
- Atualização do item para o estado pronto.
- Registro da entrega do item.
- Validação das transições entre os estados de preparo.
- Controle de concorrência com bloqueio pessimista no MySQL.
- Testes automatizados das regras de negócio da cozinha.

O módulo utiliza transações com `@Transactional` e bloqueio
pessimista com `PESSIMISTIC_WRITE` para proteger as alterações
de estado contra operações concorrentes.

A implementação foi integrada à branch principal (`main`)
por meio do Pull Request #21.

## 🗺️ Roadmap

O roadmap apresenta as funcionalidades já implementadas e as próximas evoluções planejadas:

- [x] Estrutura inicial do projeto.
- [x] Modelagem de mesas e produtos.
- [x] Serviços de mesas e produtos.
- [x] Controllers de mesas e produtos.
- [x] Modelagem inicial de sessões e subcomandas.
- [x] Integração da API de comandas à `main`.
- [x] Implementação do fluxo de pagamentos de subcomandas.
- [x] Implementação da fila de itens da cozinha.
- [x] Controle dos estados de preparo e entrega dos itens.
- [x] Controle de concorrência na cozinha com bloqueio pessimista.
- [x] Testes automatizados do módulo de cozinha.
- [x] Testes automatizados com H2 em memória.
- [x] Teste de concorrência com MySQL e bloqueio pessimista.
- [ ] Evolução das migrações SQL.
- [ ] Atualizações em tempo real com WebSocket.
- [ ] Integração com gateway de pagamento real.
- [ ] Autenticação de funcionários e autorização por perfil com Spring Security.
- [ ] Autorização individual por subcomanda e testes contra BOLA/IDOR.
- [ ] Liberação presencial de mesas por funcionário e ativação temporária de clientes.
- [ ] Controle quantitativo de estoque e disponibilidade dos produtos.
- [ ] Pagamento misto (dinheiro + Pix) com saldo restante por subcomanda.
- [ ] Confirmação segura e idempotente de Pix por webhook do PSP.
- [ ] Painel de funcionários com notificações de pagamentos confirmados.
- [ ] Registro opcional de feedback dos clientes.
- [ ] Mecanismos adicionais de validação e antifraude.
- [x] Documentação interativa com OpenAPI/Swagger.
- [x] Containerização com Docker.
- [x] Integração contínua (CI) com GitHub Actions.
- [ ] Entrega contínua (CD) automatizada.

O roadmap representa o planejamento atual e
pode ser ajustado conforme a evolução do projeto.

## 🔄 Fluxo de atendimento planejado

O fluxo abaixo representa a visão de produto e **ainda não está integralmente implementado**:

1. O funcionário autenticado recebe o cliente e libera a mesa para atendimento.
2. O cliente escaneia o **QR Code fixo** da mesa, que a identifica, mas não concede autorização para pedidos.
3. Uma ativação temporária, fornecida por canal presencial controlado, permite ingressar na sessão autorizada.
4. Cada cliente recebe uma credencial temporária vinculada à sua **própria subcomanda**.
5. O cliente consulta o cardápio e faz pedidos; o backend verifica permissões, disponibilidade e regras de negócio.
6. A cozinha acompanha o preparo; o funcionário controla a disponibilidade e, futuramente, as quantidades em estoque.
7. O cliente consulta sua conta e pode pagar em dinheiro, Pix ou combinar os dois.
8. O funcionário autenticado registra o valor efetivamente recebido em dinheiro; o backend calcula o saldo restante.
9. Para o saldo escolhido em Pix, o backend cria uma cobrança dinâmica no PSP; o cliente recebe QR Code Pix ou código copia e cola.
10. O backend confirma o Pix por integração confiável com o PSP, validando cobrança, valor e status, e processando notificações repetidas com segurança.
11. O funcionário recebe uma atualização em tempo real sobre o pagamento confirmado, sem depender de comprovantes apresentados pelo cliente.
12. A mesa só fica apta a ser liberada quando **todas as subcomandas estiverem quitadas**; o funcionário confere o atendimento, pode solicitar feedback e libera a mesa.

### 🔐 Decisões de segurança previstas

- O QR Code impresso permanece fixo: fotografá-lo não deve conceder acesso a subcomandas ou permitir abertura não autorizada de sessões.
- O funcionário autoriza a utilização da mesa; a ativação temporária do cliente precisa de uma comprovação adicional, independente da URL pública.
- Cada requisição sensível deve validar **quem solicita** e **qual subcomanda pode acessar**, evitando BOLA/IDOR.
- CPF, nome e identificadores numéricos não são credenciais de autenticação.
- O acesso dos funcionários terá perfis e permissões; alterações de estoque, recebimentos em dinheiro e liberações de mesa deverão ser auditáveis.
- Somente o backend, após verificação com o PSP, poderá confirmar um pagamento Pix real.
- O fluxo financeiro deverá tratar concorrência, idempotência, estornos, troco e divergências antes de operar com dinheiro real.
- Informações pessoais não deverão aparecer em monitores públicos.

### 💰 Pagamentos parciais e mistos — planejado

Uma subcomanda poderá ter **vários recebimentos**, cada um com forma, valor, estado e origem de confirmação. Exemplo:

| Movimento | Valor | Confirmação |
|-----------|------:|-------------|
| Total consumido | R$ 46,00 | Calculado pelo backend |
| Dinheiro recebido | R$ 20,00 | Registrado por funcionário autorizado |
| Pix restante | R$ 26,00 | Confirmado pelo PSP |
| Saldo após ambos confirmados | R$ 0,00 | Subcomanda quitada |

**Saldo = total consumido − soma dos recebimentos confirmados.** Uma cobrança Pix criada, mas ainda não confirmada, não quita a conta. O modelo atual de pagamento por estados é uma etapa anterior a esse controle financeiro detalhado.

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
CREATE DATABASE lanchonete_automatizada;
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

Na última execução validada pelo GitHub Actions,
77 testes automatizados passaram com sucesso,
sem falhas ou erros.

Em execução local pelo Spring Tools Suite (STS),
80 testes automatizados passaram com sucesso,
incluindo os testes de integração com MySQL,
sem falhas ou erros.

Para executar:

```bash
./mvnw clean test
```

### Teste de concorrência com MySQL

O projeto também possui o teste de integração
`SubcomandaRepositoryIT`, que utiliza MySQL 8.

Esse teste valida o uso de bloqueio pessimista
para controlar operações concorrentes em subcomandas.

O projeto também possui o teste de integração
`ItemPedidoRepositoryIT`, executado com MySQL.

Esse teste verifica o bloqueio pessimista
(`PESSIMISTIC_WRITE`) durante operações concorrentes
sobre itens de pedidos, utilizando duas transações
simultâneas para validar o comportamento do banco de dados.

Requisitos:

- MySQL disponível em `localhost:3306`.
- Banco de testes `lanchonete_automatizada_test`.
- Usuário com permissões no banco de testes.
- Variáveis `DB_TEST_USER` e `DB_TEST_PASSWORD` configuradas.

Para executar somente esse teste:

```bash
./mvnw -Dtest=SubcomandaRepositoryIT test
```

Para executar somente o teste de concorrência da cozinha:

```bash
./mvnw -Dtest=ItemPedidoRepositoryIT test
```

> **Atenção:** o perfil `mysql-test` utiliza
> `ddl-auto: create-drop`. Execute esse teste
> exclusivamente em um banco destinado a testes.

## 🚧 Desafios Enfrentados & Aprendizados

Esta seção distingue os problemas já tratados no código das melhorias identificadas durante a evolução do produto.

### 🍔 Contas individuais em mesas compartilhadas — implementado

**Desafio:** permitir que vários clientes consumam na mesma mesa sem misturar seus pedidos e valores.

**Solução:** modelagem de `SessaoMesa` e `Subcomanda`, associando cada consumo à subcomanda individual.

**Aprendizado:** modelagem de domínio, relacionamentos JPA, separação de responsabilidades e regras de negócio.

### 💳 Integridade do estado de pagamento — implementado no fluxo simulado

**Desafio:** impedir novos lançamentos quando a subcomanda já não está pendente e preservar a consistência do processamento.

**Solução:** validação do estado da sessão e do pagamento no serviço, com transações e controle de concorrência. A confirmação atual é **administrativa e simulada**, não uma confirmação bancária real.

**Aprendizado:** máquinas de estados, transações e integridade financeira.

### 🔄 Concorrência nos pedidos e na cozinha — implementado

**Desafio:** evitar inconsistências quando operações simultâneas modificam itens ou subcomandas.

**Solução:** uso de `@Transactional`, consultas com bloqueio pessimista e testes de integração com MySQL.

**Aprendizado:** concorrência, isolamento transacional e verificação do comportamento no banco real.

### 🔐 Autorização por subcomanda — risco identificado; solução planejada

**Desafio:** um cliente pode tentar informar o identificador da subcomanda de outro cliente para lançar pedidos ou consultar dados.

**Solução planejada:** autenticar ou credenciar o solicitante, autorizar cada operação no recurso correto e criar testes de regressão para acessos indevidos (BOLA/IDOR).

**Aprendizado:** identificação não equivale a autorização; `@Valid` e identificadores numéricos não substituem controles de acesso.

### 📷 Reutilização de fotografias de QR Codes — solução planejada

**Desafio:** alguém pode fotografar o QR Code fixo de uma mesa e tentar acessá-la remotamente.

**Solução planejada:** manter o adesivo fixo apenas como identificador, exigir liberação presencial pelo funcionário e ativação temporária independente do QR Code; revogar credenciais ao encerrar o atendimento.

**Aprendizado:** separar descoberta de recursos, prova de presença e autorização de operações.

### 💰 Pagamento misto e confirmação confiável — solução planejada

**Desafio:** registrar parte da conta em dinheiro e o restante em Pix, sem aceitar prints ou notificações falsas como prova de quitação.

**Solução planejada:** registrar recebimentos parciais auditáveis, calcular saldo no backend, gerar Pix pelo PSP e validar confirmações de forma idempotente.

**Aprendizado esperado:** conciliação financeira, webhooks, idempotência e tratamento de falhas externas.

### 🍖 Estoque e experiência do cliente — solução planejada

**Desafio:** impedir pedidos de produtos esgotados e dar ao funcionário controle sobre o atendimento e a satisfação do freguês.

**Solução planejada:** evoluir a disponibilidade atual dos produtos para controle quantitativo de estoque, painel operacional e feedback opcional ao encerrar a mesa.

**Aprendizado esperado:** autorização por perfil, consistência de estoque e desenho de fluxos orientados ao negócio.

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
- [PR #21 — Cozinha automatizada e controle de concorrência](https://github.com/AlexSaldan/lanchonete-automatizada/pull/21)

Os Pull Requests registram a evolução das funcionalidades,
as alterações realizadas e o processo de revisão do código.

## 👨‍💻 Autor

**Alexandro Saldan Pereira**

Projeto desenvolvido para estudo, prática de
Engenharia de Software e composição de portfólio
profissional em desenvolvimento backend Java.

GitHub: [AlexSaldan](https://github.com/AlexSaldan)

