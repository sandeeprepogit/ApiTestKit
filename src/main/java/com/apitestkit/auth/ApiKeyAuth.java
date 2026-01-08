
package com.apitestkit.auth;

import io.restassured.specification.RequestSpecification;

public class ApiKeyAuth implements AuthStrategy {
    private final String headerName;
    private final String apiKey;

    public ApiKeyAuth(String headerName, String apiKey) {
        this.headerName = headerName;
        this.apiKey = apiKey;
    }

    @Override
    public RequestSpecification apply(RequestSpecification spec) {
        return spec.header(headerName, apiKey);
    }
}
