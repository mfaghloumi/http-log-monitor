FROM maven:3.6.0-jdk-8 AS build
WORKDIR /workspace/app
COPY src src
COPY pom.xml pom.xml
RUN mvn clean package

FROM openjdk:8
WORKDIR /workspace/app
COPY --from=build /workspace/app/target/http-log-monitor-1.0-SNAPSHOT-jar-with-dependencies.jar http-log-monitor.jar
ENTRYPOINT ["java","-jar","/workspace/app/http-log-monitor.jar"]
