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

    @NotNull
    private String region;

    @NotNull
    private String ipV4Address;

    public ServerConfig(String version, String secretKey, String region, String ipV4Address) {
        this.version = version;
        this.secretKey = secretKey;
        this.region = region;
        this.ipV4Address = ipV4Address;
    }

    public String getVersion() {
        return version;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public String getRegion() {
        return region;
    }

    public String getIpV4Address() {
        return ipV4Address;
    }
}
