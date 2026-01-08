
package com.apitestkit.auth;

import io.restassured.specification.RequestSpecification;

@FunctionalInterface
public interface AuthStrategy {
    RequestSpecification apply(RequestSpecification spec);
}
