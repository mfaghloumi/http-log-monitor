FROM maven:3.6.0-jdk-8 AS build
WORKDIR /workspace/app
COPY src src
COPY pom.xml pom.xml
RUN mvn clean package

FROM openjdk:8
WORKDIR /workspace/app
COPY --from=build /workspace/app/target/log-monitor-1.0-SNAPSHOT-jar-with-dependencies.jar log-monitor-1.0-SNAPSHOT-jar-with-dependencies.jar
ENTRYPOINT ["java","-jar","/workspace/app/log-monitor-1.0-SNAPSHOT-jar-with-dependencies.jar"]
