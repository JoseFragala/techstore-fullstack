package com.fragala.techstore.security;

import static org.junit.jupiter.api.Assertions.*;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.session.jdbc.JdbcIndexedSessionRepository;
import org.springframework.test.context.ActiveProfiles;

import com.fragala.techstore.controller.AuthController.CsrfResponse;
import com.fragala.techstore.dto.request.CreateUserRequest;
import com.fragala.techstore.dto.request.LoginRequest;
import com.fragala.techstore.dto.response.UserResponse;
import com.fragala.techstore.repository.UserRepository;
import com.fragala.techstore.service.UserService;

import tools.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class SessionAuthenticationIntegrationTest {
    @LocalServerPort
    private int port;
    @Autowired
    private UserService users;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JdbcIndexedSessionRepository sessions;
    @Autowired
    private ObjectMapper json;
    @Autowired
    private com.fragala.techstore.repository.ProductRepository products;
    @Autowired
    private com.fragala.techstore.repository.BrandRepository brands;
    @Autowired
    private com.fragala.techstore.repository.CategoryRepository categories;

    private Long userId;
    private String email;
    private final String password = "session-integration-password";
    private CookieManager cookies;
    private HttpClient browser;

    @BeforeEach
    void setUp() {
        email = "session-" + UUID.randomUUID() + "@example.com";
        userId = users.create(new CreateUserRequest("Session test user", email, password, "11999990000")).id();
        cookies = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
        browser = HttpClient.newBuilder().cookieHandler(cookies).build();
    }

    @AfterEach
    void cleanUp() {
        sessions.findByPrincipalName(userId.toString()).keySet().forEach(sessions::deleteById);
        userRepository.deleteById(userId);
    }

    @Test
    void catalog_shouldAllowAnonymousReadsAndRejectProtectedAccess() throws Exception {
        assertEquals(200, get("/products").statusCode());
        assertEquals(200, get("/brand").statusCode());
        assertEquals(200, get("/categories").statusCode());
        assertEquals(401, get("/auth/me").statusCode());
        assertEquals(401, get("/users").statusCode());
    }

    @Test
    void catalog_shouldHideInactiveProductFromListAndLookup() throws Exception {
        var brand = new com.fragala.techstore.entity.Brand();
        brand.setName("Session brand " + UUID.randomUUID());
        brand = brands.saveAndFlush(brand);
        var category = new com.fragala.techstore.entity.Category();
        category.setName("Session category " + UUID.randomUUID());
        category = categories.saveAndFlush(category);
        var product = new com.fragala.techstore.entity.Product("Session product",
            "Integration fixture", brand, category, new java.math.BigDecimal("19.90"),
            "SESSION-" + UUID.randomUUID(), 3);
        product = products.saveAndFlush(product);
        try {
            assertEquals(200, get("/products/" + product.getId()).statusCode());
            product.deactivate();
            products.saveAndFlush(product);
            assertEquals(404, get("/products/" + product.getId()).statusCode());
            assertFalse(get("/products").body().contains(product.getSku()));
        } finally {
            products.deleteById(product.getId());
            categories.deleteById(category.getId());
            brands.deleteById(brand.getId());
        }
    }

    @Test
    void login_shouldRotateAndPersistSessionAndReturnCurrentUser_whenCredentialsAreValid() throws Exception {
        CsrfResponse csrf = csrf();
        String anonymousCookie = cookieValue();
        var response = post("/auth/login", new LoginRequest(email, password), csrf);
        assertEquals(200, response.statusCode(), response.body());
        assertNotEquals(anonymousCookie, cookieValue());
        assertTrue(response.headers().allValues("set-cookie").stream()
            .anyMatch(value -> value.contains("HttpOnly") && value.contains("SameSite=Lax")));
        assertFalse(response.body().contains("password"));
        assertEquals(userId, json.readValue(get("/auth/me").body(), UserResponse.class).id());

        var stored = sessions.findByPrincipalName(userId.toString());
        assertEquals(1, stored.size());
        org.springframework.session.Session storedSession = stored.values().iterator().next();
        var context = (org.springframework.security.core.context.SecurityContext)
            storedSession.getAttribute("SPRING_SECURITY_CONTEXT");
        assertInstanceOf(SessionUser.class, context.getAuthentication().getPrincipal());
        assertNull(context.getAuthentication().getCredentials());
    }

    @Test
    void login_shouldReturn401WithoutSessionAuthentication_whenPasswordIsWrong() throws Exception {
        var response = post("/auth/login", new LoginRequest(email, "wrong-password"), csrf());
        assertEquals(401, response.statusCode());
        assertTrue(response.body().contains("Invalid email or password"));
        assertEquals(401, get("/auth/me").statusCode());
    }

    @Test
    void login_shouldRejectInvalidRequestWithoutSessionAuthentication_whenCredentialsAreMalformed() throws Exception {
        var invalidEmail = post("/auth/login", new LoginRequest("invalid-email", password), csrf());
        var blankPassword = post("/auth/login", new LoginRequest(email, " "), csrf());

        assertEquals(400, invalidEmail.statusCode());
        assertEquals(400, blankPassword.statusCode());
        assertEquals(401, get("/auth/me").statusCode());
    }

    @Test
    void login_shouldReturnSameGenericFailure_whenAccountIsMissingOrInactive() throws Exception {
        var missing = post("/auth/login", new LoginRequest("missing@example.com", password), csrf());
        users.deactivate(userId);
        var inactive = post("/auth/login", new LoginRequest(email, password), csrf());
        assertEquals(401, missing.statusCode());
        assertEquals(401, inactive.statusCode());
        assertEquals(missing.body(), inactive.body());
        assertEquals(401, get("/auth/me").statusCode());
    }

    @Test
    void login_shouldRejectMissingCsrfToken() throws Exception {
        assertEquals(403, post("/auth/login", new LoginRequest(email, password), null).statusCode());
    }

    @Test
    void logout_shouldInvalidateCurrentSessionAndRejectOldCookie() throws Exception {
        assertEquals(200, post("/auth/login", new LoginRequest(email, password), csrf()).statusCode());
        String oldCookie = cookieValue();
        assertEquals(403, post("/auth/logout", null, null).statusCode());
        assertEquals(204, post("/auth/logout", null, csrf()).statusCode());
        assertEquals(401, get("/auth/me").statusCode());
        var replay = HttpClient.newHttpClient().send(HttpRequest.newBuilder(uri("/auth/me"))
            .header("Cookie", "TECHSTORE_SESSION=" + oldCookie).GET().build(), HttpResponse.BodyHandlers.ofString());
        assertEquals(401, replay.statusCode());
        assertTrue(sessions.findByPrincipalName(userId.toString()).isEmpty());
    }

    @Test
    void cors_shouldAllowConfiguredFrontendAndRejectUnknownOrigin() throws Exception {
        var allowed = preflight("http://localhost:5173");
        assertEquals(200, allowed.statusCode());
        assertEquals("http://localhost:5173", allowed.headers().firstValue("Access-Control-Allow-Origin").orElseThrow());
        assertEquals("true", allowed.headers().firstValue("Access-Control-Allow-Credentials").orElseThrow());
        assertEquals(403, preflight("https://untrusted.example").statusCode());
    }

    @Test
    void registration_shouldAssignCustomerEvenIfClientSendsRoleId() throws Exception {
        String registrationEmail = "registration-" + UUID.randomUUID() + "@example.com";
        try {
            String body = "{\"name\":\"New user\",\"email\":\"" + registrationEmail
                + "\",\"password\":\"registration-password\",\"phone\":\"11999990000\",\"roleId\":999}";
            CsrfResponse csrf = csrf();
            var response = browser.send(HttpRequest.newBuilder(uri("/users"))
                .header("Content-Type", "application/json").header(csrf.headerName(), csrf.token())
                .POST(HttpRequest.BodyPublishers.ofString(body)).build(), HttpResponse.BodyHandlers.ofString());
            assertEquals(201, response.statusCode(), response.body());
            assertEquals("CUSTOMER", userRepository.findByEmail(registrationEmail).orElseThrow().getRole().getName());
        } finally {
            userRepository.findByEmail(registrationEmail).ifPresent(userRepository::delete);
        }
    }

    @Test
    void protectedOperations_shouldStayClosedUntilOwnershipRulesAreImplemented() throws Exception {
        assertEquals(200, post("/auth/login", new LoginRequest(email, password), csrf()).statusCode());
        assertEquals(403, get("/users").statusCode());
        assertEquals(403, get("/addresses").statusCode());
        assertEquals(403, post("/products", null, csrf()).statusCode());
    }

    private URI uri(String path) {
        return URI.create("http://127.0.0.1:" + port + path);
    }

    private HttpResponse<String> get(String path) throws Exception {
        return browser.send(HttpRequest.newBuilder(uri(path)).GET().build(), HttpResponse.BodyHandlers.ofString());
    }

    private CsrfResponse csrf() throws Exception {
        var response = get("/auth/csrf");
        assertEquals(200, response.statusCode());
        return json.readValue(response.body(), CsrfResponse.class);
    }

    private String cookieValue() {
        return cookies.getCookieStore().getCookies().stream()
            .filter(cookie -> cookie.getName().equals("TECHSTORE_SESSION")).findFirst().orElseThrow().getValue();
    }

    private HttpResponse<String> post(String path, Object body, CsrfResponse csrf) throws Exception {
        var request = HttpRequest.newBuilder(uri(path)).header("Content-Type", "application/json");
        if (csrf != null) {
            request.header(csrf.headerName(), csrf.token());
        }
        return browser.send(request.POST(HttpRequest.BodyPublishers.ofString(
            body == null ? "{}" : json.writeValueAsString(body))).build(), HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> preflight(String origin) throws Exception {
        return browser.send(HttpRequest.newBuilder(uri("/auth/login"))
            .header("Origin", origin).header("Access-Control-Request-Method", "POST")
            .header("Access-Control-Request-Headers", "content-type,x-csrf-token")
            .method("OPTIONS", HttpRequest.BodyPublishers.noBody()).build(), HttpResponse.BodyHandlers.ofString());
    }
}
