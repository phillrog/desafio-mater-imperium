# [![CI/CD Desafio Backend Mater Imperium](https://github.com/phillrog/desafio-mater-imperium/actions/workflows/maven.yml/badge.svg)](https://github.com/phillrog/desafio-mater-imperium/actions/workflows/maven.yml) - [<img src="https://img.shields.io/badge/Railway%20Deploy-black?style=for-the-badge&logo=railway" width="220" height="38.45"/></a>](https://desafio-mater-imperium-production.up.railway.app/swagger-ui/index.html)

Desafio Backend - MaterImperium
===============================

Este repositório contém a solução para o desafio técnico de processamento de arquivos em background, desenvolvido com **Java 21** e o ecossistema **Spring Boot 3.5**.

![m](https://github.com/user-attachments/assets/cbb3c28d-7513-4afe-826b-016964b44808)


🚀 O Projeto
------------

A aplicação consiste em uma API robusta capaz de receber arquivos de grande porte (até 1GB), realizar validações estruturais e processar o conteúdo de forma assíncrona utilizando **Spring Batch**. O sistema garante segurança através de **JWT (Bearer Token)** com controle de acesso baseado em perfis (**ENVIO** e **CONSULTA**).

### 🛠️ Tecnologias Utilizadas

-   **Java 21** (LTS)

-   **Spring Boot 3.5**

-   **Spring Batch**: Para processamento eficiente de arquivos em larga escala com baixo consumo de recursos.

-   **Spring Security + JWT**: Autenticação e autorização por roles.

-   **PostgreSQL**: Banco de dados relacional para persistência de resumos e histórico.

-   **Flyway**: Migrações de banco de dados.

-   **Testcontainers**: Testes de integração com banco real em ambiente de CI.

-   **Docker & GitHub Actions**: Pipeline completo de CI/CD com deploy automatizado no **Railway**.

-   **OpenAPI (Swagger)**: Documentação interativa dos endpoints.

* * * * *

🏗️ Arquitetura e Estrutura
---------------------------

O projeto segue os princípios de **Clean Architecture** e **Modular Design**, organizado da seguinte forma:

-   `modules/seguranca`: Gerenciamento de usuários, tokens e proteção de rotas.

-   `modules/processamento`: Lógica do Spring Batch, validação de cabeçalhos e persistência de resumos.

-   `shared`: Abstrações comuns como o padrão `Result<T>` para respostas padronizadas.

Plain Text do projeto
```
└── src
    ├── main
    │   └── java
    │       └── com.materimperium.backend
    │           └── modules
    │               ├── processamento          <-- Módulo central do desafio técnico
    │               │   ├── api.controllers    <-- Endpoints de upload e consulta
    │               │   ├── application        <-- Orquestração e lógica de aplicação
    │               │   │   ├── dtos           <-- Objetos de transferência de dados (Responses)
    │               │   │   ├── interfaces     <-- Abstrações (Contratos) do serviço
    │               │   │   ├── services       <-- Implementação das regras de processamento
    │               │   │   └── validators     <-- Validação de cabeçalho do arquivo (|0000|...)
    │               │   ├── domain             <-- O "Coração" do negócio (Independente de Framework)
    │               │   │   ├── entities       <-- Modelos: Processamento, Resumo e Status
    │               │   │   └── interfaces     <-- Contratos de persistência (Repositories)
    │               │   └── infrastructure     <-- Detalhes técnicos e implementações externas
    │               │       ├── batches        <-- Configuração Spring Batch (Processamento 1Gb)
    │               │       ├── config         <-- Segurança específica do módulo
    │               │       └── repositories   <-- Implementações JPA/Postgres
    │               │
    │               ├── seguranca              <-- Módulo de Autenticação e Autorização (RBAC)
    │               │   ├── api                <-- Controllers e Requests de Login/Registro
    │               │   ├── applications       <-- Lógica de autenticação e revogação de tokens
    │               │   ├── domain             <-- Entidades de Usuário, Role e Token
    │               │   └── infrastructure     <-- Configurações de JWT e Spring Security
    │               │
    │               └── shared                 <-- Componentes globais compartilhados
    │                   └── abstractions       <-- Result Pattern (Sucesso/Falha padronizado)
    │
    └── test                                   <-- Camada de Testes (Cobertura técnica)
        └── java.com.materimperium.backend
            ├── processamento                  <-- Testes unitários e integração de negócio
            └── seguranca                      <-- Testes de persistência de usuários/tokens

```
* * * * *

🔐 Segurança e Perfis
---------------------

A aplicação protege os endpoints conforme os requisitos:

| **Endpoint** | **Verbo** | **Perfil Requerido** | **Descrição** |
| --- | --- | --- | --- |
| `/api/v1/auth/registrar` | POST | Público | Criação de novo usuário. |
| `/api/v1/auth/login` | POST | Público | Autenticação e obtenção do Token. |
| `/api/v1/processamento/upload` | POST | **ENVIO** | Upload e pré-validação do arquivo. |
| `/api/v1/processamento/progresso/{id}` | GET | **ENVIO** ou **CONSULTA** | Status do processamento. |
| `/api/v1/processamento/resultado/{id}` | GET | **CONSULTA** | Resumo estatístico do arquivo. |

* * * * *

⚙️ Como Executar
----------------

### Pré-requisitos

-   JDK 21

-   Docker (opcional, para testes e banco local)

### Execução Local

1.  Clone o repositório:

    Bash

    ```
    git clone https://github.com/phillrog/desafio-mater-imperium.git

    ```

2.  Configure as variáveis de ambiente no seu `application.yml` ou exporte no terminal (DB_URL, DB_USERNAME, DB_PASSWORD).

3.  Execute a aplicação:

    Bash

    ```
    mvn spring-boot:run

    ```

### Acesso ao Swagger

Após iniciar, acesse a documentação interativa em:

`http://localhost:8080/swagger-ui.html`

* * * * *

🤖 CI/CD e Deploy
-----------------

O projeto conta com um pipeline de **Continuous Integration** e **Continuous Deployment** via GitHub Actions:

1.  **Test**: Validação de testes unitários e de integração (Postgres via Testcontainers).

2.  **Build**: Geração do artefato `.jar`.

3.  **Deploy**: Após aprovação manual, o deploy é realizado automaticamente no **Railway**.

* * * * *

Desenvolvido por Phillipe R. - 2026.

* * * * *

# Resultado

<img width="1919" height="971" alt="Captura de tela 2026-02-12 013702" src="https://github.com/user-attachments/assets/44092406-3af9-4f17-a160-dbe6b9df793c" />

<img width="1916" height="967" alt="Captura de tela 2026-02-12 013734" src="https://github.com/user-attachments/assets/56280bcf-dbde-417d-b358-b43ba1c4b74d" />

<img width="1919" height="968" alt="Captura de tela 2026-02-12 013803" src="https://github.com/user-attachments/assets/6d36fdee-e509-4821-933c-35677dc02ab5" />

<img width="1919" height="966" alt="Captura de tela 2026-02-12 013834" src="https://github.com/user-attachments/assets/ab4c5eb4-9659-42ca-bbea-615fd67411b8" />

<img width="1919" height="964" alt="Captura de tela 2026-02-12 013925" src="https://github.com/user-attachments/assets/586db99a-a548-4d78-8517-c2795b6b3579" />

<img width="1919" height="971" alt="Captura de tela 2026-02-12 013956" src="https://github.com/user-attachments/assets/fe805564-97af-463f-8c6a-5ce2fdf88633" />





