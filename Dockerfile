FROM amazoncorretto:17-alpine3.22-jdk

RUN addgroup -S app && adduser -S app -G app

WORKDIR /home/app

COPY build/libs/*.jar app.jar

RUN chown -R app:app /home/app

USER app

ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
