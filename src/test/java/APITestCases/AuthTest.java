package APITestCases;

import io.github.cdimascio.dotenv.Dotenv;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import io.github.cdimascio.dotenv.Dotenv;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthTest {

    static Dotenv dotenv = Dotenv.load();

    private static final String BASE_URI = dotenv.get("BOOK_URL");
    private static String authToken;

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = BASE_URI;
    }

    @Test
    @Order(1)
    void shouldReturnTokenOnValidCredentials() {
        String requestBody = """
                {
                    "username": "admin",
                    "password": "password123"
                }
                """;

        Response response = given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/auth")
                .then()
                .statusCode(200)
                .body("token", notNullValue())
                .body("token", matchesPattern("^[a-zA-Z0-9]+$"))
                .extract().response();

        // Сохраняем токен для использования в других тестах
        authToken = response.jsonPath().getString("token");

        assertNotNull(authToken, "The token must not be null");
        assertFalse(authToken.isEmpty(), "The token must not be empty");
    }

    @Test
    @Order(2)
    void shouldReturn400OnInvalidCredentials() {
        String requestBody = """
                {
                    "username": "wrong",
                    "password": "wrongpass"
                }
                """;

        given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/auth")
                .then()
                .statusCode(200)
                .body("reason", equalTo("Bad credentials"));
    }

    @Test
    @Order(3)
    void shouldUseTokenForCreatingBooking() {

        assertNotNull(authToken, "The token must have been obtained in the previous test.");

        String bookingBody = """
                {
                    "firstname": "John",
                    "lastname": "Doe",
                    "totalprice": 150,
                    "depositpaid": true,
                    "bookingdates": {
                        "checkin": "2024-01-01",
                        "checkout": "2024-01-05"
                    },
                    "additionalneeds": "Breakfast"
                }
                """;

        int bookingId = given()
                .contentType("application/json")
                .body(bookingBody)
                .when()
                .post("/booking")
                .then()
                .statusCode(200)
                .extract().path("bookingid");

        String updateBody = """
                {
                    "firstname": "Jane",
                    "lastname": "Doe",
                    "totalprice": 200,
                    "depositpaid": false,
                    "bookingdates": {
                        "checkin": "2024-02-01",
                        "checkout": "2024-02-10"
                    },
                    "additionalneeds": "Lunch"
                }
                """;

        given()
                .contentType("application/json")
                .cookie("token", authToken)
                .body(updateBody)
                .when()
                .put("/booking/" + bookingId)
                .then()
                .statusCode(200)
                .body("firstname", equalTo("Jane"));
    }
}