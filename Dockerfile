FROM amazoncorretto:17-alpine AS builder
WORKDIR /app

COPY gradlew .
COPY gradle ./gradle

COPY build.gradle settings.gradle gradlew ./
RUN chmod +x ./gradlew

RUN ./gradlew dependencies --no-daemon

COPY src ./src
RUN ./gradlew clean build -x test --no-daemon

FROM bellsoft/liberica-openjre-alpine-musl:17
WORKDIR /app

ENV AWS_S3_BUCKET="" \
    AWS_ACCESS_KEY="" \
    AWS_SECRET_ACCESS_KEY="" \
    AWS_REGION=""

ENV JVM_OPTS="" \
    SPRING_PROFILES_ACTIVE=""

COPY --from=builder /app/build/libs/sb01-deokhugam-team07-0.0.1-SNAPSHOT.jar app.jar

RUN chmod 755 app.jar

EXPOSE 80
ENTRYPOINT ["sh", "-c", "exec java -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} -Duser.timezone=Asia/Seoul $JVM_OPTS -jar app.jar"]
