# Continuous integration

GitHub Actions runs `.github/workflows/ci.yml` on every push and pull request.
You can also start it from Actions > Backend CI > Run workflow.

The workflow uses Temurin Java 17 and the checked-in Maven Wrapper. A temporary
MySQL 8.0 service supplies an isolated database for the Spring Boot context test.
Database settings are overridden with environment variables; CI credentials are
temporary values used only by this service and require no repository secrets.

`bash ./mvnw --batch-mode --no-transfer-progress verify` runs tests and packages
the application. Test reports are uploaded even after a test failure; successful
builds also upload the application JAR. Artifacts are retained for seven days.
This workflow does not deploy.

Commit and push this file and `.github/workflows/ci.yml` in the backend repository
to enable CI. To require CI before merging, configure the `Test and package` check
in the repository's branch protection rules.
