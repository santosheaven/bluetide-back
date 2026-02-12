Contributing to BlueTide Backend

Thanks for contributing! This document describes how to set up your development environment, run tests, and submit changes.

1) Development setup

- Java:
  - This project targets Java 21 for local development and testing. You can run Maven with the project-local helper without changing global JAVA_HOME:

  ```bash
  ./scripts/mvnw-java21 -v
  ```

- Maven:
  - Use the provided helper `./scripts/mvnw-java21` which sets JAVA_HOME to the project's Java 21 installation and runs Maven. This avoids changing system-wide environment variables.

- IDE:
  - IntelliJ IDEA or VS Code recommended. The project uses Lombok — enable annotation processing in your IDE.

2) Run the application

- Start (development)

```bash
./scripts/mvnw-java21 spring-boot:run
```

3) Tests

- Run unit tests:

```bash
./scripts/mvnw-java21 test
```

4) Code style

- Keep existing package structure and naming conventions.
- Use Lombok for simple DTOs and models where already present.

5) Pull requests

- Create a feature branch from `main` (or whichever main branch you use):

```bash
git checkout -b feature/my-feature
```

- Open a PR with a clear description and a short summary of changes and testing steps.

6) Troubleshooting

- If tests fail due to missing MongoDB during development, either start a local MongoDB on 27017 or mock repositories in tests.

7) Contact

- If you need help, leave an issue or ping the repo maintainers.

