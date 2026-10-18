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
rather than writing cryptographic code. It does not introduce a security filter
chain, login, JWT, or endpoint authorization. Hashing is a one-way derivation,
not reversible encryption. In future authentication code, use matches(raw, hash),
not equality between two fresh hashes: salts make those hashes different.

Existing database passwords are not rewritten by this change. Any legacy raw
passwords require an explicitly approved migration or reset plan before real
accounts can be supported. Work-factor benchmarking, password policy, reset
flows, and transport protection belong to the deployment/authentication scope.

## Role assignment: approved policy, enforcement pending

Current create and update requests still accept roleId and resolve any existing
role. No caller authentication or authorization is currently enforced.
Hashing alone does not make these APIs ready for real user data.

The owner approved the following policy on October 18, 2026:

- Public registration always assigns CUSTOMER on the server.
- Only an authenticated ADMIN can assign or change roles, including SELLER.
- Public registration and profile update DTOs do not accept roleId.
- Administrative role assignment uses a separate protected operation.

This policy is approved but is not enforced by the current endpoints. Implement
the request contract changes together with authenticated administrative operations.
Authentication must establish the caller before authorization can enforce it.
Owner decisions are also needed for the initial administrator and the boundaries
between self-service profile updates and administrative user management.

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
