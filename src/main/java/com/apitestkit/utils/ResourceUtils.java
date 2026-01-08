
package com.apitestkit.utils;

import java.io.InputStream;

public final class ResourceUtils {
    private ResourceUtils() {}

    public static InputStream resourceAsStream(String name) {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
        if (is == null) throw new IllegalStateException("Resource not found on classpath: " + name);
        return is;
    }
}
