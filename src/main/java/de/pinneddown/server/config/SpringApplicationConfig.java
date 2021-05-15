package de.pinneddown.server.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@ConstructorBinding
@ConfigurationProperties("spring.application")
@Validated
public class SpringApplicationConfig {
    @NotNull
    private String name;

    public SpringApplicationConfig(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
