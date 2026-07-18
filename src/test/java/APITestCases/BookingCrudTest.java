package APITestCases;

import io.restassured.response.Response;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookingCrudTest extends BaseTest {

    private static int bookingId;

    private static final String CREATE_BODY = """
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

    private static final String UPDATE_BODY = """
            {
                "firstname": "Jane",
                "lastname": "Smith",
                "totalprice": 250,
                "depositpaid": false,
                "bookingdates": {
                    "checkin": "2024-03-01",
                    "checkout": "2024-03-10"
                },
                "additionalneeds": "Lunch"
            }
            """;

    @Test
    @Order(1)
    void shouldCreateNewBooking() {
        Response response = given()
                .contentType("application/json")
                .body(CREATE_BODY)
                .when()
                .post("/booking")
                .then()
                .statusCode(200)
                .body("bookingid", notNullValue())
                .body("booking.firstname", equalTo("John"))
                .body("booking.lastname", equalTo("Doe"))
                .body("booking.totalprice", equalTo(150))
                .body("booking.depositpaid", equalTo(true))
                .body("booking.bookingdates.checkin", equalTo("2024-01-01"))
                .body("booking.bookingdates.checkout", equalTo("2024-01-05"))
                .body("booking.additionalneeds", equalTo("Breakfast"))
                .extract().response();

        bookingId = response.jsonPath().getInt("bookingid");
        assertTrue(bookingId > 0, "Booking ID must be a positive number");
    }

    @Test
    @Order(2)
    void shouldRetrieveBookingById() {
        assertTrue(bookingId > 0, "Booking must be created in the previous test");

        given()
                .pathParam("id", bookingId)
                .when()
                .get("/booking/{id}")
                .then()
                .statusCode(200)
                .body("firstname", equalTo("John"))
                .body("lastname", equalTo("Doe"))
                .body("totalprice", equalTo(150))
                .body("depositpaid", equalTo(true))
                .body("additionalneeds", equalTo("Breakfast"));
    }

    @Test
    @Order(3)
    void shouldUpdateBooking() {
        assertTrue(bookingId > 0, "Booking must be created in the previous test");

        given()
                .contentType("application/json")
                .cookie("token", authToken)
                .body(UPDATE_BODY)
                .pathParam("id", bookingId)
                .when()
                .put("/booking/{id}")
                .then()
                .statusCode(200)
                .body("firstname", equalTo("Jane"))
                .body("lastname", equalTo("Smith"))
                .body("totalprice", equalTo(250))
                .body("depositpaid", equalTo(false))
                .body("bookingdates.checkin", equalTo("2024-03-01"))
                .body("bookingdates.checkout", equalTo("2024-03-10"))
                .body("additionalneeds", equalTo("Lunch"));
    }

    @Test
    @Order(4)
    void shouldVerifyBookingWasUpdated() {
        given()
                .pathParam("id", bookingId)
                .when()
                .get("/booking/{id}")
                .then()
                .statusCode(200)
                .body("firstname", equalTo("Jane"))
                .body("totalprice", equalTo(250));
    }

    @Test
    @Order(5)
    void shouldDeleteBooking() {
        assertTrue(bookingId > 0, "Booking must be created in the previous test");

        given()
                .cookie("token", authToken)
                .pathParam("id", bookingId)
                .when()
                .delete("/booking/{id}")
                .then()
                .statusCode(201); // API возвращает 201 при успешном удалении
    }

    @Test
    @Order(6)
    void shouldReturn404AfterBookingDeleted() {
        given()
                .pathParam("id", bookingId)
                .when()
                .get("/booking/{id}")
                .then()
                .statusCode(404);
    }
}