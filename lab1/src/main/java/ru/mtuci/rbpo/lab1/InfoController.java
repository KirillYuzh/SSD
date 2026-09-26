package ru.mtuci.rbpo.lab1;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InfoController {

    private final String greetingText;
    private final String applicationName;

    public InfoController(
            @Value("${greeting.text}") String greetingText,
            @Value("${spring.application.name}") String applicationName) {
        this.greetingText = greetingText;
        this.applicationName = applicationName;
    }

    @GetMapping("/api/text")
    public String text() {
        return greetingText;
    }

    @GetMapping("/api/sum/{count}")
    public long sum(@PathVariable int count) {
        return (long) count * (count + 1) / 2;
    }
}
