
package com.apitestkit.utils;

public final class Preconditions {
    private Preconditions() {}

    public static void notNull(Object obj, String msg) {
        if (obj == null) throw new IllegalArgumentException(msg);
    }

    public static void notBlank(String s, String msg) {
        if (s == null || s.isBlank()) throw new IllegalArgumentException(msg);
    }
}
