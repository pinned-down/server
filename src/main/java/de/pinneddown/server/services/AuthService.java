package de.pinneddown.server.services;

import de.opengamebackend.auth.model.AuthRole;
import de.opengamebackend.auth.model.requests.LoginRequest;
import de.opengamebackend.auth.model.responses.AuthTokenResponse;
import de.pinneddown.server.ServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component("AuthService")
public class AuthService implements ApplicationListener<ApplicationReadyEvent> {
    private final BackendServiceInterface serviceInterface;
    private final ServerConfig config;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public AuthService(BackendServiceInterface serviceInterface, ServerConfig config) {
        this.serviceInterface = serviceInterface;
        this.config = config;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        // Get matchmaking service.
        URI serviceUri = serviceInterface.discoverService("open-game-backend-auth").orElse(null);

        if (serviceUri == null) {
            return;
        }

        // Send request.
        LoginRequest request = new LoginRequest();
        request.setProvider("server");
        request.setRole(AuthRole.ROLE_SERVER.name());
        request.setKey(config.getSecretKey());

        AuthTokenResponse response = serviceInterface.sendRequest
                (serviceUri,"/login", request, AuthTokenResponse.class);
        serviceInterface.setAuthToken(response.getToken());

        logger.info("Authentication successful.");
    }
}
