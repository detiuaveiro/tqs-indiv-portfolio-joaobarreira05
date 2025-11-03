# Local SonarQube Setup

This project is wired to run code analysis against a local SonarQube server. Follow the steps below to spin up SonarQube, generate the coverage report, and push metrics from the Maven build.

## 1. Start SonarQube locally

The easiest way is via Docker (requires Docker Desktop):

```bash
docker run -d --name sonarqube \
  -p 9000:9000 \
  sonarqube:latest
```

> If Docker complains that a container named `sonarqube` already exists, reuse it or clean it up first:
>
> ```bash
> docker stop sonarqube || true
> docker rm sonarqube
> ```
>
> Once the old container is removed, rerun the `docker run` command above.

> First launch can take a couple of minutes while the plugins load.

Alternatively, download the [SonarQube Community Edition](https://www.sonarsource.com/products/sonarqube/downloads/) zip, unzip it, and run `bin/macosx-universal-64/sonar.sh start`.

Once the server is up, open http://localhost:9000 and log in with the default credentials `admin` / `admin`. You will be prompted to change the password.

## 2. Create an authentication token

1. In SonarQube, go to **My Account → Security → Tokens**.
2. Generate a new token (e.g., `garbage-collection-local`).
3. Export it in your shell so Maven can read it:

```bash
export SONAR_TOKEN="<paste-generated-token>"
```

(Optional) If you host SonarQube somewhere other than localhost, export `SONAR_HOST_URL` too; Maven will pick it up automatically and override the default `http://localhost:9000`.

```bash
export SONAR_HOST_URL="http://your-sonarqube-server:9000"
```

## 3. Run the analysis

Generate the coverage and test reports, then push the metrics to SonarQube. The `pom.xml` already contains the JaCoCo and Sonar configuration, so a single Maven invocation is enough:

```bash
./mvnw verify sonar:sonar -Dsonar.token="$SONAR_TOKEN"
```

What happens during the run:

- `verify` executes the unit, service, integration, and Playwright tests.
- The JaCoCo Maven plugin writes the XML report to `target/site/jacoco/jacoco.xml`.
- The Sonar Maven plugin publishes code smells, test results, and coverage to your local SonarQube server.

## 4. Inspect the results

Open http://localhost:9000, navigate to the newly created project (`pt.zeromonos:garbage-collection` by default), and review the quality gate, code coverage, and issue lists.

### Rerunning analyses quickly

If you make code changes and only need a fresh analysis:

1. Keep SonarQube running.
2. Run `./mvnw verify sonar:sonar -Dsonar.token="$SONAR_TOKEN"` again. The sonar plugin reuses the existing project.

### Cleanup

To stop the Docker container when you are done:

```bash
docker stop sonarqube && docker rm sonarqube
```

If you used the zip distribution, run `bin/macosx-universal-64/sonar.sh stop` instead.

---

**Troubleshooting**

- *Login fails:* Double-check that the token is active and was exported in the same shell session you are using to run Maven.
- *Coverage missing:* Ensure `./mvnw verify` runs without errors so the JaCoCo XML report is produced before Sonar starts.
- *Port conflicts:* Change the exposed port in the Docker command (`-p 9001:9000`) and export `SONAR_HOST_URL` accordingly.
