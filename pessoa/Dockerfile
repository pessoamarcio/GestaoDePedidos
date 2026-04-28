#
# Multi-stage build:
# 1) Build the Spring Boot jar using Maven Wrapper
# 2) Run it on a slim JRE image
#

FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
COPY src/ src/

RUN chmod +x mvnw
RUN ./mvnw -DskipTests package

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/*.jar /app/app.jar

EXPOSE 8080

# Database config comes from environment variables (DB_URL/DB_USER/DB_PASS)
ENTRYPOINT ["java","-jar","/app/app.jar"]