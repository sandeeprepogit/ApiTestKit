
package com.apitestkit.tls;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class TlsConfig {

    private final TlsMode mode;
    private final String trustStorePath;
    private final String trustStorePassword;
    private final String trustStoreType;

    private final String clientP12Path;
    private final String clientP12Password;

    private final List<String> enabledProtocols;

    private TlsConfig(Builder b) {
        this.mode = b.mode;
        this.trustStorePath = b.trustStorePath;
        this.trustStorePassword = b.trustStorePassword;
        this.trustStoreType = b.trustStoreType;
        this.clientP12Path = b.clientP12Path;
        this.clientP12Password = b.clientP12Password;
        this.enabledProtocols = b.enabledProtocols == null ? Collections.emptyList() : List.copyOf(b.enabledProtocols);
    }

    public static TlsConfig defaultTls() {
        return builder().mode(TlsMode.DEFAULT).build();
    }

    public static TlsConfig trustStore(String path, String password, String type) {
        return builder().mode(TlsMode.TRUSTSTORE)
                .trustStore(path, password, type)
                .build();
    }

    /** QA only: trust all certs + disable hostname verification (blocked in prod by ApiClient builder). */
    public static TlsConfig relaxedForQa() {
        return builder().mode(TlsMode.RELAXED_QA).build();
    }

    public static TlsConfig mtls(String clientP12Path, String clientP12Password) {
        return builder().mode(TlsMode.MTLS)
                .clientPkcs12(clientP12Path, clientP12Password)
                .build();
    }

    public static TlsConfig mtls(String clientP12Path, String clientP12Password,
                                 String trustStorePath, String trustStorePassword, String trustStoreType) {
        return builder().mode(TlsMode.MTLS)
                .clientPkcs12(clientP12Path, clientP12Password)
                .trustStore(trustStorePath, trustStorePassword, trustStoreType)
                .build();
    }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private TlsMode mode = TlsMode.DEFAULT;
        private String trustStorePath;
        private String trustStorePassword;
        private String trustStoreType = "JKS";

        private String clientP12Path;
        private String clientP12Password;

        private List<String> enabledProtocols = new ArrayList<>();

        public Builder mode(TlsMode mode) { this.mode = mode; return this; }

        public Builder trustStore(String path, String password, String type) {
            this.trustStorePath = path;
            this.trustStorePassword = password;
            this.trustStoreType = type;
            return this;
        }

        public Builder clientPkcs12(String path, String password) {
            this.clientP12Path = path;
            this.clientP12Password = password;
            return this;
        }

        public Builder enabledProtocols(String... protocols) {
            this.enabledProtocols = new ArrayList<>(Arrays.asList(protocols));
            return this;
        }

        public TlsConfig build() { return new TlsConfig(this); }
    }

    public TlsMode getMode() { return mode; }
    public String getTrustStorePath() { return trustStorePath; }
    public String getTrustStorePassword() { return trustStorePassword; }
    public String getTrustStoreType() { return trustStoreType; }
    public String getClientP12Path() { return clientP12Path; }
    public String getClientP12Password() { return clientP12Password; }
    public List<String> getEnabledProtocols() { return enabledProtocols; }
}
