
package com.apitestkit.assertions;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import static org.junit.jupiter.api.Assertions.*;

public final class ResponseAssertions {

    private final Response response;

    private ResponseAssertions(Response response) {
        this.response = response;
    }

    public static ResponseAssertions assertThat(Response response) {
        assertNotNull(response, "response must not be null");
        return new ResponseAssertions(response);
    }

    public ResponseAssertions status(int expected) {
        assertEquals(expected, response.getStatusCode(),
                "Expected status " + expected + " but got " + response.getStatusCode() + ". Body: " + response.asString());
        return this;
    }

    public ResponseAssertions bodyFieldEquals(String jsonPath, Object expected) {
        JsonPath jp = response.jsonPath();
        Object actual = jp.get(jsonPath);
        assertEquals(expected, actual, "Mismatch at jsonPath: " + jsonPath);
        return this;
    }

    public ResponseAssertions bodyFieldNotNull(String jsonPath) {
        JsonPath jp = response.jsonPath();
        Object actual = jp.get(jsonPath);
        assertNotNull(actual, "Expected non-null value at jsonPath: " + jsonPath);
        return this;
    }

    /** Optional: JSON schema validation if dependency exists (json-schema-validator). */
    public ResponseAssertions matchesJsonSchemaInClasspath(String schemaPath) {
        SchemaValidator.validateIfAvailable(response, schemaPath);
        return this;
    }
}
