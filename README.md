# 🏥 Sistema de Agendamento de Consultas Médicas

Sistema de microserviços para agendamento de consultas médicas com notificações automáticas via RabbitMQ, **segurança implementada** com controle de acesso baseado em roles e comunicação assíncrona.

## 🏗️ Arquitetura

```
┌─────────────────┐    ┌──────────────┐    ┌─────────────────┐
│  Agendamento    │───▶│   RabbitMQ   │───▶│  Notificação    │
│    Service      │    │              │    │    Service      │
│                 │    │              │    │                 │
│ • CRUD Pacientes│    │ • Fila       │    │ • Email         │
│ • CRUD Consultas│    │ • Exchange   │    │ • SMS           │
│ • REST API      │    │ • Routing    │    │ • Logs          │
└─────────────────┘    └──────────────┘    └─────────────────┘
         │
         ▼
┌─────────────────┐
│   Histórico     │
│    Service      │
│                 │
│ • Logs          │
│ • Auditoria     │
└─────────────────┘
```

## 🚀 Serviços

### 1. Agendamento Service (Porta 9090)
- **Funcionalidades**: CRUD de pacientes, médicos e consultas via GraphQL
- **Segurança**: Autenticação HTTP Basic + controle de acesso por roles
- **Banco**: H2 em memória (desenvolvimento)
- **Integração**: Envia notificações via RabbitMQ
- **Usuários**: Médicos, Enfermeiros, Pacientes

### 2. Notificação Service (Porta 9091)
- **Funcionalidades**: Processamento de notificações
- **Comunicação**: Exclusivamente via RabbitMQ
- **Modos**: LOG, EMAIL, SMS, AMBOS
- **Integração**: Consome mensagens da fila

### 3. Histórico Service (Porta 9092)
- **Funcionalidades**: Logs e auditoria via GraphQL
- **Segurança**: Autenticação HTTP Basic + controle de acesso por roles
- **Banco**: H2 em memória (desenvolvimento)
- **Status**: ✅ Implementado e funcionando

## 🛠️ Tecnologias

- **Backend**: Java 21, Spring Boot 3.5.6
- **API**: GraphQL com Spring GraphQL
- **Segurança**: Spring Security com HTTP Basic Auth
- **Banco de Dados**: H2 (desenvolvimento), PostgreSQL (produção)
- **Message Broker**: RabbitMQ 3-management
- **Containerização**: Docker & Docker Compose
- **Build**: Maven

## 📋 Pré-requisitos

- Docker e Docker Compose
- Java 21+ (para desenvolvimento local)
- Maven 3.6+ (para desenvolvimento local)

## 🚀 Como Executar

### 1. Executar com Docker Compose (Recomendado)

```bash
# Clonar o repositório
git clone <repository-url>
cd tech_challenge_3_agendamento

# Subir todos os serviços
docker-compose up -d

# Verificar status
docker-compose ps
```

### 2. Executar Localmente

```bash
# Subir dependências (PostgreSQL, RabbitMQ)
docker-compose up -d agendamento-db historico-db rabbitmq

# Executar Agendamento Service
cd agendamento-service
mvn spring-boot:run

# Executar Notificação Service (em outro terminal)
cd notificacao-service
mvn spring-boot:run
```

## 🔧 Configuração

### Variáveis de Ambiente

```bash
# Banco de Dados
DATABASE_URL=jdbc:postgresql://localhost:5432/agendamento

# RabbitMQ
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=admin
RABBITMQ_PASSWORD=admin123

# SMTP (para notificações reais)
SMTP_USERNAME=seu_email@gmail.com
SMTP_PASSWORD=sua_senha_app
```

### Portas dos Serviços

- **Agendamento Service**: http://localhost:9090
- **Notificação Service**: http://localhost:9091
- **Histórico Service**: http://localhost:9092
- **RabbitMQ Management**: http://localhost:15672 (admin/admin123)
- **PostgreSQL Agendamento**: localhost:5432
- **PostgreSQL Histórico**: localhost:5433

## 🔐 Segurança e Usuários

### Usuários de Teste

Todos os usuários usam a senha: `password123`

| Username | Password | Role | Descrição |
|----------|----------|------|-----------|
| `medico1` | `password123` | MEDICO | Acesso total - pode visualizar/editar histórico de qualquer paciente |
| `enfermeiro1` | `password123` | ENFERMEIRO | Pode registrar consultas e acessar histórico |
| `1` | `password123` | PACIENTE | Pode visualizar apenas suas próprias consultas (ID = 1) |

### Regras de Acesso

#### **Médicos (ROLE_MEDICO)**
- ✅ Podem visualizar **todos** os pacientes
- ✅ Podem visualizar **todas** as consultas
- ✅ Podem **registrar** novas consultas
- ✅ Podem **editar** consultas existentes
- ✅ Podem **cancelar** consultas
- ✅ Podem visualizar **consultas por médico**
- ✅ Podem visualizar **todo** o histórico de qualquer paciente
- ✅ Podem **criar**, **editar**, **ativar** e **desativar** pacientes
- ✅ Podem **criar**, **editar**, **ativar** e **desativar** médicos

#### **Enfermeiros (ROLE_ENFERMEIRO)**
- ✅ Podem visualizar **todos** os pacientes
- ✅ Podem visualizar **todas** as consultas
- ✅ Podem **registrar** novas consultas
- ✅ Podem **editar** consultas existentes
- ✅ Podem **cancelar** consultas
- ✅ Podem visualizar **consultas por médico**
- ✅ Podem visualizar **todo** o histórico de qualquer paciente
- ✅ Podem **criar**, **editar**, **ativar** e **desativar** pacientes
- ✅ Podem **criar**, **editar**, **ativar** e **desativar** médicos

#### **Pacientes (ROLE_PACIENTE)**
- ✅ Podem visualizar **apenas suas** consultas
- ❌ NÃO podem visualizar consultas de outros pacientes
- ❌ NÃO podem registrar ou editar consultas
- ✅ Podem visualizar **apenas seu** histórico
- ❌ NÃO podem visualizar histórico de outros pacientes
- ✅ Podem visualizar **apenas seus** dados pessoais
- ❌ NÃO podem listar todos os pacientes
- ❌ NÃO podem criar, editar, ativar ou desativar pacientes

## 📚 API Endpoints

### Agendamento Service (GraphQL)

**Endpoint**: `POST http://localhost:9090/graphql`  
**Interface**: `http://localhost:9090/graphiql`  
**Autenticação**: HTTP Basic Auth obrigatória

#### Queries (Leitura)
```graphql
# Pacientes
query { pacientes { id nome cpf email } }                    # Médicos/Enfermeiros
query { pacientesAtivos { id nome cpf } }                    # Médicos/Enfermeiros
query { pacientePorId(id: 1) { id nome cpf } }              # Todos (com restrições)
query { pacientePorCpf(cpf: "12345678901") { id nome } }    # Médicos/Enfermeiros
query { pacientesPorNome(nome: "João") { id nome } }        # Médicos/Enfermeiros
query { pacientesPorCidade(cidade: "São Paulo") { id nome } } # Médicos/Enfermeiros

# Consultas
query { consultasPorPaciente(pacienteId: 1) { id descricao } }           # Todos (com restrições)
query { proximasConsultas(pacienteId: 1) { id dataHora } }               # Todos (com restrições)
query { consultaPorId(id: 1) { id descricao } }                         # Todos (com restrições)
query { consultasPorMedico(medicoId: 1) { id descricao } }              # Médicos/Enfermeiros
query { proximasConsultasMedico(medicoId: 1) { id dataHora } }          # Médicos/Enfermeiros
query { consultasMedicoPorPeriodo(medicoId: 1, dataInicio: "2024-01-01", dataFim: "2024-12-31") { id } } # Médicos/Enfermeiros

# Médicos
query { medicos { id nome crm especialidade } }                           # Médicos/Enfermeiros
query { medicosAtivos { id nome crm especialidade } }                     # Médicos/Enfermeiros
query { medicoPorId(id: 1) { id nome crm especialidade } }               # Médicos/Enfermeiros
query { medicoPorCrm(crm: "123456") { id nome especialidade } }          # Médicos/Enfermeiros
query { medicosPorEspecialidade(especialidade: "Cardiologia") { id nome } } # Médicos/Enfermeiros
query { medicosAtivosPorEspecialidade(especialidade: "Cardiologia") { id nome } } # Médicos/Enfermeiros
query { medicosPorNome(nome: "Dr. João") { id nome crm } }               # Médicos/Enfermeiros
query { medicosAtivosPorNome(nome: "Dr. João") { id nome crm } }         # Médicos/Enfermeiros
```

#### Mutations (Escrita)
```graphql
# Pacientes
mutation { criarPaciente(input: { nome: "João", cpf: "12345678901" }) { id } }     # Médicos/Enfermeiros
mutation { editarPaciente(id: 1, input: { nome: "João Silva" }) { id } }          # Médicos/Enfermeiros
mutation { desativarPaciente(id: 1) }                                             # Médicos/Enfermeiros
mutation { ativarPaciente(id: 1) }                                                # Médicos/Enfermeiros

# Consultas
mutation { registrarConsulta(input: { pacienteId: 1, medicoId: 1, dataHora: "2024-12-25T10:00:00", descricao: "Consulta" }) { id } } # Médicos/Enfermeiros
mutation { editarConsulta(id: 1, input: { dataHora: "2024-12-25T14:00:00" }) { id } } # Médicos/Enfermeiros
mutation { cancelarConsulta(id: 1) } # Médicos/Enfermeiros

# Médicos
mutation { criarMedico(input: { nome: "Dr. João", crm: "123456", especialidade: "Cardiologia", email: "joao@email.com" }) { id } } # Médicos/Enfermeiros
mutation { editarMedico(id: 1, input: { nome: "Dr. João Silva", email: "joao.silva@email.com" }) { id } } # Médicos/Enfermeiros
mutation { desativarMedico(id: 1) } # Médicos/Enfermeiros
mutation { ativarMedico(id: 1) } # Médicos/Enfermeiros
```

### Histórico Service (GraphQL)

**Endpoint**: `POST http://localhost:9092/graphql`  
**Interface**: `http://localhost:9092/graphiql`  
**Autenticação**: HTTP Basic Auth obrigatória

#### Queries
```graphql
query { historicoPorPaciente(pacienteId: 1) { id evento timestamp } } # Médicos/Enfermeiros (qualquer paciente), Pacientes (apenas próprio)
```

### Tipos GraphQL

#### Consulta
```graphql
type Consulta {
    id: ID
    pacienteId: ID
    medicoId: ID
    dataHora: String
    descricao: String
}
```

#### Paciente
```graphql
type Paciente {
    id: ID
    nome: String
    cpf: String
    email: String
    telefone: String
    dataNascimento: String
    endereco: String
    cidade: String
    estado: String
    cep: String
    ativo: Boolean
    dataCriacao: String
    dataAtualizacao: String
}
```

#### Médico
```graphql
type Medico {
    id: ID
    nome: String
    crm: String
    especialidade: String
    email: String
    telefone: String
    ativo: Boolean
    dataCriacao: String
    dataAtualizacao: String
}
```

#### Inputs
```graphql
input ConsultaInput {
    pacienteId: ID!
    medicoId: ID!
    dataHora: String!
    descricao: String
}

input PacienteInput {
    nome: String!
    cpf: String!
    email: String
    telefone: String
    dataNascimento: String!
    endereco: String
    cidade: String
    estado: String
    cep: String
    ativo: Boolean
}

input MedicoInput {
    nome: String!
    crm: String!
    especialidade: String!
    email: String
    telefone: String
    ativo: Boolean
}
```

### Notificação Service

**⚠️ IMPORTANTE**: O serviço de notificação **não possui endpoints REST**. Toda comunicação é feita via RabbitMQ.

```http
GET /actuator/health            # Health check
```

## 🧪 Testes

### Testes com cURL

#### 1. Teste como Médico (deve funcionar)
```bash
# Listar todos os pacientes
curl -X POST http://localhost:9090/graphql \
  -u medico1:password123 \
  -H "Content-Type: application/json" \
  -d '{"query":"{ pacientes { id nome cpf } }"}'

# Registrar consulta
curl -X POST http://localhost:9090/graphql \
  -u medico1:password123 \
  -H "Content-Type: application/json" \
  -d '{"query":"mutation { registrarConsulta(input: { pacienteId: 1, medicoId: 1, dataHora: \"2024-12-25T10:00:00\", descricao: \"Consulta de rotina\" }) { id descricao } }"}'

# Listar médicos
curl -X POST http://localhost:9090/graphql \
  -u medico1:password123 \
  -H "Content-Type: application/json" \
  -d '{"query":"{ medicos { id nome crm especialidade } }"}'

# Consultas por médico
curl -X POST http://localhost:9090/graphql \
  -u medico1:password123 \
  -H "Content-Type: application/json" \
  -d '{"query":"{ consultasPorMedico(medicoId: 1) { id descricao dataHora } }"}'
```

#### 2. Teste como Paciente (acesso restrito)
```bash
# Ver suas próprias consultas (deve funcionar)
curl -X POST http://localhost:9090/graphql \
  -u 1:password123 \
  -H "Content-Type: application/json" \
  -d '{"query":"{ consultasPorPaciente(pacienteId: 1) { id descricao } }"}'

# Tentar listar todos os pacientes (deve falhar - 403 Forbidden)
curl -X POST http://localhost:9090/graphql \
  -u 1:password123 \
  -H "Content-Type: application/json" \
  -d '{"query":"{ pacientes { id nome } }"}'
```

#### 3. Teste sem autenticação (deve falhar - 401 Unauthorized)
```bash
curl -X POST http://localhost:9090/graphql \
  -H "Content-Type: application/json" \
  -d '{"query":"{ pacientes { id nome } }"}'
```

#### 4. Teste de Histórico
```bash
# Médico vendo histórico de qualquer paciente (deve funcionar)
curl -X POST http://localhost:9092/graphql \
  -u medico1:password123 \
  -H "Content-Type: application/json" \
  -d '{"query":"{ historicoPorPaciente(pacienteId: 1) { id evento } }"}'

# Paciente vendo seu próprio histórico (deve funcionar)
curl -X POST http://localhost:9092/graphql \
  -u 1:password123 \
  -H "Content-Type: application/json" \
  -d '{"query":"{ historicoPorPaciente(pacienteId: 1) { id evento } }"}'
```

### Testes com Postman

1. **Importar Collection**: Use o arquivo `FIAP 24 - Tech Challenge 3.postman_collection.json`
2. **Executar Testes**: A collection inclui testes organizados por tipo de usuário e funcionalidade:
   - **Autenticação**: Testes de login para cada role
   - **Pacientes**: CRUD completo
   - **Médicos**: CRUD completo
   - **Consultas**: CRUD + cancelamento + consultas por médico
   - **Histórico**: Consultas de auditoria
   - **Segurança**: Testes de autorização
3. **Verificar Resultados**: 
   - Médicos/Enfermeiros: 200 OK
   - Pacientes (dados próprios): 200 OK
   - Pacientes (dados de outros): 403 Forbidden
   - Sem autenticação: 401 Unauthorized

### Testes com GraphiQL

1. **Agendamento**: http://localhost:9090/graphiql
2. **Histórico**: http://localhost:9092/graphiql
3. **Autenticação**: Use Basic Auth com os usuários de teste

### Health Checks
```bash
# Verificar status dos serviços
curl http://localhost:9090/actuator/health  # Agendamento
curl http://localhost:9091/actuator/health  # Notificação
curl http://localhost:9092/actuator/health  # Histórico

# RabbitMQ Management UI
# Acesse: http://localhost:15672 (admin/admin123)
```

## 📊 Monitoramento

### Health Checks
- **Agendamento**: http://localhost:9090/actuator/health
- **Notificação**: http://localhost:9091/actuator/health
- **Histórico**: http://localhost:9092/actuator/health
- **RabbitMQ**: http://localhost:15672

### Logs
```bash
# Ver logs de todos os serviços
docker-compose logs -f

# Ver logs de um serviço específico
docker-compose logs -f agendamento-service
docker-compose logs -f notificacao-service
docker-compose logs -f historico-service
```

## 🔄 Fluxo de Notificações

1. **Cliente** cria/atualiza consulta via Agendamento Service
2. **Agendamento Service** envia mensagem para RabbitMQ
3. **Notificação Service** consome mensagem da fila
4. **Notificação Service** processa e envia notificação (email/SMS/log)

### Formato da Mensagem RabbitMQ
```json
{
  "consultaId": 1,
  "pacienteId": 1,
  "mensagem": "Sua consulta foi agendada para 15/10/2025 às 10:00",
  "tipo": "CONSULTA",
  "timestamp": "2025-10-01T23:00:00Z"
}
```

## 🚨 Troubleshooting

### Problemas Comuns

1. **Serviços não iniciam**
   ```bash
   # Verificar logs
   docker-compose logs
   
   # Verificar se as portas estão livres
   netstat -tulpn | grep :9090
   ```

2. **RabbitMQ não conecta**
   ```bash
   # Verificar se RabbitMQ está rodando
   docker-compose ps rabbitmq
   
   # Verificar logs
   docker-compose logs rabbitmq
   ```

3. **Banco de dados não conecta**
   ```bash
   # Verificar se PostgreSQL está rodando
   docker-compose ps agendamento-db
   
   # Verificar logs
   docker-compose logs agendamento-db
   ```

4. **Notificações não são enviadas**
   ```bash
   # Verificar se a fila RabbitMQ está configurada
   # Acessar http://localhost:15672
   
   # Verificar logs do serviço de notificação
   docker-compose logs notificacao-service
   ```

## 📁 Estrutura do Projeto

```
tech_challenge_3_agendamento/
├── agendamento-service/          # Serviço de agendamento
│   ├── src/main/java/           # Código fonte
│   ├── src/main/resources/      # Configurações
│   ├── Dockerfile               # Imagem Docker
│   └── pom.xml                  # Dependências Maven
├── notificacao-service/         # Serviço de notificação
│   ├── src/main/java/           # Código fonte
│   ├── src/main/resources/      # Configurações
│   ├── Dockerfile               # Imagem Docker
│   ├── pom.xml                  # Dependências Maven
│   └── README.md                # Documentação específica
├── historico-service/           # Serviço de histórico
├── docker-compose.yml           # Orquestração dos serviços
└── README.md                    # Este arquivo
```

## 📚 Documentação Adicional

- [Notificação Service](notificacao-service/README.md)
- [Como Configurar Email Real](COMO-CONFIGURAR-EMAIL.md)
- [Coleção Postman](FIAP%2024%20-%20Tech%20Challenge%203.postman_collection.json)

## 🤝 Contribuição

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

## 👥 Equipe

- **Desenvolvimento**: Equipe FIAP Tech Challenge 3
- **Arquitetura**: Microserviços com Spring Boot
- **Integração**: RabbitMQ para comunicação assíncrona
