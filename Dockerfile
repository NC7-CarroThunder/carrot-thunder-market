FROM  --platform=linux/amd64 openjdk:17-alpine
COPY app/build/libs/app-0.0.1-SNAPSHOT.jar /app-0.0.1-SNAPSHOT.jar
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "app-0.0.1-SNAPSHOT.jar"]