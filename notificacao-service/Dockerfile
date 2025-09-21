FROM openjdk:21-jdk-slim

WORKDIR /app

# Copiar o arquivo JAR
COPY target/notificacao-service-*.jar app.jar

# Expor a porta
EXPOSE 9091

# Comando para executar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
