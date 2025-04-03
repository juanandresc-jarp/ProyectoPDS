FROM openjdk:23-jdk

ARG JAR_FILE=./demo/target/*.jar

COPY ${JAR_FILE} app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app.jar"]
