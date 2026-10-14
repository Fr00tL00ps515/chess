FROM node:24-alpine AS client
WORKDIR /client
COPY client/package.json client/package-lock.json ./
RUN npm ci
COPY client/ ./
RUN npm run build

FROM eclipse-temurin:17-jdk AS server
WORKDIR /server
COPY server/.mvn/ .mvn/
COPY server/mvnw server/pom.xml ./
RUN ./mvnw -B -ntp dependency:go-offline
COPY server/src/ src/
COPY --from=client /client/dist/ src/main/resources/static/
RUN ./mvnw -B -ntp package -DskipTests

FROM eclipse-temurin:17-jre
RUN useradd --system chess
WORKDIR /app
COPY --from=server /server/target/chess-server-*.jar app.jar
USER chess
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
