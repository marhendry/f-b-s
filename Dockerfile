FROM openjdk:17-alpine
ARG JAR_FILE=build/libs/flight-booking-system-0.0.1-SNAPSHOT.jar
WORKDIR /app
COPY ${JAR_FILE} app.jar
#COPY src/main/resources/db/changelog /app/db
ENTRYPOINT ["java","-jar","app.jar"]
