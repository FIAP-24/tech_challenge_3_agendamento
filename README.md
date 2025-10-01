# 🏥 Sistema de Agendamento de Consultas Médicas

Sistema de microserviços para agendamento de consultas médicas com notificações automáticas via RabbitMQ.

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
- **Funcionalidades**: CRUD de pacientes e consultas
- **API REST**: Endpoints para gerenciamento
- **Banco**: PostgreSQL
- **Integração**: Envia notificações via RabbitMQ

### 2. Notificação Service (Porta 9091)
- **Funcionalidades**: Processamento de notificações
- **Comunicação**: Exclusivamente via RabbitMQ
- **Modos**: LOG, EMAIL, SMS, AMBOS
- **Integração**: Consome mensagens da fila

### 3. Histórico Service (Porta 9092)
- **Funcionalidades**: Logs e auditoria
- **Banco**: PostgreSQL
- **Status**: Em desenvolvimento

## 🛠️ Tecnologias

- **Backend**: Java 21, Spring Boot 3.5.6
- **Banco de Dados**: PostgreSQL 15
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

## 📚 API Endpoints

### Agendamento Service

#### Pacientes
```http
GET    /api/pacientes           # Listar pacientes
POST   /api/pacientes           # Criar paciente
GET    /api/pacientes/{id}      # Buscar paciente
PUT    /api/pacientes/{id}      # Atualizar paciente
DELETE /api/pacientes/{id}      # Deletar paciente
```

#### Consultas
```http
GET    /api/consultas           # Listar consultas
POST   /api/consultas           # Criar consulta
GET    /api/consultas/{id}      # Buscar consulta
PUT    /api/consultas/{id}      # Atualizar consulta
DELETE /api/consultas/{id}      # Deletar consulta
```

### Notificação Service

**⚠️ IMPORTANTE**: O serviço de notificação **não possui endpoints REST**. Toda comunicação é feita via RabbitMQ.

```http
GET /actuator/health            # Health check
```

## 🧪 Testes

### Testar Agendamento Service
```bash
# Verificar status
curl http://localhost:9090/actuator/health

# Criar paciente
curl -X POST http://localhost:9090/api/pacientes \
  -H "Content-Type: application/json" \
  -d '{"nome":"João Silva","email":"joao@email.com","telefone":"11999999999"}'

# Criar consulta
curl -X POST http://localhost:9090/api/consultas \
  -H "Content-Type: application/json" \
  -d '{"pacienteId":1,"dataHora":"2025-10-15T10:00:00","observacoes":"Consulta de rotina"}'
```

### Testar Notificação Service
```bash
# Verificar status
curl http://localhost:9091/actuator/health

# Testar via RabbitMQ Management UI
# Acesse: http://localhost:15672 (admin/admin123)
```

## 📊 Monitoramento

### Health Checks
- **Agendamento**: http://localhost:9090/actuator/health
- **Notificação**: http://localhost:9091/actuator/health
- **RabbitMQ**: http://localhost:15672

### Logs
```bash
# Ver logs de todos os serviços
docker-compose logs -f

# Ver logs de um serviço específico
docker-compose logs -f agendamento-service
docker-compose logs -f notificacao-service
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
