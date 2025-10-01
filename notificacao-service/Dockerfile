FROM openjdk:21-jdk-slim

WORKDIR /app

# Instalar curl para health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Copiar o arquivo JAR
COPY target/notificacao-service-*.jar app.jar

# Expor a porta
EXPOSE 9091

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:9091/api/notificacao/health || exit 1

# Comando para executar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
