package ru.mtuci.rbpo.lab2;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(
        title = "Приют и передача животных в семью",
        version = "1.0.0",
        description = "REST API приюта: животные, вольеры, заявки на усыновление, акты передачи, пользователи. "
                + "Даты и время проставляет сервер, в запросах не передаются."))
public class AnimalShelterApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnimalShelterApplication.class, args);
    }
}
