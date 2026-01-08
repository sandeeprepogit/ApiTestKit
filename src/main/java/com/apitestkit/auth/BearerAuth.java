
package com.apitestkit.auth;

import io.restassured.specification.RequestSpecification;

public class BearerAuth implements AuthStrategy {
    private final String token;

    public BearerAuth(String token) { this.token = token; }

    @Override
    public RequestSpecification apply(RequestSpecification spec) {
        return spec.header("Authorization", "Bearer " + token);
    }
}
