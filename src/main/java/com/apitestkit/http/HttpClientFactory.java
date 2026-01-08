
package com.apitestkit.http;

import com.apitestkit.config.ProxyConfig;
import com.apitestkit.config.TimeoutsConfig;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

public final class HttpClientFactory {

    private HttpClientFactory() {}

    public static CloseableHttpClient build(TimeoutsConfig timeouts,
                                            ProxyConfig proxy,
                                            SSLConnectionSocketFactory sslSocketFactory) {

        RequestConfig.Builder req = RequestConfig.custom();
        if (timeouts != null) {
            req.setConnectTimeout(timeouts.getConnectTimeoutMs())
                    .setSocketTimeout(timeouts.getSocketTimeoutMs())
                    .setConnectionRequestTimeout(timeouts.getConnectionRequestTimeoutMs());
        }

        HttpClientBuilder builder = HttpClientBuilder.create()
                .setDefaultRequestConfig(req.build());

        if (proxy != null && proxy.isEnabled()) {
            builder.setProxy(new HttpHost(proxy.getHost(), proxy.getPort(), proxy.getScheme()));
        }

        Registry registry = RegistryBuilder.create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", sslSocketFactory)
                .build();

        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
        cm.setMaxTotal(200);
        cm.setDefaultMaxPerRoute(50);

        return builder.setConnectionManager(cm).evictExpiredConnections().build();
    }
}
