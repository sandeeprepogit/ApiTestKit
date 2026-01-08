
package com.apitestkit.http;

import com.apitestkit.config.ApiTestKitConfig;
import com.apitestkit.config.RetryConfig;
import com.apitestkit.logging.RestAssuredMaskingFilter;
import com.apitestkit.tls.SslContextFactory;
import com.apitestkit.tls.TlsConfig;
import com.apitestkit.tls.TlsMode;
import com.apitestkit.utils.Preconditions;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.Map;

import static io.restassured.RestAssured.given;

public final class ApiClient {

    private static final Logger log = LoggerFactory.getLogger(ApiClient.class);

    private final ApiTestKitConfig cfg;
    private final TlsConfig tlsConfig;
    private final RetryConfig retryConfig;

    private final RequestSpecification baseSpec;

    private ApiClient(Builder b) {
        this.cfg = b.cfg;
        this.tlsConfig = b.tlsConfig;
        this.retryConfig = b.retryConfig;

        // Safeguard: relaxed SSL must never be allowed in prod
        if (tlsConfig != null && tlsConfig.getMode() == TlsMode.RELAXED_QA) {
            String env = cfg.getEnvironment() == null ? "" : cfg.getEnvironment().toLowerCase(Locale.ROOT);
            if ("prod".equals(env) || "production".equals(env)) {
                throw new IllegalStateException("RELAXED_QA TLS is forbidden in production.");
            }
        }

        var sslSocketFactory = SslContextFactory.createSocketFactory(tlsConfig == null ? TlsConfig.defaultTls() : tlsConfig);

        CloseableHttpClient apacheClient = HttpClientFactory.build(cfg.getTimeouts(), cfg.getProxy(), sslSocketFactory);

        RestAssuredConfig raConfig = RestAssuredConfig.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .httpClientFactory(() -> apacheClient));

        RequestSpecification spec = given()
                .config(raConfig)
                .baseUri(cfg.getBaseUrl())
                .filter(new RestAssuredMaskingFilter(cfg.getMaskedHeaders(), cfg.getMaskedJsonFields()));

        // Default headers from config
        Map<String, String> defaults = cfg.getDefaultHeaders();
        if (defaults != null && !defaults.isEmpty()) {
            spec = spec.headers(defaults);
        }

        this.baseSpec = spec;
    }

    public static Builder builder(ApiTestKitConfig cfg) {
        return new Builder(cfg);
    }

    public ApiRequest request() {
        return new ApiRequest(baseSpec);
    }

    public Response get(String path) {
        return execute("GET", path, request());
    }

    public Response delete(String path) {
        return execute("DELETE", path, request());
    }

    public Response post(String path, ApiRequest req) {
        return execute("POST", path, req);
    }

    public Response put(String path, ApiRequest req) {
        return execute("PUT", path, req);
    }

    public Response patch(String path, ApiRequest req) {
        return execute("PATCH", path, req);
    }

    private Response execute(String method, String path, ApiRequest req) {
        Preconditions.notBlank(path, "path must not be blank");
        return RetryExecutor.execute(retryConfig,
                () -> invoke(method, path, req),
                (Response r) -> retryConfig != null
                        && retryConfig.isEnabled()
                        && retryConfig.getRetryOnStatusCodes() != null
                        && retryConfig.getRetryOnStatusCodes().contains(r.getStatusCode())
        );
    }

    private Response invoke(String method, String path, ApiRequest req) {
        RequestSpecification spec = (req == null ? request() : req).toSpec();

        switch (method) {
            case "GET": return spec.when().get(path);
            case "DELETE": return spec.when().delete(path);
            case "POST": return spec.when().post(path);
            case "PUT": return spec.when().put(path);
            case "PATCH": return spec.when().patch(path);
            default: throw new IllegalArgumentException("Unsupported method: " + method);
        }
    }

    public static final class Builder {
        private final ApiTestKitConfig cfg;
        private TlsConfig tlsConfig = TlsConfig.defaultTls();
        private RetryConfig retryConfig;

        private Builder(ApiTestKitConfig cfg) {
            this.cfg = cfg;
            Preconditions.notNull(cfg, "ApiTestKitConfig must not be null");
            Preconditions.notBlank(cfg.getBaseUrl(), "baseUrl must not be blank");
        }

        public Builder tls(TlsConfig tlsConfig) {
            this.tlsConfig = tlsConfig;
            return this;
        }

        public Builder retry(RetryConfig retryConfig) {
            this.retryConfig = retryConfig;
            return this;
        }

        public ApiClient build() {
            if (retryConfig == null) retryConfig = cfg.getRetry();
            return new ApiClient(this);
        }
    }
}
