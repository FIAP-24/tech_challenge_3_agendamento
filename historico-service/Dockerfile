FROM openjdk:21-jdk-slim

WORKDIR /app

# Copiar o arquivo JAR
COPY target/historico-service-*.jar app.jar

# Expor a porta
EXPOSE 9092

# Comando para executar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
