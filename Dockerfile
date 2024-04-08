FROM amazoncorretto:17-alpine

RUN apk add curl

WORKDIR /app

COPY ./target .

ENV QUARKUS_PROFILE="prod"
CMD java -Dquarkus.profile=$QUARKUS_PROFILE -jar quarkus-app/quarkus-run.jar


