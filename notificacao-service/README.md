# Serviço de Notificação

Este serviço é responsável por processar e enviar notificações relacionadas a consultas médicas, incluindo agendamentos, remarcações e lembretes. **Funciona exclusivamente via RabbitMQ** - não possui endpoints REST.

## 🚀 Funcionalidades

- **Múltiplos modos de notificação**: LOG, EMAIL, SMS, AMBOS
- **Integração RabbitMQ**: Consome mensagens do serviço de agendamento
- **Simulação de SMS**: Suporte para Twilio, AWS SNS, Vonage
- **Configuração flexível**: Via properties ou variáveis de ambiente
- **Health checks**: Monitoramento de status do serviço via Actuator
- **Comunicação assíncrona**: Processamento via filas RabbitMQ

## 📋 Modos de Notificação

### LOG (Padrão)
- Apenas registra as notificações no log
- Ideal para desenvolvimento e testes
- Simula envios de email e SMS

### EMAIL
- **Envia notificações reais por email via SMTP**
- Requer configuração SMTP (Gmail, Outlook, etc.)
- Template configurável
- Fallback para log se SMTP não configurado

### SMS
- **Apenas log (sem provedores reais)**
- Simula envio de SMS
- Pode ser expandido para provedores reais no futuro

### AMBOS
- Envia email real E log de SMS simultaneamente

## ⚙️ Configuração

### Variáveis de Ambiente

```bash
# Modo de notificação
NOTIFICACAO_MODO=LOG          # LOG, EMAIL, SMS, AMBOS

# Configurações de Email
EMAIL_HABILITADO=true
EMAIL_ASSUNTO=Notificação de Consulta Médica
EMAIL_TEMPLATE=Olá, você tem uma nova notificação...

# Configurações de SMS
SMS_HABILITADO=false
SMS_PROVEDOR=TWILIO           # TWILIO, AWS_SNS, VONAGE
SMS_TEMPLATE=Lembrete: Você tem uma consulta...

# SMTP (para email)
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USERNAME=seu_email@gmail.com
SMTP_PASSWORD=sua_senha_app

# RabbitMQ
RABBITMQ_HOST=rabbitmq
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=admin
RABBITMQ_PASSWORD=admin123
```

## 🐰 Comunicação via RabbitMQ

### Configuração da Fila
- **Fila**: `notificacoes.queue`
- **Exchange**: `notificacoes.exchange`
- **Routing Key**: `notificacao.consulta`

### Formato da Mensagem
```json
{
  "consultaId": 1,
  "pacienteId": 1,
  "mensagem": "Sua consulta foi agendada para amanhã às 10:00",
  "tipo": "CONSULTA",
  "timestamp": "2025-10-01T23:00:00Z"
}
```

## 🔧 Endpoints Disponíveis

### Health Check (Actuator)
```http
GET /actuator/health
```

### Informações do Actuator
```http
GET /actuator
```

**⚠️ IMPORTANTE**: Este serviço **NÃO possui endpoints REST** para notificações. Toda comunicação é feita via RabbitMQ.

## 🐳 Docker

### Construir a imagem
```bash
cd notificacao-service
mvn clean package
docker build -t notificacao-service .
```

### Executar com Docker Compose
```bash
docker-compose up notificacao-service
```

## 📝 Exemplos de Uso

### 1. Modo LOG (Desenvolvimento)
```bash
export NOTIFICACAO_MODO=LOG
java -jar notificacao-service.jar
```

### 2. Modo EMAIL (Produção)
```bash
export NOTIFICACAO_MODO=EMAIL
export SMTP_USERNAME=seu_email@gmail.com
export SMTP_PASSWORD=sua_senha_app
java -jar notificacao-service.jar
```

### 3. Modo AMBOS (Email real + SMS log)
```bash
export NOTIFICACAO_MODO=AMBOS
export SMTP_USERNAME=seu_email@gmail.com
export SMTP_PASSWORD=sua_senha_app
java -jar notificacao-service.jar
```

## 🔍 Logs

O serviço gera logs detalhados para cada operação:

```
INFO  - 📨 Nova mensagem recebida da fila: NotificacaoDTO{...}
INFO  - === NOTIFICAÇÃO DE CONSULTA ===
INFO  - Consulta ID: 1
INFO  - Paciente ID: 1
INFO  - Mensagem: Sua consulta foi agendada
INFO  - 📧 SIMULAÇÃO: Email enviado para paciente ID 1
INFO  - 📱 SIMULAÇÃO: SMS enviado para paciente ID 1
INFO  - ✅ Notificação processada com sucesso
```

## 🧪 Testes

### Testar via RabbitMQ Management UI
1. Acesse: http://localhost:15672
2. Login: admin / admin123
3. Vá para "Queues" → "notificacoes.queue"
4. Publique uma mensagem com o formato JSON acima

### Testar via curl (RabbitMQ API)
```bash
curl -u admin:admin123 -H "content-type:application/json" \
  -X POST -d '{"consultaId":1,"pacienteId":1,"mensagem":"Teste via RabbitMQ","tipo":"CONSULTA"}' \
  http://localhost:15672/api/exchanges/%2F/notificacoes.exchange/publish
```

### Testar via RabbitMQ Management UI
```bash
# Acesse: http://localhost:15672
# Login: admin / admin123
# Vá para "Queues" → "notificacoes.queue"
# Publique uma mensagem com o formato JSON acima
```

## 🚨 Troubleshooting

### Problemas Comuns

1. **RabbitMQ não conecta**
   - Verificar se RabbitMQ está rodando
   - Conferir credenciais e host/porta
   - Verificar logs: `docker-compose logs notificacao-service`

2. **Email não envia**
   - Verificar configurações SMTP
   - Confirmar credenciais do email
   - Verificar se modo está como EMAIL
   - Ver logs para erros específicos

3. **SMS não envia**
   - Verificar se modo está como SMS
   - Confirmar provedor configurado
   - Verificar logs para erros específicos

4. **Mensagens não são processadas**
   - Verificar se a fila RabbitMQ está configurada
   - Confirmar se o consumer está ativo
   - Verificar logs do consumer

### Logs de Debug
```bash
# Habilitar logs detalhados
export LOGGING_LEVEL_COM_CHALLENGE_NOTIFICACAO=DEBUG
export LOGGING_LEVEL_ORG_SPRINGFRAMEWORK_AMQP=DEBUG
```

## 🔄 Integração

O serviço se integra automaticamente com:
- **Agendamento Service**: Recebe mensagens via RabbitMQ
- **RabbitMQ**: Fila `notificacoes.queue`
- **SMTP**: Para envio de emails
- **Provedores SMS**: Twilio, AWS SNS, Vonage (simulação)

## 📊 Monitoramento

- Health checks automáticos via Actuator
- Métricas do Actuator
- Logs estruturados
- Status via `/actuator/health`

## 🏗️ Arquitetura

```
Agendamento Service → RabbitMQ → Notificação Service
                           ↓
                    notificacoes.queue
                           ↓
                    NotificacaoConsumer
                           ↓
                    NotificacaoService
                           ↓
                    EmailService / SmsService
```

## 📚 Documentação Adicional

- [Como Configurar Email Real](../COMO-CONFIGURAR-EMAIL.md)