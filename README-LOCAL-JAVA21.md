Using Java 21 for this project only

This repository has been migrated to Java 21 / Spring Boot 3.x but you can keep your system Java (e.g., Java 8) unchanged.

What we added
- `scripts/mvnw-java21` — a small helper script that sets JAVA_HOME to common Java 21 install locations (Homebrew or /Library/Java) for the lifetime of the maven process and then runs the project's `mvnw` (if present) or your system `mvn`.
- `.mvn/jvm.config` — placeholder file (project-level JVM config). You can add JVM options here.

Quick commands
- Build using Java 21 (project-local):
  ```bash
  ./scripts/mvnw-java21 clean package
  ```

- Run with tests:
  ```bash
  ./scripts/mvnw-java21 test
  ```

Notes
- The script looks for Java 21 in common locations (Homebrew paths). If you installed Java 21 via Homebrew, the script should find it automatically.
- If you don't have Java 21 installed, the script will fall back to your system Java and warn.
- If you prefer, install `openjdk@21` via Homebrew:
  ```bash
  brew install openjdk@21
  ```

If you want, I can add a small GitHub Actions workflow to run CI with Java 21 as well.

