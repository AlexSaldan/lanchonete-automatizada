# 🍔 Lanchonete Automatizada

![Java](https://shields.io)
![Spring](https://shields.io)
![MySQL](https://shields.io)
![Status](https://shields.io)

Este é o backend de um sistema para gerenciamento e operação de uma lanchonete automatizada. O projeto simula a inteligência de uma lanchonete moderna, controlando desde a entrada do pedido no autoatendimento até o comando de preparo para as máquinas ou robôs.

O objetivo principal deste repositório é demonstrar a aplicação prática de arquitetura de software, boas práticas de desenvolvimento e manipulação de banco de dados relacional voltado para o mercado real.

## 🛠️ Tecnologias e Ferramentas

*   **Linguagem:** Java
*   **Framework:** Spring Boot (Spring Web, Spring Data JPA, Spring Validation)
*   **Banco de Dados:** MySQL (Persistência de dados relacionais)
*   **Gerenciador de Dependências:** Maven

## 🎯 Entidades do Sistema (Escopo Inicial)

O banco de dados MySQL gerencia o relacionamento das seguintes entidades:
*   `Cliente`: Cadastro opcional para identificação no totem de autoatendimento.
*   `Produto`: Itens do cardápio (bebidas, lanches, acompanhamentos) e seus valores.
*   `Pedido`: Registro das compras, vinculando cliente, itens e o valor total.
*   `StatusPedido`: Controle de fluxo do preparo (`RECEBIDO`, `EM_PREPARO`, `PRONTO`, `ENTREGUE`).

## 🚀 Como Executar o Projeto Localmente

### Pré-requisitos
*   Java JDK instalado
*   MySQL Server ativo na máquina
*   Uma IDE de sua preferência (IntelliJ IDEA, Eclipse, VS Code)

### Passos para execução
1. Clone o repositório:
   ```bash
   git clone https://github.com
   ```

2. Configure a conexão com o banco de dados no arquivo `src/main/resources/application.properties` (ou `application.yml`):
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/lanchonete_db
   spring.datasource.username=seu_usuario
   spring.datasource.password=sua_senha
   spring.jpa.hibernate.ddl-auto=update
   ```

3. Crie o schema no seu MySQL:
   ```sql
   CREATE DATABASE lanchonete_db;
   ```

4. Execute a aplicação através da sua IDE ou utilizando o Maven pelo terminal:
   ```bash
   ./mvnw spring-boot:run
   ```

## 🧑‍💻 Autor

Desenvolvido por **Alexandro Saldan Pereira** 
*   **LinkedIn:** [Insira o link do seu LinkedIn aqui]
*   **E-mail:** [Insira o seu e-mail aqui]
