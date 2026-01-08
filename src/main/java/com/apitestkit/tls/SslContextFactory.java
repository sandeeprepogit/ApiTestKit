
package com.apitestkit.tls;

import com.apitestkit.utils.Preconditions;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.ssl.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.util.List;

public final class SslContextFactory {

    private static final Logger log = LoggerFactory.getLogger(SslContextFactory.class);

    private SslContextFactory() {}

    public static SSLConnectionSocketFactory createSocketFactory(TlsConfig tlsConfig) {
        Preconditions.notNull(tlsConfig, "tlsConfig must not be null");

        try {
            SSLContext sslContext = buildSslContext(tlsConfig);
            String[] protocols = normalizeProtocols(tlsConfig.getEnabledProtocols());

            if (tlsConfig.getMode() == TlsMode.RELAXED_QA) {
                log.warn("RELAXED_QA TLS is enabled (trust-all + no hostname verification). Use ONLY for non-prod.");
                return new SSLConnectionSocketFactory(
                        sslContext,
                        protocols,
                        null,
                        NoopHostnameVerifier.INSTANCE
                );
            }

            return new SSLConnectionSocketFactory(
                    sslContext,
                    protocols,
                    null,
                    SSLConnectionSocketFactory.getDefaultHostnameVerifier()
            );

        } catch (Exception e) {
            throw new IllegalStateException("Failed to create SSL socket factory: " + e.getMessage(), e);
        }
    }

    private static SSLContext buildSslContext(TlsConfig cfg) throws Exception {
        switch (cfg.getMode()) {
            case DEFAULT:
                return SSLContexts.createDefault();

            case TRUSTSTORE:
                Preconditions.notBlank(cfg.getTrustStorePath(), "trustStorePath required for TRUSTSTORE mode");
                return SSLContexts.custom()
                        .loadTrustMaterial(loadKeyStore(cfg.getTrustStorePath(), cfg.getTrustStorePassword(), cfg.getTrustStoreType()), null)
                        .build();

            case MTLS:
                Preconditions.notBlank(cfg.getClientP12Path(), "clientP12Path required for MTLS mode");
                Preconditions.notBlank(cfg.getClientP12Password(), "clientP12Password required for MTLS mode");

                var builder = SSLContexts.custom()
                        .loadKeyMaterial(
                                loadKeyStore(cfg.getClientP12Path(), cfg.getClientP12Password(), "PKCS12"),
                                cfg.getClientP12Password().toCharArray()
                        );

                // Optional truststore
                if (cfg.getTrustStorePath() != null && !cfg.getTrustStorePath().isBlank()) {
                    builder.loadTrustMaterial(loadKeyStore(cfg.getTrustStorePath(), cfg.getTrustStorePassword(), cfg.getTrustStoreType()), null);
                } else {
                    builder.loadTrustMaterial((KeyStore) null, null); // use default trust material
                }
                return builder.build();

            case RELAXED_QA:
                // Trust all (self-signed etc.). Hostname verification disabled in socket factory.
                return SSLContexts.custom()
                        .loadTrustMaterial((chain, authType) -> true)
                        .build();

            default:
                throw new IllegalArgumentException("Unknown TLS mode: " + cfg.getMode());
        }
    }

    private static KeyStore loadKeyStore(String path, String password, String type) throws Exception {
        Preconditions.notBlank(path, "keystore/truststore path must not be blank");
        Preconditions.notBlank(type, "keystore/truststore type must not be blank");

        KeyStore ks = KeyStore.getInstance(type);
        char[] pwd = password == null ? null : password.toCharArray();

        Path p = Path.of(path);
        try (InputStream is = Files.exists(p) ? Files.newInputStream(p) : Thread.currentThread().getContextClassLoader().getResourceAsStream(path)) {
            if (is == null) {
                throw new IllegalStateException("Keystore/Truststore not found at: " + path +
                        " (checked filesystem and classpath).");
            }
            ks.load(is, pwd);
            return ks;
        }
    }

    private static String[] normalizeProtocols(List<String> protocols) {
        if (protocols == null || protocols.isEmpty()) return null; // let JVM choose
        return protocols.toArray(new String[0]);
    }
}
