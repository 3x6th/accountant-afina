Локальный запуск:
1. Поднять у себя postgresql
2. Создать бд и пользователя с правами на работу с ней
3. Сделать в resources/application-local.yml
4. В application-local.yaml прописать настройки подключения к бд: spring.datasource.url, username, password
5. Запустить приложение с конфигурацией application-local

Запуск в Docker контейнере:
1. Установить Docker Desktop 4.8.2 или Docker Compose 1.27.0+ и Docker Engine 19.03.0+
2. В корневой папке проекта выполнить команды: docker-compose up -d --build
3. Проверить командой docker-compose ps, что accountant-app и accountant-postgres в состоянии Up (healthy)

Эндпоинты:

http://localhost:8080/swagger-ui/index.html

http://localhost:8080/actuator/health