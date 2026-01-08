
package com.apitestkit.config;

import java.util.List;

public class TlsSettings {
    private String mode = "DEFAULT"; // DEFAULT, TRUSTSTORE, RELAXED_QA, MTLS
    private String trustStorePath;
    private String trustStorePassword;
    private String trustStoreType; // JKS or PKCS12

    private String clientP12Path;
    private String clientP12Password;

    private List<String> enabledProtocols; // e.g. ["TLSv1.2","TLSv1.3"]

    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }

    public String getTrustStorePath() { return trustStorePath; }
    public void setTrustStorePath(String trustStorePath) { this.trustStorePath = trustStorePath; }

    public String getTrustStorePassword() { return trustStorePassword; }
    public void setTrustStorePassword(String trustStorePassword) { this.trustStorePassword = trustStorePassword; }

    public String getTrustStoreType() { return trustStoreType; }
    public void setTrustStoreType(String trustStoreType) { this.trustStoreType = trustStoreType; }

    public String getClientP12Path() { return clientP12Path; }
    public void setClientP12Path(String clientP12Path) { this.clientP12Path = clientP12Path; }

    public String getClientP12Password() { return clientP12Password; }
    public void setClientP12Password(String clientP12Password) { this.clientP12Password = clientP12Password; }

    public List<String> getEnabledProtocols() { return enabledProtocols; }
    public void setEnabledProtocols(List<String> enabledProtocols) { this.enabledProtocols = enabledProtocols; }
}
