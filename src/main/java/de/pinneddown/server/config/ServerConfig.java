package de.pinneddown.server.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@ConstructorBinding
@ConfigurationProperties("de.pinneddown.server")
@Validated
public class ServerConfig {
    @NotNull
    private String version;

    @NotNull
    private String secretKey;

    public ServerConfig(String version, String secretKey) {
        this.version = version;
        this.secretKey = secretKey;
    }

    public String getVersion() {
        return version;
    }

    public String getSecretKey() {
        return secretKey;
    }
}
