package com.bookstore.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI bookstoreOpenAPI() {
        Contact contact = new Contact();
        contact.setName("Bookstore API Support");
        contact.setEmail("support@bookstore.com");
        contact.setUrl("https://github.com/UmaMaheshwari7474/bookstore-api");

        License mitLicense = new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");

        Info info = new Info()
                .title("RESTful Bookstore API")
                .version("1.0.0")
                .contact(contact)
                .description("Production-ready, industry-standard RESTful API for managing books and authors " +
                        "with Spring Boot 3, Spring Data JPA, PostgreSQL / H2, Bean Validation, and Swagger UI.")
                .license(mitLicense);

        Server localServer = new Server()
                .url("http://localhost:" + serverPort)
                .description("Local Development Server");

        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer));
    }
}
