
package com.apitestkit.auth;

import io.restassured.specification.RequestSpecification;

public class BasicAuth implements AuthStrategy {
    private final String username;
    private final String password;

    public BasicAuth(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public RequestSpecification apply(RequestSpecification spec) {
        return spec.auth().preemptive().basic(username, password);
    }
}
