# Задание 2: работа с REST

Для каждой сущности доступны CRUD операции.


| Ресурс | Путь | Атрибуты |
|---|---|---|
| Животное | `/api/animals` | кличка, вид, возраст, статус размещения |
| Вольер | `/api/enclosures` | допустимые виды животных, вместимость |
| Заявка на усыновление | `/api/adoption-applications`  | животное, заявитель, статус, дата подачи |
| Акт передачи | `/api/animal-handover-records`| заявка, дата подготовки, дата подтверждения, статус |
| Пользователь | `/api/users` | логин, имя, адрес электронной почты |

## Разделение по слоям

| Слой | Содерджимое и ответственность |
| --- | --- |
| api | API контроллеры, принимают запрос, разбирают тело, проверяют его аннотациями |
| service | бизнес-логика, проверяют существование связанных объектов, ведут переходы статусов, закрепляют и освобождают животное, назначают даты |
| repository | доступ к данным |
| domain | сущности |
| exception | `NotFoundException`, `ConflictException` и `ApiExceptionHandler`, который превращает исключения в единый формат ответа |

## Хранилище

Используется БД SQLite, доступ через Spring Data JPA.

| Эелмент | Расположение |
|---|---|
| База | `shelter.db` в рабочем каталоге, создаётся автоматически |
| Схема таблиц | `src/main/resources/schema.sql` |
| Связи | внешние ключи: заявка ссылается на животное и заявителя, акт - на заявку |

## Документация API

Swagger UI: <http://localhost:8080/swagger-ui.html>

Чтобы обновить файлы после изменений в контроллерах:

```bash
curl -s http://localhost:8080/v3/api-docs | python3 -m json.tool --indent 2 --no-ensure-ascii > openapi.json
curl -s http://localhost:8080/v3/api-docs.yaml > openapi.yaml
```

Примеры запросов:

```bash
curl -i -X POST http://localhost:8080/api/animals -H 'Content-Type: application/json' -d '{"name":"Бобик","species":"Собака","age":3}'
```

```bash
curl http://localhost:8080/api/animals
```

```bash
curl -X PATCH http://localhost:8080/api/animals/1 -H 'Content-Type: application/json' -d '{"age":4}'
```

```bash
curl -X POST http://localhost:8080/api/users -H 'Content-Type: application/json' -d '{"username":"ivan","fullName":"Иван Петров","email":"ivan@example.com"}'
curl -X POST http://localhost:8080/api/adoption-applications -H 'Content-Type: application/json' -d '{"animalId":1,"applicantId":1}'
curl http://localhost:8080/api/animals/1
```

```bash
curl -i -X DELETE http://localhost:8080/api/animals/1
```

```bash
curl -i -X DELETE http://localhost:8080/api/adoption-applications/1
curl -i -X DELETE http://localhost:8080/api/animals/1
```

## Сборка и запуск

```bash
mvn clean package
java -jar target/lab2-0.0.1-SNAPSHOT.jar
```

```bash
mvn spring-boot:run
```
