# Gerenciador de Chaveamento em Torneios 🏆

Sistema para gerenciamento de torneios de eliminação simples com atribuição dinâmica de jogadores, desenvolvido em Spring Boot.

## Inspiração

Este projeto nasceu da minha paixão por torneios de jogos de luta como o [Red Bull Kumite](https://liquipedia.net/fighters/Red_Bull_Kumite/2025). O objetivo foi criar um sistema robusto que pudesse gerenciar chaveamentos de forma eficiente, aplicando conceitos de desenvolvimento backend.

## Funcionalidades Principais

- **Gerenciamento de Torneios**
  - API REST para inicialização do campeonato
  - Seleção e deseleção de vencedores
  - Armazenamento em memória com padrão Singleton
  - Arquitetura MVC bem definida

- **Gestão de Jogadores**
  - Operações CRUD completas para participantes
  - Persistência em banco de dados H2 (embutido)

- **Sistema de Chaveamento Inteligente**
  - Geração de brackets usando árvores binárias
  - Implementação de estratégias de distribuição:
    - Atribuição aleatória
    - Distribuição balanceada (jogadores fortes em chaves diferentes)
  - Tratamento de casos especiais (byes, número ímpar de jogadores)

- **Visualização Integrada**
  - Frontend básico para acompanhamento do torneio (HTML/JavaScript)

## Tecnologias Utilizadas

| Camada           | Tecnologias                                                                 |
|------------------|-----------------------------------------------------------------------------|
| **Backend**      | Java 17, Spring Boot 3.x, Spring Data JPA, Spring MVC                       |
| **Banco de Dados**| H2 Database 	                                                        |
| **Testes**       | JUnit 5, Mockito, Spring Boot Test                                         |
| **Build**        | Maven 3.8+                                                                 |
| **Controle de Versão** | Git                                                                       |

## Como Executar o Projeto

### Pré-requisitos
- Java 17+
- Maven 3.8+

### Passo a Passo
```bash
# Clonar repositório
git clone https://github.com/seu-usuario/tournament-bracket-system.git

# Navegar para o diretório
cd tournament-bracket-system

# Instalar dependências e construir
mvn clean install

# Executar aplicação
mvn spring-boot:run

# Acessar recursos
 - API REST: http://localhost:8080
 - Console H2: http://localhost:8080/h2-console