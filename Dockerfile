FROM eclipse-temurin:21-jre

RUN mkdir -p /opt/app
COPY build/libs/history.jar /opt/app
CMD ["java", "-jar", "/opt/app/history.jar"]