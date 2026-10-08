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

O projeto também prevê a evolução para funcionalidades como
acompanhamento de pedidos em tempo real, pagamentos individuais
e integração com sistemas automatizados de preparo.

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
| Maven | Gerenciamento de dependências e build |
| JUnit | Testes automatizados |
| Mockito | Simulação de dependências nos testes |
| Git e GitHub | Versionamento e organização do desenvolvimento |

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

Na versão principal do projeto, estão disponíveis
funcionalidades relacionadas aos módulos de mesas
e produtos.

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

### Estrutura de domínio

- Modelagem de sessões de mesa.
- Modelagem de subcomandas individuais.
- Enumerações para estados de mesas, sessões e pagamentos.
- Repositórios para persistência das entidades.

## 🚧 Funcionalidades em desenvolvimento

### API de comandas

A implementação da API de comandas está sendo
desenvolvida em uma branch específica.

Essa etapa contempla a evolução dos fluxos de
sessões de mesa, subcomandas e itens.

A funcionalidade ainda não está integrada
à branch principal (`main`).

## 🗺️ Roadmap

As próximas etapas planejadas incluem:

- [x] Estrutura inicial do projeto.
- [x] Modelagem de mesas e produtos.
- [x] Serviços de mesas e produtos.
- [x] Controllers de mesas e produtos.
- [x] Modelagem inicial de sessões e subcomandas.
- [ ] Integração da API de comandas à `main`.
- [ ] Evolução das migrações SQL.
- [ ] Atualizações em tempo real com WebSocket.
- [ ] Integração de pagamentos.
- [ ] Mecanismos adicionais de validação e antifraude.
- [ ] Documentação interativa com OpenAPI/Swagger.
- [ ] Containerização com Docker.
- [ ] Integração contínua com CI/CD.

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

O projeto utiliza JUnit e Mockito para testar
comportamentos dos serviços.

Entre os módulos com testes unitários estão:

- `MesaService`
- `ProdutoService`

Para executar os testes:

```bash
./mvnw test
```

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

## 👨‍💻 Autor

**Alexandro Saldan Pereira**

Projeto desenvolvido para estudo, prática de
Engenharia de Software e composição de portfólio
profissional em desenvolvimento backend Java.

GitHub: [AlexSaldan](https://github.com/AlexSaldan)
