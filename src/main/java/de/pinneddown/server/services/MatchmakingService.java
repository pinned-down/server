package de.pinneddown.server.services;

import com.google.common.base.Strings;
import de.opengamebackend.matchmaking.model.ServerStatus;
import de.opengamebackend.matchmaking.model.requests.*;
import de.opengamebackend.matchmaking.model.responses.*;
import de.pinneddown.server.PlayerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.net.URI;

@Component
@DependsOn({"AuthService"})
public class MatchmakingService implements ApplicationListener<ApplicationReadyEvent> {
    private final BackendServiceInterface serviceInterface;
    private final PlayerManager playerManager;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private URI serviceUri;
    private String serverId;

    @Value("${server.port}")
    private String serverPort;

    @Autowired
    public MatchmakingService(BackendServiceInterface serviceInterface, PlayerManager playerManager) {
        this.serviceInterface = serviceInterface;
        this.playerManager = playerManager;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        // Get matchmaking service.
        serviceUri = serviceInterface.discoverService("open-game-backend-matchmaking").orElse(null);

        if (serviceUri == null) {
            return;
        }

        // Build request.
        String version = "0.1";
        String gameMode = "PD";
        int maxPlayers = playerManager.getMaxPlayers();
        String region = "EU";
        String ipV4Address = "localhost";

        int port = 0;
        try {
            port = Integer.parseInt(serverPort);
        } catch (NumberFormatException e) {
            logger.error(e.toString());
        }

        ServerRegisterRequest request = new ServerRegisterRequest
                (version, gameMode, region, ipV4Address, port, maxPlayers);
        ServerRegisterResponse response = serviceInterface.sendRequest
                (serviceUri,"/server/register", request, ServerRegisterResponse.class);

        serverId = response.getId();

        logger.info("Registered server: " + serverId);
    }

    @PreDestroy
    private void preDestroy() {
        if (!isInitialized()) {
            return;
        }

        ServerDeregisterRequest request = new ServerDeregisterRequest(serverId);
        ServerDeregisterResponse response = serviceInterface.sendRequest
                (serviceUri, "/server/deregister", request, ServerDeregisterResponse.class);

        logger.info("Unregistered server: " + response.getRemovedId());

        serverId = null;
    }

    @Scheduled(fixedRate = 120000)
    public void sendHeartbeat() {
        if (!isInitialized()) {
            return;
        }

        ServerSendHeartbeatRequest request = new ServerSendHeartbeatRequest(serverId);
        ServerSendHeartbeatResponse response = serviceInterface.sendRequest
                (serviceUri, "/server/sendHeartbeat", request, ServerSendHeartbeatResponse.class);

        logger.info("Sent heartbeat for: " + response.getUpdatedId());
    }

    public String notifyPlayerJoined(String ticket) {
        if (!isInitialized()) {
            return null;
        }

        ServerNotifyPlayerJoinedRequest request = new ServerNotifyPlayerJoinedRequest();
        request.setServerId(serverId);
        request.setTicket(ticket);

        ServerNotifyPlayerJoinedResponse response = serviceInterface.sendRequest
                (serviceUri, "/server/notifyPlayerJoined", request, ServerNotifyPlayerJoinedResponse.class);

        logger.info("Player joined: " + response.getPlayerId());

        return response.getPlayerId();
    }

    public void notifyPlayerLeft(String playerId) {
        if (!isInitialized()) {
            return;
        }

        ServerNotifyPlayerLeftRequest request = new ServerNotifyPlayerLeftRequest();
        request.setServerId(serverId);
        request.setPlayerId(playerId);

        serviceInterface.sendRequest
                (serviceUri, "/server/notifyPlayerLeft", request, ServerNotifyPlayerLeftResponse.class);

        logger.info("Player left: " + playerId);
    }

    public void setServerStatus(ServerStatus status) {
        if (!isInitialized()) {
            return;
        }

        ServerSetStatusRequest request = new ServerSetStatusRequest();
        request.setId(serverId);
        request.setStatus(status);

        serviceInterface.sendRequest(serviceUri, "/server/setStatus", request, ServerSetStatusResponse.class);

        logger.info("Status changed: " + status);
    }

    private boolean isInitialized() {
        return !Strings.isNullOrEmpty(serverId);
    }
}
