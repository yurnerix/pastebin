# Pastebin

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-18-blue)
![Redis](https://img.shields.io/badge/Redis-7-red)
![Docker](https://img.shields.io/badge/Docker-Compose-blue)
![Maven](https://img.shields.io/badge/Maven-Build-orange)

**Pastebin** — pet-проект на Java Spring Boot для создания текстовых заметок, хранения их в базе данных и обмена ими по короткой ссылке.

Пользователь может создать текстовый блок, выбрать срок его жизни, сделать его публичным или приватным, а затем поделиться ссылкой с другим человеком.

---

## Содержание

* [О проекте](#о-проекте)
* [Возможности](#возможности)
* [Технологии](#технологии)
* [Архитектура проекта](#архитектура-проекта)
* [Основные страницы](#основные-страницы)
* [REST API](#rest-api)
* [База данных](#база-данных)
* [Redis](#redis)
* [Запуск проекта](#запуск-проекта)
* [Проверка сервисов](#проверка-сервисов)
* [Пример сценария использования](#пример-сценария-использования)
* [Статус проекта](#статус-проекта)

---

## О проекте

Pastebin работает по простой логике:

1. Пользователь создаёт текстовый блок.
2. Приложение генерирует уникальный короткий `hash`.
3. Paste сохраняется в PostgreSQL.
4. Пользователь получает короткую ссылку.
5. Другой пользователь может открыть ссылку и увидеть сохранённый текст.

Пример ссылки:

```text
http://localhost:8081/p/{hash}
```

Проект реализован как полноценное Spring Boot приложение с HTML-интерфейсом, REST API, авторизацией, миграциями базы данных, Redis rate limiting и Docker-инфраструктурой.

---

## Возможности

* Создание paste с заголовком и текстом
* Генерация короткой уникальной ссылки
* Просмотр paste по `hash`
* Выбор срока жизни paste:

  * 10 минут
  * 1 час
  * 1 день
  * 1 неделя
  * без срока истечения
* Проверка истечения срока действия paste
* Счётчик просмотров
* Регистрация пользователей
* Авторизация пользователей
* Выход из аккаунта
* Привязка paste к авторизованному пользователю
* Страница **«Мои paste»**
* Удаление конкретного paste
* Удаление всех истёкших paste
* Приватные paste
* REST API для работы через Postman
* JWT-аутентификация для REST API
* HTML-интерфейс на Thymeleaf
* Rate limiting через Redis
* Миграции базы данных через Flyway
* PostgreSQL и Redis через Docker Compose
* Swagger UI для просмотра REST API

---

## Технологии

| Категория          | Технологии                                   |
| ------------------ | -------------------------------------------- |
| Backend            | Java 17, Spring Boot, Spring Web, Spring MVC |
| Database           | PostgreSQL, Spring Data JPA, Hibernate       |
| Security           | Spring Security, JWT                         |
| Validation         | Spring Validation                            |
| Migrations         | Flyway                                       |
| Cache / Rate Limit | Redis                                        |
| Frontend           | Thymeleaf, HTML, CSS                         |
| Documentation      | Springdoc OpenAPI, Swagger UI                |
| Infrastructure     | Docker, Docker Compose                       |
| Build Tool         | Maven                                        |

---

## Архитектура проекта

Проект построен по слоистой архитектуре.

```text
src/main/java/by/yurnerix/pastebin
├── config
├── controller
│   └── web
├── dto
│   ├── request
│   ├── response
│   └── type
├── entity
├── exception
├── repository
├── security
├── service
├── util
└── PastebinApplication.java
```

### Основные слои

| Слой             | Назначение                                |
| ---------------- | ----------------------------------------- |
| `controller`     | REST-контроллеры                          |
| `controller/web` | Контроллеры для HTML-страниц              |
| `service`        | Бизнес-логика приложения                  |
| `repository`     | Работа с базой данных                     |
| `entity`         | JPA-сущности                              |
| `dto`            | Request/response объекты                  |
| `security`       | JWT и Spring Security                     |
| `exception`      | Кастомные исключения и обработчики ошибок |
| `util`           | Вспомогательные классы                    |

---

## Основные страницы

| Страница      | Описание         |
| ------------- | ---------------- |
| `/`           | Главная страница |
| `/register`   | Регистрация      |
| `/login`      | Вход             |
| `/logout`     | Выход            |
| `/pastes/new` | Создание paste   |
| `/me/pastes`  | Мои paste        |
| `/p/{hash}`   | Просмотр paste   |

---

## REST API

### Auth API

```http
POST /api/v1/auth/register
POST /api/v1/auth/login
```

### Paste API

```http
POST /api/v1/pastes
GET  /api/v1/pastes/{hash}
GET  /api/v1/pastes/my
```

### Swagger UI

После запуска приложения Swagger доступен по адресу:

```text
http://localhost:8081/swagger-ui.html
```

---

## База данных

В проекте используются две основные таблицы:

```text
users
pastes
```

Также Flyway создаёт служебную таблицу:

```text
flyway_schema_history
```

### Таблица `users`

| Поле         | Описание                   |
| ------------ | -------------------------- |
| `id`         | Идентификатор пользователя |
| `username`   | Имя пользователя           |
| `email`      | Email пользователя         |
| `password`   | Захешированный пароль      |
| `role`       | Роль пользователя          |
| `created_at` | Дата создания              |
| `updated_at` | Дата обновления            |

### Таблица `pastes`

| Поле          | Описание                      |
| ------------- | ----------------------------- |
| `id`          | Идентификатор paste           |
| `hash`        | Уникальный короткий hash      |
| `title`       | Заголовок                     |
| `content`     | Текст paste                   |
| `is_private`  | Приватность paste             |
| `user_id`     | Автор paste                   |
| `views_count` | Количество просмотров         |
| `created_at`  | Дата создания                 |
| `expires_at`  | Дата истечения срока действия |

---

## Redis

Redis используется для ограничения частоты создания paste.

Пример ограничения:

```text
Не больше 10 paste за определённый промежуток времени
```

При создании paste приложение создаёт временный ключ в Redis.

Для неавторизованного пользователя:

```text
rate_limit:create_paste:ip:127.0.0.1
```

Для авторизованного пользователя:

```text
rate_limit:create_paste:user:user@mail.com
```

Redis помогает защитить приложение от спама и слишком большого количества запросов.

---

## Запуск проекта

### Требования

Для запуска проекта понадобятся:

* Java 17+
* Maven
* Docker
* Docker Compose
* IntelliJ IDEA или другая IDE

---

### 1. Клонировать репозиторий

```bash
git clone https://github.com/your-username/pastebin.git
cd pastebin
```

---

### 2. Запустить PostgreSQL и Redis

```bash
docker compose up -d postgres redis
```

Проверить контейнеры:

```bash
docker ps
```

Ожидаемые контейнеры:

```text
pastebin-postgres
pastebin-redis
```

---

### 3. Проверить настройки приложения

Пример настроек в `application.properties`:

```properties
spring.application.name=pastebin

server.port=${SERVER_PORT:8081}

spring.datasource.url=${SPRING_DATASOURCE_URL:jdbc:postgresql://localhost:5433/pastebin_db}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME:postgres}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD:asterisk}
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.open-in-view=false

spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration

spring.data.redis.host=${SPRING_DATA_REDIS_HOST:localhost}
spring.data.redis.port=${SPRING_DATA_REDIS_PORT:6379}
spring.data.redis.database=${SPRING_DATA_REDIS_DATABASE:0}

spring.thymeleaf.cache=false

springdoc.swagger-ui.path=/swagger-ui.html

app.jwt.secret=${APP_JWT_SECRET:PastebinSecretKeyForJwtTokenGenerationPastebinSecretKey123456}
app.jwt.expiration-ms=${APP_JWT_EXPIRATION_MS:86400000}
```

---

### 4. Запустить приложение

Запустить главный класс:

```text
PastebinApplication
```

Или через Maven:

```bash
mvn spring-boot:run
```

---

### 5. Открыть приложение

```text
http://localhost:8081
```

---

## Проверка сервисов

### PostgreSQL

Подключиться к PostgreSQL внутри Docker:

```bash
docker exec -it pastebin-postgres psql -U postgres -d pastebin_db
```

Показать таблицы:

```sql
\dt
```

Ожидаемые таблицы:

```text
flyway_schema_history
users
pastes
```

Выйти из `psql`:

```sql
\q
```

---

### Redis

Подключиться к Redis:

```bash
docker exec -it pastebin-redis redis-cli
```

Проверить подключение:

```bash
ping
```

Ожидаемый ответ:

```text
PONG
```

Проверить ключи:

```bash
keys *
```

Пример rate limit ключа:

```text
rate_limit:create_paste:ip:127.0.0.1
```

Выйти из Redis CLI:

```bash
exit
```

---

## Docker Compose

### Запустить инфраструктуру

```bash
docker compose up -d postgres redis
```

### Остановить контейнеры

```bash
docker compose down
```

### Остановить контейнеры и удалить volumes

```bash
docker compose down -v
```

Команда с `-v` удалит данные PostgreSQL и Redis, которые хранятся в Docker volumes.

---

## Настройки подключения

### PostgreSQL

| Параметр | Значение      |
| -------- | ------------- |
| Host     | `localhost`   |
| Port     | `5433`        |
| Database | `pastebin_db` |
| Username | `postgres`    |
| Password | `asterisk`    |

### Redis

| Параметр | Значение    |
| -------- | ----------- |
| Host     | `localhost` |
| Port     | `6379`      |
| Database | `0`         |

---

## Пример сценария использования

1. Пользователь открывает главную страницу.
2. Переходит на страницу регистрации.
3. Создаёт аккаунт.
4. Выполняет вход.
5. Создаёт paste.
6. Получает короткую ссылку.
7. Открывает paste по ссылке.
8. Переходит на страницу **«Мои paste»**.
9. Просматривает список своих paste.
10. Удаляет ненужные или истёкшие paste.

---

## Для чего нужен этот проект

Этот проект демонстрирует навыки разработки backend-приложения на Java и Spring Boot:

* создание REST API
* работа с PostgreSQL через Spring Data JPA
* использование Flyway для миграций
* реализация регистрации и авторизации
* использование Spring Security
* работа с JWT
* создание HTML-интерфейса через Thymeleaf
* подключение Redis
* реализация rate limiting
* обработка ошибок
* Docker-инфраструктура
* работа с Maven

Проект подходит для портфолио начинающего Java-разработчика и может быть использован как pet-проект для демонстрации навыков на собеседовании.

---

## Статус проекта

Проект находится в стадии разработки.

### Реализовано

* базовая логика Pastebin
* регистрация и вход
* создание и просмотр paste
* страница пользователя
* удаление paste
* приватные paste
* PostgreSQL
* Redis
* Docker Compose
* REST API
* HTML UI
* Swagger UI

### Возможные будущие улучшения

* тесты
* кэширование популярных paste
* полноценная админ-панель
* улучшение дизайна интерфейса
* деплой приложения
* CI/CD

---

## Автор

Проект разработан как pet-проект для практики Java Backend разработки.
