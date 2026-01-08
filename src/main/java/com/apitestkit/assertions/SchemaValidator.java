
package com.apitestkit.assertions;

import io.restassured.response.Response;

public final class SchemaValidator {

    private SchemaValidator() {}

    public static void validateIfAvailable(Response response, String schemaClasspathPath) {
        try {
            Class<?> matcher = Class.forName("io.restassured.module.jsv.JsonSchemaValidator");
            var method = matcher.getMethod("matchesJsonSchemaInClasspath", String.class);
            Object hamcrestMatcher = method.invoke(null, schemaClasspathPath);

            // response.then().assertThat().body(hamcrestMatcher)
            response.then().assertThat().body((org.hamcrest.Matcher) hamcrestMatcher);

        } catch (ClassNotFoundException cnf) {
            // Optional dependency not present
            throw new IllegalStateException("json-schema-validator dependency not found. " +
                    "Add io.rest-assured:json-schema-validator to enable schema validation.");
        } catch (Exception e) {
            throw new IllegalStateException("Schema validation failed: " + e.getMessage(), e);
        }
    }
}
