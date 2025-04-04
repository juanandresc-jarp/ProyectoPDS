FROM openjdk:23-jdk

COPY . /spring

WORKDIR /spring/demo

RUN ./mvnw clean package

WORKDIR /spring/demo/target

RUN ls -a

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "demo-0.0.1-SNAPSHOT.jar"]
