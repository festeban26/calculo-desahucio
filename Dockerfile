#
# Build stage
#
FROM maven:3.8.3 AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package -DskipTests

#
# Package stage
#
FROM openjdk:17.0.2-jdk
COPY --from=build /home/app/target/*.jar /usr/local/lib/app.jar
COPY --from=build /home/app/target/*.jar /app/*.jar
WORKDIR /app
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/local/lib/app.jar"]
