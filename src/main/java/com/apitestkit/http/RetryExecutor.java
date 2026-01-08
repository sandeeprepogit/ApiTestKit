
package com.apitestkit.http;

import com.apitestkit.config.RetryConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

public final class RetryExecutor {

    private static final Logger log = LoggerFactory.getLogger(RetryExecutor.class);

    private RetryExecutor() {}

    public static <T> T execute(RetryConfig cfg, Callable<T> action, java.util.function.Predicate<T> shouldRetry) {
        if (cfg == null || !cfg.isEnabled()) {
            try { return action.call(); }
            catch (Exception e) { throw wrap(e); }
        }

        long delay = cfg.getBaseDelayMs();
        int max = Math.max(1, cfg.getMaxAttempts());

        for (int attempt = 1; attempt <= max; attempt++) {
            try {
                T result = action.call();
                if (attempt < max && shouldRetry.test(result)) {
                    log.warn("Retry attempt {}/{} after {} ms", attempt, max, delay);
                    sleep(delay);
                    delay = nextDelay(cfg, delay);
                    continue;
                }
                return result;
            } catch (Exception e) {
                if (attempt >= max) throw wrap(e);
                log.warn("Retry attempt {}/{} due to exception: {}", attempt, max, e.toString());
                sleep(delay);
                delay = nextDelay(cfg, delay);
            }
        }
        throw new IllegalStateException("Retry loop ended unexpectedly");
    }

    private static long nextDelay(RetryConfig cfg, long current) {
        long next = (long) Math.ceil(current * cfg.getMultiplier());
        return Math.min(next, cfg.getMaxDelayMs());
    }

    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Retry interrupted", ie);
        }
    }

    private static RuntimeException wrap(Exception e) {
        return (e instanceof RuntimeException) ? (RuntimeException) e : new RuntimeException(e);
    }
}
