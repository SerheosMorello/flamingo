package GraphQLTestCases;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import io.github.cdimascio.dotenv.Dotenv;

public class GraphQLTestBase {
    protected static RequestSpecification spec;
    static Dotenv dotenv = Dotenv.load();
    static final String GRAPHQL_URL = dotenv.get("GRAPHQL_URL");

    @BeforeAll
    static void setup() {
        spec = new RequestSpecBuilder()
                .setBaseUri(GRAPHQL_URL)
                .setContentType(ContentType.JSON)
                .build();
    }

    public Response executeQuery(String query, Map<String, Object> variables) {
        GraphQLRequest body = new GraphQLRequest(query, variables);
        return given()
                .spec(spec)
                .body(body)
                .when()
                .post();
    }


    @Test
    void shouldReturnLimitedVideosList() {
        String query = """
                query ExampleQuery {
                  movies (first: 5) {
                    title
                    moviePoster {
                      url
                    }
                  }
                }
        """;



        Response response = executeQuery(query, null);

        response.then().statusCode(200);

        List<Object> videos = response.jsonPath().getList("data.movies");

        assertThat(videos).hasSizeLessThanOrEqualTo(5);
    }

    @Test
    void shouldReturnVideoById() {
        String query = """
                query Query {
                  movies(where: { id: "clq16555f0nqq0ak8fvxe2c0d"} ){
                    title
                    id
                  }
                }
        """;

        Response response = executeQuery(query, null);

        List<MovieDto> movies = response.jsonPath().getList("data.movies", MovieDto.class);

        assertThat(movies).isNotNull();
        assertThat(movies.get(0).id()).isEqualTo("clq16555f0nqq0ak8fvxe2c0d");
    }

    @Test
    void shouldReturnVideoByIdUsingVariables() {
        String existingVideoId = "clq16555f0nqq0ak8fvxe2c0d";

        String query = """
        query GetVideo($id: ID!) {
          movies(where: { id: $id }) {
            id
            title
          }
        }
        """;

        Response response = executeQuery(query, Map.of("id", existingVideoId));

        assertThat(response.jsonPath().getString("data.movies.id[0]"))
                .isEqualTo(existingVideoId);
    }

    @Test
    void shouldReturnVideoWithPublisher() {
        String query = """
        fragment PublisherInfo on User {
          id
          name
        }
        
        query ExampleQuery {
          movies(first: 3) {
            id
            publishedBy {
              ...PublisherInfo
            }
          }
        }
        """;

        Response response = executeQuery(query, null);

        List<Map<String, Object>> videos = response.jsonPath().getList("data.movies");
        assertThat(videos).allSatisfy(v ->
                assertThat(v).extracting(m -> ((Map<?, ?>) m.get("publishedBy")).get("name"))
                        .isNotNull()
        );
    }

    @Test
    void shouldReturnNullForNonExistentMovieId() {
        String query = """
        query ExampleQuery {
          movies(where: { id: "non-existent-id-000000" }) {
            id
            title
          }
        }
        """;

        Response response = executeQuery(query, null);

        response.then().statusCode(200);

        // errors либо отсутствует, либо пустой массив — фиксируем это явно
        assertThat(response.jsonPath().getList("errors")).isNullOrEmpty();

        List<MovieDto> movies = response.jsonPath().getList("data.movies", MovieDto.class);
        assertThat(movies).isEmpty(); // а не null — т.к. movies(where:) с filter вернёт пустой список
    }

    @Test
    void shouldReturnSyntaxErrorForMalformedQuery() {
        String malformedQuery = """
        query ExampleQuery {
          movies( {
            id
            title
        """;

        Response response = executeQuery(malformedQuery, null);

        response.then().statusCode(400);

        Object data = response.jsonPath().get("data");
        assertThat(data).isNull();

        List<String> errorMessages = response.jsonPath().getList("errors.message", String.class);
        assertThat(errorMessages).isNotEmpty();
        assertThat(errorMessages.get(0)).containsIgnoringCase("parse error");
    }

    @Test
    void shouldReturnValidationErrorForNonExistentField() {
        String query = """
        query ExampleQuery {
          movies {
            id
            title
            nonExistentField
          }
        }
        """;

        Response response = executeQuery(query, null);

        response.then().statusCode(400);

        Object data = response.jsonPath().get("data");
        assertThat(data).isNull();

        List<String> errorMessages = response.jsonPath().getList("errors.message", String.class);
        assertThat(errorMessages).isNotEmpty();
        assertThat(errorMessages.get(0))
                .containsIgnoringCase("is not defined in")
                .contains("nonExistentField");
    }

}


record GraphQLRequest(String query, Map<String, Object> variables) {}

record MovieDto(String id, String title) {}
