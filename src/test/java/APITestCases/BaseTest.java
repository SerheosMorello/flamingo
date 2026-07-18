package APITestCases;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;

import static io.restassured.RestAssured.given;

public class BaseTest {

    protected static String authToken;

    @BeforeAll
    static void authenticate() {
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";

        String requestBody = """
                {
                    "username": "admin",
                    "password": "password123"
                }
                """;

        authToken = given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/auth")
                .then()
                .statusCode(200)
                .extract().path("token");
    }
}