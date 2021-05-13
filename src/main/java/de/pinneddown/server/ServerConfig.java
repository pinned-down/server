package de.pinneddown.server;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@ConstructorBinding
@ConfigurationProperties("de.pinneddown.server")
@Validated
public class ServerConfig {
    @NotNull
    private String secretKey;

    public ServerConfig(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getSecretKey() {
        return secretKey;
    }
}
