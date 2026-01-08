
package com.apitestkit.logging;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class MaskingUtils {

    private MaskingUtils() {}

    public static String maskHeader(String headerName, String headerValue, List<String> maskedHeaders) {
        if (headerName == null) return headerValue;
        String hn = headerName.toLowerCase(Locale.ROOT);

        boolean shouldMask = maskedHeaders != null && maskedHeaders.stream()
                .anyMatch(x -> x != null && hn.equalsIgnoreCase(x.trim()));

        if (!shouldMask) {
            // Sensible defaults
            shouldMask = hn.contains("authorization") || hn.contains("token") || hn.contains("apikey") || hn.contains("api-key");
        }
        return shouldMask ? "****" : headerValue;
    }

    public static String maskJsonFields(String body, List<String> fields) {
        if (body == null || body.isBlank() || fields == null || fields.isEmpty()) return body;

        String masked = body;
        for (String f : fields) {
            if (f == null || f.isBlank()) continue;
            // Simple JSON string masking: "field":"value" -> "field":"****"
            masked = masked.replaceAll("(\"" + java.util.regex.Pattern.quote(f) + "\"\\s*:\\s*\")([^\"]*)(\")", "$1****$3");
            // Also handle non-string primitives: "field":123 -> "field":****
            masked = masked.replaceAll("(\"" + java.util.regex.Pattern.quote(f) + "\"\\s*:\\s*)([^,}\\]]+)", "$1****");
        }
        return masked;
    }

    public static String headersToString(Map<String, String> headers, List<String> maskedHeaders) {
        if (headers == null || headers.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        headers.forEach((k, v) -> sb.append(k).append(": ").append(maskHeader(k, v, maskedHeaders)).append("\n"));
        return sb.toString();
    }
}
