# Используем базовый образ с Java 21
FROM eclipse-temurin:21-jdk-alpine

# Метаданные образа
LABEL maintainer="arsp93@mail.ru"
LABEL version="1.0"
LABEL description="Accountant afina70 application"

# Создаем папку для приложения
WORKDIR /app

# Устанавливаем curl для healthcheck и bash для удобства
RUN apk add --no-cache curl bash

# Создаем папку для логов
RUN mkdir -p /app/logs

# Копируем наш JAR файл в контейнер
COPY target/accountant-0.0.1-SNAPSHOT.jar app.jar

# Создаем пользователя для безопасности
RUN adduser -D -u 1000 appuser && chown -R appuser:appuser /app
USER appuser

# Открываем порт (если приложение слушает на 8080)
EXPOSE 8080

# Запускаем приложение
CMD ["java", "-jar", "app.jar"]