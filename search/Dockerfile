FROM eclipse-temurin:22-jre

RUN mkdir -p /opt/app
COPY build/libs/search.jar /opt/app
CMD ["java", "-jar", "/opt/app/search.jar"]
++