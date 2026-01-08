
package com.apitestkit.config;

import java.util.List;

public class RetryConfig {
    private boolean enabled = false;
    private int maxAttempts = 3;
    private long baseDelayMs = 250;
    private long maxDelayMs = 2000;
    private double multiplier = 2.0;

    private List<Integer> retryOnStatusCodes = List.of(429, 500, 502, 503, 504);

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public int getMaxAttempts() { return maxAttempts; }
    public void setMaxAttempts(int maxAttempts) { this.maxAttempts = maxAttempts; }

    public long getBaseDelayMs() { return baseDelayMs; }
    public void setBaseDelayMs(long baseDelayMs) { this.baseDelayMs = baseDelayMs; }

    public long getMaxDelayMs() { return maxDelayMs; }
    public void setMaxDelayMs(long maxDelayMs) { this.maxDelayMs = maxDelayMs; }

    public double getMultiplier() { return multiplier; }
    public void setMultiplier(double multiplier) { this.multiplier = multiplier; }

    public List<Integer> getRetryOnStatusCodes() { return retryOnStatusCodes; }
    public void setRetryOnStatusCodes(List<Integer> retryOnStatusCodes) { this.retryOnStatusCodes = retryOnStatusCodes; }
}
