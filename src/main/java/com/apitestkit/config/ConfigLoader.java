
package com.apitestkit.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.apitestkit.utils.Preconditions;
import com.apitestkit.utils.ResourceUtils;

import java.io.InputStream;

public final class ConfigLoader {

    private static final ObjectMapper YAML_MAPPER = new ObjectMapper(new YAMLFactory());

    private ConfigLoader() {}

    public static ApiTestKitConfig load(String env) {
        Preconditions.notBlank(env, "env must not be blank");
        String resourceName = "application-" + env.toLowerCase() + ".yml";

        try (InputStream is = ResourceUtils.resourceAsStream(resourceName)) {
            ApiTestKitConfig cfg = YAML_MAPPER.readValue(is, ApiTestKitConfig.class);
            if (cfg.getEnvironment() == null || cfg.getEnvironment().isBlank()) {
                cfg.setEnvironment(env.toLowerCase());
            }
            Preconditions.notBlank(cfg.getBaseUrl(), "baseUrl must be provided in " + resourceName);
            return cfg;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load config: " + resourceName + ". Error: " + e.getMessage(), e);
        }
    }
}
