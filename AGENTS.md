# Project Overview: embabeltests

This is a Spring Boot application designed to demonstrate and explore testing strategies for **Embabel agents**. Embabel is a framework for building AI-powered agents, and this project serves as a testbed for verifying their behavior.

## Technologies
- **Java 25**
- **Spring Boot 3.5.10**
- **Maven** (with Wrapper)
- **Embabel Agent Framework** (v0.3.4-SNAPSHOT)
- **Ollama** (as an LLM provider)
- **JUnit 5** (for testing)

## Architecture
The project follows a standard Spring Boot layout:
- `src/main/java`: Contains the application entry point and agent definitions.
- `src/main/resources`: Configuration files, including `application.properties`.
- `src/test/java`: Contains unit and integration tests for Embabel agents.

## Building and Running

### Prerequisites
- Java 25 installed.
- Ollama running locally (if using Ollama-based agents).

### Commands
- **Build the project:**
  ```bash
  ./mvnw clean compile
  ```
- **Run the application:**
  ```bash
  ./mvnw spring-boot:run
  ```
- **Run tests:**
  ```bash
  ./mvnw test
  ```

## Development Conventions
- **Testing:** New agents should be accompanied by tests in `src/test/java`. Use `embabel-agent-test` utilities for agent-specific assertions.
- **Configuration:** Model settings and agent configurations are managed in `src/main/resources/application.properties`.
- **Naming:** Follow standard Java and Spring Boot naming conventions.
