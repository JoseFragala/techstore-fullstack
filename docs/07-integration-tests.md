# PostgreSQL integration tests

The integration tests use a dedicated PostgreSQL 17 instance on port 55434,
with database `techstore_test`. They do not use the development database.
No additional Java dependency is required.

From the repository root on Windows:

```powershell
docker compose -f docker-compose.test.yml up -d --wait
.\mvnw.cmd "-Dtest=AddressServiceIntegrationTest" test
.\mvnw.cmd "-Dtest=UserServiceIntegrationTest" test
.\mvnw.cmd "-Dspring.profiles.active=test" test
```

The explicit profile on the full-suite command also routes existing Spring
context tests to the test database. Unit tests continue to use mocks.

`AddressServiceIntegrationTest` commits its fixture with `TransactionTemplate`,
then invokes the Spring-managed service outside a test transaction. A missing
street triggers a persistence failure after the previous default was flushed.
A new transaction reloads the original address and verifies rollback and the
absence of the new address. A second scenario verifies successful replacement.
The test removes only its own addresses and user; system roles are preserved.
Do not annotate this test class with `@Transactional`: an enclosing test
transaction could hide a missing transaction on the service.

`UserServiceIntegrationTest` calls the real user service and reloads the user
after committed creation and password changes. It checks that the stored values
are hashes, that the new password matches, and that the previous password stops
matching after a change. It removes only its own test user afterward.

The database uses temporary container storage. Its contents disappear when the
container stops. Stop it after testing with:

```powershell
docker compose -f docker-compose.test.yml stop postgres-test
```

Do not use `--remove-orphans`: the development container can share the Compose
project and must be preserved.
