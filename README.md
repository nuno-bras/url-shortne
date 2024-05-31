# url-shortner-api

This project uses Quarkus, the Supersonic Subatomic Java Framework.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw spotless:apply quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

> **_NOTE:_**  The UI is available at http://loclahost:8080/ui

## Packaging the application

The application can be packaged using:

```shell script
./mvnw package
```

## Running the application stack

The application stack can be deployed using docker. The application is exposed on port 80

```shell script
docker compose up -d
```