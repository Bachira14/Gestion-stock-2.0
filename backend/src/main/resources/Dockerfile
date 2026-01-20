# Backend Dockerfile
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app

# Copier le jar
COPY target/backend-0.0.1-SNAPSHOT.jar app.jar

# Copier le script wait-for-it
COPY wait-for-it.sh .

# Autoriser l’exécution du script
RUN chmod +x wait-for-it.sh

# Démarrer le backend après que MySQL soit prêt
CMD ["./wait-for-it.sh", "mysql:3306", "--", "java", "-jar", "app.jar"]
