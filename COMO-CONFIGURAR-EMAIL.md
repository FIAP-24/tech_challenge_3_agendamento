# 📧 Como Configurar Email Real no Serviço de Notificação

## 🎯 Objetivo
Configurar o serviço de notificação para enviar emails reais via SMTP, mantendo SMS apenas como log.

## 📋 Passos para Configuração

### 1. Configurar Credenciais SMTP

#### Para Gmail:
```bash
# Usar senha de app (não a senha normal)
export SMTP_USERNAME=seu_email@gmail.com
export SMTP_PASSWORD=sua_senha_de_app_do_gmail
```

#### Para Outlook/Hotmail:
```bash
export SMTP_USERNAME=seu_email@outlook.com
export SMTP_PASSWORD=sua_senha_do_outlook
```

### 2. Configurar Modo de Notificação
```bash
# Para apenas email real
export NOTIFICACAO_MODO=EMAIL

# Para email real + SMS log
export NOTIFICACAO_MODO=AMBOS
```

### 3. Atualizar application-docker.properties

Adicione estas configurações ao arquivo `notificacao-service/src/main/resources/application-docker.properties`:

```properties
# Configuração SMTP para Gmail
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${SMTP_USERNAME}
spring.mail.password=${SMTP_PASSWORD}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
spring.mail.properties.mail.smtp.ssl.trust=smtp.gmail.com
```

### 4. Testar Configuração

#### Subir os serviços:
```bash
docker-compose up -d rabbitmq notificacao-service
```

#### Testar configuração de email:
```bash
curl -X POST "http://localhost:9091/api/notificacao/teste-email?pacienteId=1"
```

#### Verificar status:
```bash
curl "http://localhost:9091/api/notificacao/status"
```

## 🔍 Logs Esperados

### Email Real Enviado:
```
INFO - 📧 Processando notificação por EMAIL para paciente ID: 1
INFO - ✅ Email enviado com sucesso para paciente ID: 1 - Email: paciente1@exemplo.com
```

### Email com Fallback (SMTP não configurado):
```
INFO - 📧 Processando notificação por EMAIL para paciente ID: 1
WARN - ⚠️ JavaMailSender não configurado. Verifique as configurações SMTP.
INFO - 📧 SIMULAÇÃO: Email enviado para paciente ID 1
```

### SMS (sempre log):
```
INFO - 📱 Processando notificação por SMS para paciente ID: 1
INFO - 📱 === ENVIO DE SMS (LOG) ===
INFO - 📱 Paciente ID: 1
INFO - 📱 Mensagem: Sua consulta foi agendada
INFO - 📱 Status: SIMULADO (não enviado realmente)
```

## 🧪 Teste Completo

### 1. Teste via API:
```bash
# Testar notificação completa
curl -X POST http://localhost:9091/api/notificacao/teste \
  -H "Content-Type: application/json" \
  -d '{
    "consultaId": 1,
    "pacienteId": 1,
    "mensagem": "Teste de notificação com email real"
  }'
```

### 2. Teste via GraphQL (integração completa):
1. Acesse: http://localhost:9090/graphiql
2. Crie um paciente
3. Registre uma consulta
4. Verifique os logs do serviço de notificação

## 🔧 Configurações Avançadas

### Personalizar Templates:
```properties
notificacao.email.assunto-padrao=Sistema Médico - Notificação de Consulta
notificacao.email.template-padrao=Olá, você tem uma nova notificação sobre sua consulta médica.
```

### Configurar Outros Provedores SMTP:

#### Outlook:
```properties
spring.mail.host=smtp-mail.outlook.com
spring.mail.port=587
```

#### Yahoo:
```properties
spring.mail.host=smtp.mail.yahoo.com
spring.mail.port=587
```

## 🚨 Troubleshooting

### Problema: Email não envia
1. Verificar se SMTP_USERNAME e SMTP_PASSWORD estão configurados
2. Para Gmail, usar "Senha de App" não a senha normal
3. Verificar logs para erros específicos
4. Testar com endpoint `/api/notificacao/teste-email`

### Problema: "JavaMailSender não configurado"
1. Verificar se as configurações SMTP estão no application-docker.properties
2. Verificar se as variáveis de ambiente estão definidas
3. Reiniciar o serviço após configurar

### Problema: "Authentication failed"
1. Verificar credenciais
2. Para Gmail, habilitar "Acesso menos seguro" ou usar Senha de App
3. Verificar se a conta não tem 2FA sem Senha de App

## ✅ Verificação Final

Após configurar, você deve ver:

1. **Status do serviço**:
```json
{
  "modo": "EMAIL",
  "email": {
    "habilitado": true,
    "disponivel": true
  }
}
```

2. **Logs de email real**:
```
INFO - ✅ Email enviado com sucesso para paciente ID: 1
```

3. **Teste de email funcionando**:
```bash
curl -X POST "http://localhost:9091/api/notificacao/teste-email?pacienteId=1"
# Deve retornar: {"sucesso": true, "emailDisponivel": true}
```

## 🎉 Resultado

Com essa configuração, você terá:
- ✅ **Email real** enviado via SMTP
- ✅ **SMS apenas log** (sem provedores reais)
- ✅ **Fallback inteligente** se SMTP não configurado
- ✅ **Testes automatizados** para validar configuração
- ✅ **Logs detalhados** para debugging

