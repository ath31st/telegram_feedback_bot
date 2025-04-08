FROM eclipse-temurin:17-jdk-alpine AS base

LABEL version="1.0.0"
LABEL description="Telegram feedback bot"

FROM base AS builder
WORKDIR /app
COPY . .
RUN chmod +x ./gradlew && ./gradlew build -x test

FROM eclipse-temurin:17-jre-alpine AS runner
RUN apk --no-cache add dumb-init \
    && mkdir /app \
    && addgroup --system javauser \
    && adduser -S -s /bin/false -G javauser javauser

COPY --from=builder /app/build/libs/feedback_bot-*-standalone.jar /app/feedback_bot.jar
WORKDIR /app
RUN chown -R javauser:javauser /app
USER javauser
ENTRYPOINT ["dumb-init", "java", "-jar", "feedback_bot.jar"]
