# Задание 1: подготовка репозитория

Проект Spring Boot на Maven, содержит  два GET-эндпоинта.

## Сборка и запуск

```bash
mvn clean package
```

Запуск из .jar:

```bash
java -jar target/lab1-0.0.1-SNAPSHOT.jar
```

Или запуск без предварительной упаковки:

```bash
mvn spring-boot:run
```

## Endpoints

#### Константный тестовый ответ

```bash
curl -i http://localhost:8080/api/text
```
```text
Hello, world!
```

#### Сумма от 1 до N

```bash
curl -i http://localhost:8080/api/sum/9
```

```java
// 1 + 2 + ... + 9 = 45
45 
```
