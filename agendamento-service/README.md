# Agendamento Service

Serviço responsável pelo gerenciamento de pacientes e consultas médicas, com integração ao sistema de notificações via RabbitMQ.

## 🚀 Funcionalidades

- **CRUD de Pacientes**: Criar, listar, buscar, atualizar e deletar pacientes
- **CRUD de Consultas**: Gerenciar agendamentos de consultas
- **API REST**: Endpoints para integração com frontend
- **Integração RabbitMQ**: Envia notificações automáticas
- **Banco PostgreSQL**: Persistência de dados
- **Health Checks**: Monitoramento via Actuator

## 📋 Entidades

### Paciente
```json
{
  "id": 1,
  "nome": "João Silva",
  "email": "joao@email.com",
  "telefone": "11999999999",
  "dataNascimento": "1990-01-01",
  "endereco": "Rua das Flores, 123"
}
```

### Consulta
```json
{
  "id": 1,
  "pacienteId": 1,
  "dataHora": "2025-10-15T10:00:00",
  "observacoes": "Consulta de rotina",
  "status": "AGENDADA"
}
```

## 🔧 API Endpoints

### Pacientes

#### Listar Pacientes
```http
GET /api/pacientes
```

#### Criar Paciente
```http
POST /api/pacientes
Content-Type: application/json

{
  "nome": "João Silva",
  "email": "joao@email.com",
  "telefone": "11999999999",
  "dataNascimento": "1990-01-01",
  "endereco": "Rua das Flores, 123"
}
```

#### Buscar Paciente
```http
GET /api/pacientes/{id}
```

#### Atualizar Paciente
```http
PUT /api/pacientes/{id}
Content-Type: application/json

{
  "nome": "João Silva Santos",
  "email": "joao.santos@email.com",
  "telefone": "11999999999",
  "dataNascimento": "1990-01-01",
  "endereco": "Rua das Flores, 123"
}
```

#### Deletar Paciente
```http
DELETE /api/pacientes/{id}
```

### Consultas

#### Listar Consultas
```http
GET /api/consultas
```

#### Criar Consulta
```http
POST /api/consultas
Content-Type: application/json

{
  "pacienteId": 1,
  "dataHora": "2025-10-15T10:00:00",
  "observacoes": "Consulta de rotina"
}
```

#### Buscar Consulta
```http
GET /api/consultas/{id}
```

#### Atualizar Consulta
```http
PUT /api/consultas/{id}
Content-Type: application/json

{
  "pacienteId": 1,
  "dataHora": "2025-10-15T14:00:00",
  "observacoes": "Consulta remarcada"
}
```

#### Deletar Consulta
```http
DELETE /api/consultas/{id}
```

### Health Check
```http
GET /actuator/health
```

## ⚙️ Configuração

### Variáveis de Ambiente

```bash
# Banco de Dados
DATABASE_URL=jdbc:postgresql://localhost:5432/agendamento
DB_USERNAME=agendamento
DB_PASSWORD=agendamento123

# RabbitMQ
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=admin
RABBITMQ_PASSWORD=admin123

# Servidor
SERVER_PORT=9090
```

### application.properties

```properties
# Servidor
server.port=9090

# Banco de Dados
spring.datasource.url=jdbc:postgresql://localhost:5432/agendamento
spring.datasource.username=agendamento
spring.datasource.password=agendamento123
spring.jpa.hibernate.ddl-auto=update

# RabbitMQ
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=admin
spring.rabbitmq.password=admin123

# Actuator
management.endpoints.web.exposure.include=health,info,metrics
```

## 🐳 Docker

### Construir a imagem
```bash
cd agendamento-service
mvn clean package
docker build -t agendamento-service .
```

### Executar com Docker Compose
```bash
docker-compose up agendamento-service
```

## 🧪 Testes

### Testar Endpoints

#### 1. Criar Paciente
```bash
curl -X POST http://localhost:9090/api/pacientes \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Maria Santos",
    "email": "maria@email.com",
    "telefone": "11988888888",
    "dataNascimento": "1985-05-15",
    "endereco": "Av. Paulista, 1000"
  }'
```

#### 2. Listar Pacientes
```bash
curl http://localhost:9090/api/pacientes
```

#### 3. Criar Consulta
```bash
curl -X POST http://localhost:9090/api/consultas \
  -H "Content-Type: application/json" \
  -d '{
    "pacienteId": 1,
    "dataHora": "2025-10-15T10:00:00",
    "observacoes": "Consulta de rotina"
  }'
```

#### 4. Listar Consultas
```bash
curl http://localhost:9090/api/consultas
```

### Testar Health Check
```bash
curl http://localhost:9090/actuator/health
```

## 🔄 Integração com Notificações

Quando uma consulta é criada, atualizada ou deletada, o serviço automaticamente envia uma mensagem para o RabbitMQ:

### Fila de Notificações
- **Fila**: `notificacoes.queue`
- **Exchange**: `notificacoes.exchange`
- **Routing Key**: `notificacao.consulta`

### Formato da Mensagem
```json
{
  "consultaId": 1,
  "pacienteId": 1,
  "mensagem": "Sua consulta foi agendada para 15/10/2025 às 10:00",
  "tipo": "CONSULTA",
  "timestamp": "2025-10-01T23:00:00Z"
}
```

## 🗄️ Banco de Dados

### Tabelas

#### pacientes
```sql
CREATE TABLE pacientes (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    telefone VARCHAR(20),
    data_nascimento DATE,
    endereco TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### consultas
```sql
CREATE TABLE consultas (
    id BIGSERIAL PRIMARY KEY,
    paciente_id BIGINT REFERENCES pacientes(id),
    data_hora TIMESTAMP NOT NULL,
    observacoes TEXT,
    status VARCHAR(50) DEFAULT 'AGENDADA',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## 🚨 Troubleshooting

### Problemas Comuns

1. **Banco de dados não conecta**
   ```bash
   # Verificar se PostgreSQL está rodando
   docker-compose ps agendamento-db
   
   # Verificar logs
   docker-compose logs agendamento-db
   ```

2. **RabbitMQ não conecta**
   ```bash
   # Verificar se RabbitMQ está rodando
   docker-compose ps rabbitmq
   
   # Verificar logs
   docker-compose logs rabbitmq
   ```

3. **Porta 9090 já está em uso**
   ```bash
   # Verificar processos usando a porta
   lsof -i :9090
   
   # Matar processo se necessário
   kill -9 <PID>
   ```

4. **Erro de compilação**
   ```bash
   # Limpar e recompilar
   mvn clean compile
   
   # Verificar versão do Java
   java -version
   ```

## 📊 Monitoramento

### Health Checks
- **Status**: http://localhost:9090/actuator/health
- **Info**: http://localhost:9090/actuator/info
- **Métricas**: http://localhost:9090/actuator/metrics

### Logs
```bash
# Ver logs do serviço
docker-compose logs -f agendamento-service

# Ver logs com filtro
docker-compose logs -f agendamento-service | grep ERROR
```

## 🏗️ Arquitetura

```
┌─────────────────┐    ┌──────────────┐    ┌─────────────────┐
│   Frontend      │───▶│  Agendamento │───▶│   RabbitMQ      │
│                 │    │    Service   │    │                 │
│ • Web App       │    │              │    │ • Fila          │
│ • Mobile App    │    │ • REST API   │    │ • Exchange      │
│ • Postman       │    │ • PostgreSQL │    │ • Routing       │
└─────────────────┘    └──────────────┘    └──────────────┘
                                │
                                ▼
                       ┌─────────────────┐
                       │  Notificação    │
                       │    Service      │
                       │                 │
                       │ • Email         │
                       │ • SMS           │
                       │ • Logs          │
                       └─────────────────┘
```

## 📚 Dependências Principais

- **Spring Boot**: 3.5.6
- **Spring Data JPA**: Persistência
- **Spring Web**: REST API
- **Spring AMQP**: RabbitMQ
- **PostgreSQL Driver**: Banco de dados
- **Spring Boot Actuator**: Monitoramento
- **Lombok**: Redução de boilerplate

## 🔐 Segurança

- **CORS**: Configurado para permitir requisições do frontend
- **Validação**: Validação de dados de entrada
- **Tratamento de Erros**: Respostas padronizadas para erros
- **Logs**: Logs de auditoria para operações

## 📈 Próximos Passos

- [ ] Implementar autenticação e autorização
- [ ] Adicionar paginação nas listagens
- [ ] Implementar cache com Redis
- [ ] Adicionar testes unitários e de integração
- [ ] Implementar rate limiting
- [ ] Adicionar documentação Swagger/OpenAPI
