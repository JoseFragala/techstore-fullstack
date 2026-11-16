# Password Storage and Security Plan

## Implemented password storage

UserService hashes raw passwords before assigning them to User during creation
and password changes. The entity receives an encoded value; it does not depend
on cryptographic infrastructure. UserResponse does not expose passwords or hashes.
The service-level changePassword method now accepts a raw password, not a hash.
It is not exposed as an HTTP endpoint.

PasswordConfig provides a PasswordEncoder through spring-security-crypto, whose
version is managed by Spring Boot. It uses PBKDF2 with the named Spring Security
5.8 parameter set and a random salt. DelegatingPasswordEncoder stores the prefix
`{pbkdf2@SpringSecurity_v5_8}` alongside the salt and derived value. The prefix
identifies the parameter set for future verification and controlled upgrades.
Only this encoding is accepted; there is no plaintext fallback.

The crypto module is needed to use a maintained password-hashing implementation
rather than writing cryptographic code. The subsequent session foundation adds
Spring Security and Spring Session JDBC; see the current implementation below.
Hashing is a one-way derivation,
not reversible encryption. In future authentication code, use matches(raw, hash),
not equality between two fresh hashes: salts make those hashes different.

Existing database passwords are not rewritten by this change. On October 19,
2026, the owner confirmed that all current accounts are disposable test data.
They can be recreated through the new hashed-password workflow when preparing
the authentication environment; no legacy password fallback is required.
This planning decision has not deleted or changed any database records.
Work-factor benchmarking, password policy, reset flows, and transport protection
belong to the deployment/authentication scope.

## Role assignment: approved policy, enforcement pending

Public creation no longer accepts roleId in its DTO; UserService resolves CUSTOMER
by name. Unknown JSON properties are ignored by the current Jackson configuration,
so a submitted roleId has no effect. The update DTO still contains roleId but its
HTTP endpoint is blocked until the owner/admin authorization slice is completed.

The owner approved the following policy on October 18, 2026:

- Public registration always assigns CUSTOMER on the server.
- Only an authenticated ADMIN can assign or change roles, including SELLER.
- Public registration and profile update DTOs do not accept roleId.
- Administrative role assignment uses a separate protected operation.

Creation enforces the default role. Administrative assignment and owner profile
updates remain pending and are blocked by the current security filter chain.

## Initial administrator: approved design, implementation pending

Approved on October 19, 2026:

- Initial ADMIN creation requires an explicitly enabled bootstrap configuration.
- Bootstrap user data and credentials come from external configuration, with no
  default password in source code and no public ADMIN registration endpoint.
- The password is hashed using the configured PasswordEncoder.
- Restarting the application must not reset a password or silently promote an
  existing account. Bootstrap is disabled after provisioning.

The existing RoleDataInitializer creates role definitions, not an administrator
account. Bootstrap failure handling and external property names will be specified
before implementation.

## User and address permissions: approved, enforcement pending

Approved on October 19, 2026:

| Operation | Allowed caller |
| --- | --- |
| Register a CUSTOMER account | Public |
| Read or update own profile | Authenticated account owner |
| Change own password | Authenticated account owner, confirming current password |
| Manage own addresses | Authenticated account owner |
| List users or read another user's profile | Authenticated ADMIN |
| Activate/deactivate accounts or change roles | Authenticated ADMIN |

SELLER has no administrative authority over users. Catalog and sales permissions
remain to be defined. Ownership checks must compare the authenticated identity
with the resource owner; receiving a userId or addressId is not authorization.
The table does not grant ADMIN blanket access to modify other users' profiles,
passwords, or addresses. Account recovery is a separate pending workflow.

## Authentication lifetime: approved behavior, implementation pending

Approved on October 19, 2026:

| Rule | CUSTOMER | ADMIN and SELLER |
| --- | --- | --- |
| Remain signed in after closing the browser | Yes | No |
| Inactivity expiration | 30 days | 15 minutes |
| Absolute lifetime since login | 90 days | 8 hours |
| Silent renewal within both limits | Yes | Yes |
| After either limit is reached | Login required | Login required |

Activity and silent renewal must not extend the absolute lifetime. Privileged
access should remain usable without asking for credentials on every action.
Changing a password requires confirmation of the current password. Other
sensitive operations requiring reauthentication must be explicitly identified
before implementation; they are not automatically implied by this decision.

Browser persistence is a target behavior, not a chosen session/token mechanism.
The implementation must define how a new browser session is recognized, including
browser session restoration behavior. Authentication state is not browser cache.

Validation must cover inactivity and absolute boundaries, renewal within limits,
role-specific persistence, and rejection of an incorrect current password, using
a controlled clock rather than sleeps.

## Login and access revocation: approved, implementation pending

Approved on October 19, 2026:

- Login uses email and password.
- Logout invalidates the current authenticated session/access credential, leaving
  independent logins on other devices or browsers unaffected.
- A successful password change invalidates all existing authenticated accesses,
  including the current one. Login with the new password is required.
- Account deactivation blocks new logins and existing authenticated access from
  the next request after the change is committed.
- A role change invalidates all existing authenticated accesses. A new login is
  required to establish access with the updated permissions.

Validation must cover independent logins, current-access logout, global
invalidation after password/role changes, and the first protected request after
deactivation. Invalidated credentials must not become valid again merely because
an account is reactivated or a role is changed back.

These rules require a mechanism that can reject previously issued credentials;
expiration alone does not meet the immediate invalidation requirements.

## Server-side sessions: selected design, implementation pending

Selected by the owner on October 19, 2026. The frontend remains a separate
project and communicates with the backend over HTTP. JWT is deferred from this
implementation scope.

Use Spring Security for authentication/authorization and Spring Session JDBC
for PostgreSQL-backed HTTP sessions. Reuse PostgreSQL rather than introduce a
second data store. The initial foundation now includes these components.
The existing PasswordEncoder remains the credential verification mechanism.

The browser receives only an opaque session identifier in an HttpOnly cookie.
The server associates the session with a stable user ID, authentication time,
and the data required to enforce the approved lifetime and revocation rules.
Do not store a JPA User entity, raw password, or password hash in browser cookies
or application session attributes. Rotate the session identifier after login.

CUSTOMER requires both server persistence and a persistent browser cookie.
Neither cookie expiration nor server timeout alone implements all lifetime rules:
enforce inactivity and absolute lifetime on the server, and bound cookie lifetime
by the remaining absolute lifetime. Background renewal must not indefinitely
count as meaningful user activity; the activity definition remains to be settled.

ADMIN/SELLER use non-persistent browser cookies. Browser session restoration can
retain these cookies, so closing a browser is not a reliable revocation event.
Document this limitation rather than claim detection of every browser restart.
Server-enforced inactivity and absolute limits remain mandatory; explicit logout
invalidates the session immediately.

Cookie configuration must use HTTPS/Secure in deployment, HttpOnly, an appropriate
SameSite policy, and narrow host/path scope. Use an explicit local HTTP development
configuration. Frontend requests include credentials; CORS permits only configured
frontend origins and credentialed requests. Prefer a same-site deployment, with
cross-site hosting requiring a separate cookie/browser compatibility review.

Keep CSRF protection enabled, including login and logout. A separate CSRF token
can be exposed for the frontend to send in a header; the session cookie itself
must remain inaccessible to JavaScript. CSRF tokens are not JWT login credentials.

Global invalidation requires persistent session lookup or a persistent account
authentication version checked on protected requests. Select the exact mechanism
and concurrency handling during implementation; do not rely only on cached role
or active flags. Revocation must remain effective after reactivation and restart.

### Ordered implementation slices

| Slice | Expected files/modules | Validation |
| --- | --- | --- |
| 1. Session/security foundation | pom.xml, config/SecurityConfig.java, security package, application properties, session schema setup | PostgreSQL session creation/reload, CSRF/CORS, configured origins, login ID rotation |
| 2. Login and current-user flow | controller/AuthController.java or equivalent Spring Security handlers, dto/request/LoginRequest.java, service/security authentication adapter, repository/UserRepository.java, UserController.java | Valid/invalid credentials, inactive account, current-user response, no credential leakage, local logout |
| 3. Account boundaries | UserService, request DTOs, RoleRepository, AddressService/Controller, administrative role operation, bootstrap configuration | Server-assigned CUSTOMER, owner-only profile/address operations, ADMIN-only management, bootstrap idempotence/conflicts |
| 4. Lifetime and global revocation | session policy, Clock configuration, persistent revocation mechanism, UserService state transitions | Both roles' deadlines, multiple logins, password/role revocation, deactivate/reactivate, restart behavior |
| 5. Frontend contract and regression | controller/service/security tests, PostgreSQL integration tests, security/setup documentation | Cookie round trips, real browser restore checks, full existing suite with explicit authenticated/anonymous cases |

Each slice depends on the preceding foundation. Introduce only the dependencies
needed for the current slice, with versions managed by Spring Boot. Session DDL
is infrastructure setup, not a new business entity. Do not delete development
records automatically while preparing the schema.

Use Spring Security's supported context/session integration for login; a custom
JSON login must explicitly apply the required authentication/session lifecycle.
Keep HTTP concerns in the web/security layer, business rules in services, and
repository access out of controllers. Exact public endpoint and SELLER/catalog
permissions must be settled before configuring the final authorization matrix.

## Current implementation: initial foundation (October 20, 2026)

Implemented contracts:

| Method and path | Behavior |
| --- | --- |
| GET /auth/csrf | Public; returns headerName and a CSRF token tied to the cookie session |
| POST /auth/login | JSON email/password, CSRF required; returns UserResponse, rotates session ID and persists authentication |
| GET /auth/me | Authenticated; returns current UserResponse without credentials |
| POST /auth/logout | CSRF required; invalidates the current session, returns 204 |
| POST /users | Public with CSRF; creates CUSTOMER, ignoring any client-supplied roleId |
| GET /products and /products/{id} | Public; active products only, inactive/missing IDs return 404 |
| GET /brand, /brand/{id}, /categories, /categories/{id} | Public reads |

Other routes are denied even to authenticated users until their explicit access
rules are implemented. In particular, existing user management, role reads,
address management, and catalog writes are temporarily unavailable over HTTP.
This is an intermediate implementation, not production-ready authentication.
No cart or purchase endpoint is introduced. The future frontend must redirect
anonymous buyers to login/registration; the API uses JSON errors, not page redirects.

Wrong credentials, unknown users, inactive users, and unsupported legacy password
encodings return the same generic 401 message. Missing authentication on protected
reads returns 401; denied operations or missing/invalid CSRF return 403. Missing
CSRF can therefore produce 403 before an authentication check on a write request.

The current common session timeout is 15 minutes, with non-persistent cookies for
all roles. The approved CUSTOMER persistence, role-specific inactivity/absolute
limits, global password/role revocation, and post-login deactivation enforcement
are still pending. Do not infer these features from the existence of login.
Initial ADMIN bootstrap and owner-authorized user/address endpoints are also pending.

Dependencies added: spring-boot-starter-security for web authentication controls,
spring-boot-starter-session-jdbc for PostgreSQL session storage, and
spring-boot-starter-security-test for Boot 4 integration with simulated identities/CSRF in HTTP contract tests.
Versions remain managed by Spring Boot. No JWT dependency is added.

### Local execution and frontend integration

Run Maven with the explicit local profile for HTTP development:

```powershell
.\mvnw.cmd "-Dspring-boot.run.profiles=local" spring-boot:run
```

The default deployment configuration uses Secure and HttpOnly session cookies;
the local and test profiles allow HTTP cookies explicitly. The cookie is named
TECHSTORE_SESSION, scoped to /, and uses SameSite=Lax. Set TECHSTORE_ALLOWED_ORIGINS
to the actual frontend origin (default development origin: http://localhost:5173).
Do not configure wildcard origins with credentials. Production requires HTTPS.

The frontend must include credentials on all relevant requests. First GET
/auth/csrf, then POST login with the returned header/token. After login, fetch a
fresh CSRF token before subsequent writes/logout because login clears the old
token. Retrieve a fresh token after logout before another login/registration.
Do not store passwords or copy session cookie values into application storage.

Spring Session initializes its PostgreSQL tables with the library's schema via
spring.session.jdbc.initialize-schema=always for the current local setup. This
does not delete application records. Before deployment, plan controlled schema
provisioning rather than relying on development-style initialization.

### Testing boundaries

Start the dedicated local test database with
`docker compose -f docker-compose.test.yml up -d --wait`, then run
`mvnw.cmd test` on Windows or `bash ./mvnw test` on Linux/macOS.
Integration tests and the application context smoke test use the `test` profile,
which connects to PostgreSQL on port 55434. GitHub Actions overrides the datasource
URL and credentials on the Maven step to use its PostgreSQL service on port 5433.
The security/session dependencies in `pom.xml`, test support configuration, and
authentication tests must be included together when committing this implementation.

SessionAuthenticationIntegrationTest starts the actual server with the test
profile and uses a JDK HTTP client with cookies. It exercises the production
filter chain, PostgreSQL session reload/indexing, login ID rotation, CSRF, CORS,
logout/replay rejection, default role assignment, public catalog, and default denial.
PublicCatalogServiceTest verifies the active-only service queries.

Existing WebMvcTest classes continue to test controller contracts independently
of unfinished access rules. They explicitly exclude production SecurityConfig,
import a test-only authenticated security chain, simulate a user, and supply CSRF
on writes. Filters are not disabled and the original HTTP assertions are retained.
Only the integration tests above claim coverage of production authorization.

Validation on October 20, 2026: 71 targeted tests passed after a clean rebuild,
followed by the full test-profile suite with 170 tests, zero failures/errors and
no skipped tests. Cookie and CORS behavior was exercised over real HTTP; browser
UI/session-restoration behavior has not been manually verified.

## Remaining authentication decisions

- Credential error responses.
- Exact session implementation, approved expiration/persistence rules, meaningful
  activity definition, and the approved revocation behavior.
- SELLER permissions and administrative catalog access. Public browsing was
  approved on October 20, 2026; buying/adding to cart requires authentication.
- Initial ADMIN safeguards, including conflicts with existing accounts and
  prevention of administrative lockout.

These decisions are not implemented or implied by the approved permission table.

## Validation

- UserServiceTest checks duplicate emails, missing resources, user state changes,
  password encoding, and preservation of passwords during profile updates.
- UserControllerTest checks validation, DTO serialization without passwords,
  service arguments, and HTTP success/conflict/not-found responses.
- PasswordConfigTest checks actual password matching, incorrect passwords, and
  different salts for repeated encoding of the same password.
- UserServiceIntegrationTest verifies the hashes persisted by creation and
  password changes against the real PostgreSQL test database.

## Sources

- [Spring Security Crypto Module](https://docs.spring.io/spring-security/reference/features/integrations/cryptography.html)
- [Spring Security Password Storage](https://docs.spring.io/spring-security/reference/features/authentication/password-storage.html)
- [Spring Boot Spring Session](https://docs.spring.io/spring-boot/reference/web/spring-session.html)
- [Spring Security CSRF](https://docs.spring.io/spring-security/reference/servlet/exploits/csrf.html)
- [Browser cookie lifetime and session restoration](https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Set-Cookie)
