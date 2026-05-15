# syntax=docker/dockerfile:1.6

# ─────────────────────────────────────────────────────────────────────────────
# Stage 1: Tailwind CSS 빌드
# ─────────────────────────────────────────────────────────────────────────────
FROM node:20-alpine AS css
WORKDIR /app

COPY package.json package-lock.json ./
RUN npm ci

COPY tailwind.config.js ./
COPY src ./src

RUN npx tailwindcss \
        -i ./src/styles/app.css \
        -o ./src/main/resources/static/css/app.css \
        --minify

# ─────────────────────────────────────────────────────────────────────────────
# Stage 2: Spring Boot 빌드 (Gradle bootJar)
# ─────────────────────────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle settings.gradle ./
RUN chmod +x gradlew && ./gradlew --version --no-daemon

COPY src ./src
COPY --from=css /app/src/main/resources/static/css/app.css \
                ./src/main/resources/static/css/app.css

RUN ./gradlew clean bootJar -x test --no-daemon

# ─────────────────────────────────────────────────────────────────────────────
# Stage 3: JRE 21 런타임
# ─────────────────────────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/build/libs/camp-swing-outdoor-1.0.0.jar app.jar

EXPOSE 8080
ENTRYPOINT ["sh","-c","exec java -jar /app/app.jar --server.port=${PORT:-8080} --spring.profiles.active=${SPRING_PROFILES_ACTIVE:-prod}"]
