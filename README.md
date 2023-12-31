### Project stack

- Spring Boot 3.2.1, with Web and JPA
- H2 as database
- REST API, exposed on port 8080

### Building the project

`./gradlew build`

### Running the project

`./gradlew run`

Or after building `java -jar build/libs/ubnt-task-1.0.jar`

An empty database file (database.h2.mv.db) will be created on the first run. To run the project using the provided sample DB run
```
export DATABASE_FILE=sample_db.h2
./gradlew run
```

### Running tests

`./gradlew test`

### API documentation
[Available here](https://documenter.getpostman.com/view/14339659/2s9YsDkaK9)