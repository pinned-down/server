package de.pinneddown.server;

import com.google.common.base.Strings;
import de.opengamebackend.matchmaking.model.requests.*;
import de.opengamebackend.matchmaking.model.responses.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.ApplicationListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PreDestroy;
import java.net.URI;
import java.util.List;

@Component
public class MatchmakingService implements ApplicationListener<ApplicationReadyEvent> {
    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private HttpHeaders httpHeaders;

    @Autowired
    private PlayerManager playerManager;

    private Logger logger = LoggerFactory.getLogger(ServerApplication.class);

    private URI matchmakingUri;
    private String serverId;

    @Value("${server.port}")
    private String serverPort;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        // Get matchmaking service.
        List<ServiceInstance> instances = this.discoveryClient.getInstances("open-game-backend-matchmaking");

        if (instances.isEmpty()) {
            logger.error("Unable to connect to matchmaking service.");
            return;
        }

        ServiceInstance instance = instances.get(0);
        matchmakingUri = instance.getUri();

        logger.info("Found " + instance.getServiceId() + " at " + matchmakingUri);

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
        ServerRegisterResponse response =
                sendRequest("/server/register", request, ServerRegisterResponse.class);

        serverId = response.getId();

        logger.info("Registered server: " + serverId);
    }

    @PreDestroy
    private void preDestroy() {
        if (!isInitialized()) {
            return;
        }

        ServerDeregisterRequest request = new ServerDeregisterRequest(serverId);
        ServerDeregisterResponse response =
                sendRequest("/server/deregister", request, ServerDeregisterResponse.class);

        logger.info("Unregistered server: " + response.getRemovedId());

        serverId = null;
    }

    @Scheduled(fixedRate = 120000)
    public void sendHeartbeat() {
        if (!isInitialized()) {
            return;
        }

        ServerSendHeartbeatRequest request = new ServerSendHeartbeatRequest(serverId);
        ServerSendHeartbeatResponse response =
                sendRequest("/server/sendHeartbeat", request, ServerSendHeartbeatResponse.class);

        logger.info("Sent heartbeat for: " + response.getUpdatedId());
    }

    public void notifyPlayerJoined(String ticket) {
        if (!isInitialized()) {
            return;
        }

        ServerNotifyPlayerJoinedRequest request = new ServerNotifyPlayerJoinedRequest();
        request.setServerId(serverId);
        request.setTicket(ticket);

        ServerNotifyPlayerJoinedResponse response =
                sendRequest("/server/notifyPlayerJoined", request, ServerNotifyPlayerJoinedResponse.class);

        logger.info("Player joined: " + response.getPlayerId());
    }

    public void notifyPlayerLeft(String playerId) {
        if (!isInitialized()) {
            return;
        }

        ServerNotifyPlayerLeftRequest request = new ServerNotifyPlayerLeftRequest();
        request.setServerId(serverId);
        request.setPlayerId(playerId);

        ServerNotifyPlayerLeftResponse response =
                sendRequest("/server/notifyPlayerLeft", request, ServerNotifyPlayerLeftResponse.class);

        logger.info("Player left: " + playerId);
    }

    private boolean isInitialized() {
        return matchmakingUri != null && !Strings.isNullOrEmpty(serverId);
    }

    private <TRequest, TResponse> TResponse sendRequest(String relativeUri, TRequest request, Class<TResponse> responseClass) {
        HttpEntity<TRequest> httpEntity = new HttpEntity<>(request, httpHeaders);
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.postForObject(matchmakingUri + relativeUri, httpEntity, responseClass);
    }
}
