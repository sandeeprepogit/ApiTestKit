
package com.apitestkit.http;

import com.apitestkit.auth.AuthStrategy;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ApiRequest {

    private final RequestSpecification base;
    private AuthStrategy authStrategy;

    private final Map<String, Object> queryParams = new LinkedHashMap<>();
    private final Map<String, Object> pathParams = new LinkedHashMap<>();
    private final Map<String, String> headers = new LinkedHashMap<>();
    private Object body;
    private ContentType contentType = ContentType.JSON;

    ApiRequest(RequestSpecification base) {
        this.base = base;
    }

    public ApiRequest auth(AuthStrategy auth) {
        this.authStrategy = auth;
        return this;
    }

    public ApiRequest header(String name, String value) {
        headers.put(name, value);
        return this;
    }

    public ApiRequest headers(Map<String, String> headers) {
        if (headers != null) this.headers.putAll(headers);
        return this;
    }

    public ApiRequest queryParam(String name, Object value) {
        queryParams.put(name, value);
        return this;
    }

    public ApiRequest pathParam(String name, Object value) {
        pathParams.put(name, value);
        return this;
    }

    public ApiRequest body(Object body) {
        this.body = body;
        return this;
    }

    public ApiRequest contentType(ContentType ct) {
        this.contentType = ct;
        return this;
    }

    RequestSpecification toSpec() {
        RequestSpecification spec = base;
        if (authStrategy != null) spec = authStrategy.apply(spec);

        if (!headers.isEmpty()) spec = spec.headers(headers);
        if (!queryParams.isEmpty()) spec = spec.queryParams(queryParams);
        if (!pathParams.isEmpty()) spec = spec.pathParams(pathParams);

        spec = spec.contentType(contentType);
        if (body != null) spec = spec.body(body);

        return spec;
    }
}
