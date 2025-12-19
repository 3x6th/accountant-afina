Локальный запуск:
1. Поднять у себя postgresql
2. Создать бд и пользователя с правами на работу с ней
3. Сделать в resources/application-local.yml
4. В application-local.yaml прописать настройки подключения к бд: spring.datasource.url, username, password
5. Запустить приложение с конфигурацией application-local

Эндпоинты:
http://localhost:8080/swagger-ui/index.html
http://localhost:8080/actuator/health