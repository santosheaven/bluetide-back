# bluetide-back
backend blue tide property manager

## Using Java 21 for this project

This repository includes a small helper script and Maven wrapper configured to run the build with Java 21 without changing your global environment variables.

- Use the provided script to run Maven with the project's Java 21 installation:

```bash
./scripts/mvnw-java21 -v
./scripts/mvnw-java21 -DskipTests package
```

- I added the Maven wrapper JAR (`.mvn/wrapper/maven-wrapper.jar`) and helper scripts so `./scripts/mvnw-java21` works out-of-the-box and will download the right Maven distribution locally.

Notes:
- The script runs Java from `/opt/homebrew/opt/openjdk@21` (Homebrew) when available.
- The project has been migrated to Java 21 (pom property `java.version=21`) and most tests were updated to run in the WebMvc test slices without requiring a running MongoDB instance (security filters disabled for controller tests where appropriate).

If you'd like, I can also add a dedicated `mvnw-java21` wrapper script at the repo root or convert the helper into `./mvnw` behavior — tell me which you prefer.

---

Documentation

- API requests and examples: `API_REQUESTS.md`
- VSCode REST Client requests: `requests/bluetide.http`
- Architecture overview: `docs/ARCHITECTURE.md`
- Contributing guide: `CONTRIBUTING.md`

Run the full test suite:

```bash
./scripts/mvnw-java21 -U test
```
