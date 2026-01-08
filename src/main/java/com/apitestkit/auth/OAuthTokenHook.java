
package com.apitestkit.auth;

import io.restassured.specification.RequestSpecification;

import java.util.function.Supplier;

public class OAuthTokenHook implements AuthStrategy {
    private final Supplier<String> tokenSupplier;

    public OAuthTokenHook(Supplier<String> tokenSupplier) {
        this.tokenSupplier = tokenSupplier;
    }

    @Override
    public RequestSpecification apply(RequestSpecification spec) {
        String token = tokenSupplier.get();
        return spec.header("Authorization", "Bearer " + token);
    }
}
