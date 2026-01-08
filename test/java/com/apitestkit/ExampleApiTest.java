
package com.apitestkit;

import com.apitestkit.assertions.ResponseAssertions;
import com.apitestkit.config.ApiTestKitConfig;
import com.apitestkit.config.ConfigLoader;
import com.apitestkit.http.ApiClient;
import com.apitestkit.http.ApiRequest;
import com.apitestkit.tls.TlsConfig;
import com.apitestkit.tls.TlsMode;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.Locale;

public class ExampleApiTest {

    @Test
    void healthCheck_example() {
        String env = System.getProperty("env", "qa").toLowerCase(Locale.ROOT);
        ApiTestKitConfig cfg = ConfigLoader.load(env);

        // Build TLS config from YAML settings (simple mapping example)
        TlsConfig tls;
        String mode = cfg.getTls() == null ? "DEFAULT" : cfg.getTls().getMode();
        if ("TRUSTSTORE".equalsIgnoreCase(mode)) {
            tls = TlsConfig.trustStore(
                    cfg.getTls().getTrustStorePath(),
                    cfg.getTls().getTrustStorePassword(),
                    cfg.getTls().getTrustStoreType()
            );
        } else if ("MTLS".equalsIgnoreCase(mode)) {
            if (cfg.getTls().getTrustStorePath() != null && !cfg.getTls().getTrustStorePath().isBlank()) {
                tls = TlsConfig.mtls(
                        cfg.getTls().getClientP12Path(),
                        cfg.getTls().getClientP12Password(),
                        cfg.getTls().getTrustStorePath(),
                        cfg.getTls().getTrustStorePassword(),
                        cfg.getTls().getTrustStoreType()
                );
            } else {
                tls = TlsConfig.mtls(cfg.getTls().getClientP12Path(), cfg.getTls().getClientP12Password());
            }
        } else if ("RELAXED_QA".equalsIgnoreCase(mode)) {
            tls = TlsConfig.relaxedForQa();
        } else {
            tls = TlsConfig.defaultTls();
        }

        // Apply protocol list if provided
        if (cfg.getTls() != null && cfg.getTls().getEnabledProtocols() != null && !cfg.getTls().getEnabledProtocols().isEmpty()) {
            tls = TlsConfig.builder()
                    .mode(tls.getMode())
                    .enabledProtocols(cfg.getTls().getEnabledProtocols().toArray(new String[0]))
                    .trustStore(tls.getTrustStorePath(), tls.getTrustStorePassword(), tls.getTrustStoreType())
                    .clientPkcs12(tls.getClientP12Path(), tls.getClientP12Password())
                    .build();
        }

        ApiClient client = ApiClient.builder(cfg)
                .tls(tls)
                .build();

        Response response = client.get("/health"); // endpoint example; change for your system

        ResponseAssertions.assertThat(response)
                .status(200);
    }

    @Test
    void post_example() {
        ApiTestKitConfig cfg = ConfigLoader.load(System.getProperty("env", "qa"));
        ApiClient client = ApiClient.builder(cfg).build();

        ApiRequest request = client.request()
                .header("X-Correlation-Id", "test-123")
                .body("{\"ping\":\"pong\"}");

        Response response = client.post("/api/example", request);

        ResponseAssertions.assertThat(response)
                .status(200);
    }
}
