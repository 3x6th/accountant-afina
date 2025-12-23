Настроен распределенный на 2 инстанса hazelcast rate limiter 100 запросов за 60 сек, также настроен балансировщик nginx.

Запуск в Docker контейнере:
1. Установить Docker Desktop 4.8.2+ или Docker Compose 1.27.0+ и Docker Engine 19.03.0+
2. В корневой папке проекта выполнить команды: docker-compose up -d --build
3. Проверить командой docker-compose ps, что accountant-app-1, accountant-app-2, accountant-postgres  и accountant-nginx в состоянии Up (healthy)

Эндпоинты:

http://localhost/actuator/health

http://localhost/swagger-ui/index.html#/

http://localhost/api/demo/check-request