# Coding Maven Project

This directory contains the Java desktop application for the Personal Finance Tracker.

## Project Structure (Prompt 2)

- Follows standard Maven layout:
  - `src/main/java` for source code
  - `src/main/resources` for resources
  - `src/test/java` for tests
- Uses Java 21 and Maven 3.9.9
- Dependencies: Gson (JSON persistence), JavaFX (UI)

## Prompt Annotation System
- All code from the initial requirements and core logic is annotated as `Prompt 1`.
- All persistence and Maven-related code is annotated as `Prompt 2`.
- Future changes will be annotated as `Prompt 3`, `Prompt 4`, etc.

## How to Build and Run
- Use `mvn clean install` to build.
- Use `mvn javafx:run` to launch the desktop application (JavaFX entry point will be provided in future prompts). 