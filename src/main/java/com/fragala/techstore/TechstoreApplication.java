package com.fragala.techstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Techstore Spring Boot application.
 *
 * <p>This class represents the bootstrap class that starts the entire application. It exists
 * because Spring Boot needs a well-known starting point to initialize the Spring container,
 * scan components, configure infrastructure beans, and launch the embedded web server.
 *
 * <p>Architecturally, this class sits at the very edge of the application. It does not contain
 * business logic; its responsibility is only to hand control over to Spring Boot so the rest of
 * the layers, such as services, repositories, and entities, can be wired together.
 *
 * <p>It is used when the application starts, whether that happens from an IDE, from the command
 * line, or during deployment.
 */
// `@SpringBootApplication` is a convenience annotation that combines configuration,
// component scanning, and auto-configuration. It is used here so Spring Boot can discover
// the project's beans and set up the application with minimal manual configuration.
@SpringBootApplication
public class TechstoreApplication {

	/**
	 * Starts the Spring Boot application.
	 *
	 * @param args command-line arguments passed at application startup
	 */
	public static void main(String[] args) {
		SpringApplication.run(TechstoreApplication.class, args);
	}

}
