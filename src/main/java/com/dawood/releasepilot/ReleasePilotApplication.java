package com.dawood.releasepilot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// @SpringBootApplication marks this as the starting point of the Spring Boot app.
//
// It tells Spring Boot:
// 1. This is the main application class.
// 2. Start component scanning from package com.dawood.releasepilot.
// 3. Auto-configure the application based on dependencies in pom.xml.
@SpringBootApplication
public class ReleasePilotApplication {

    public static void main(String[] args) {

        // This starts the Spring Boot application.
        // It also starts the embedded web server because we added spring-boot-starter-web.
        SpringApplication.run(ReleasePilotApplication.class, args);
    }
}
