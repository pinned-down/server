package de.pinneddown.server;

import de.pinneddown.server.config.ServerConfig;
import de.pinneddown.server.config.SpringApplicationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Profile("!test")
public class LogApplicationVersion {
    private SpringApplicationConfig springApplicationConfig;
    private ServerConfig serverConfig;

    private Logger logger = LoggerFactory.getLogger(LogApplicationVersion.class);

    @Autowired
    public LogApplicationVersion(SpringApplicationConfig springApplicationConfig, ServerConfig applicationConfig) {
        this.springApplicationConfig = springApplicationConfig;
        this.serverConfig = applicationConfig;
    }

    @PostConstruct
    public void postConstruct() {
        logger.info(springApplicationConfig.getName() + " " + serverConfig.getVersion());
    }
}
