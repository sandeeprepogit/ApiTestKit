
package com.apitestkit.config;

import java.util.List;
import java.util.Map;

public class ApiTestKitConfig {

    private String environment;        // dev/qa/stage/prod
    private String baseUrl;

    private Map<String, String> defaultHeaders;
    private List<String> maskedHeaders;
    private List<String> maskedJsonFields;

    private TimeoutsConfig timeouts;
    private ProxyConfig proxy;
    private RetryConfig retry;
    private TlsSettings tls;

    public String getEnvironment() { return environment; }
    public void setEnvironment(String environment) { this.environment = environment; }

    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

    public Map<String, String> getDefaultHeaders() { return defaultHeaders; }
    public void setDefaultHeaders(Map<String, String> defaultHeaders) { this.defaultHeaders = defaultHeaders; }

    public List<String> getMaskedHeaders() { return maskedHeaders; }
    public void setMaskedHeaders(List<String> maskedHeaders) { this.maskedHeaders = maskedHeaders; }

    public List<String> getMaskedJsonFields() { return maskedJsonFields; }
    public void setMaskedJsonFields(List<String> maskedJsonFields) { this.maskedJsonFields = maskedJsonFields; }

    public TimeoutsConfig getTimeouts() { return timeouts; }
    public void setTimeouts(TimeoutsConfig timeouts) { this.timeouts = timeouts; }

    public ProxyConfig getProxy() { return proxy; }
    public void setProxy(ProxyConfig proxy) { this.proxy = proxy; }

    public RetryConfig getRetry() { return retry; }
    public void setRetry(RetryConfig retry) { this.retry = retry; }

    public TlsSettings getTls() { return tls; }
    public void setTls(TlsSettings tls) { this.tls = tls; }
}
