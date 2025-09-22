FROM openjdk:21-jdk-slim

WORKDIR /app

# Copiar o arquivo JAR
COPY target/agendamento-*.jar app.jar

# Expor a porta
EXPOSE 9090

# Comando para executar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
