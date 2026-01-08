
package com.apitestkit.config;

public class TimeoutsConfig {
    private int connectTimeoutMs = 10_000;
    private int socketTimeoutMs = 30_000;
    private int connectionRequestTimeoutMs = 10_000;

    public int getConnectTimeoutMs() { return connectTimeoutMs; }
    public void setConnectTimeoutMs(int connectTimeoutMs) { this.connectTimeoutMs = connectTimeoutMs; }

    public int getSocketTimeoutMs() { return socketTimeoutMs; }
    public void setSocketTimeoutMs(int socketTimeoutMs) { this.socketTimeoutMs = socketTimeoutMs; }

    public int getConnectionRequestTimeoutMs() { return connectionRequestTimeoutMs; }
    public void setConnectionRequestTimeoutMs(int connectionRequestTimeoutMs) {
        this.connectionRequestTimeoutMs = connectionRequestTimeoutMs;
    }
}

