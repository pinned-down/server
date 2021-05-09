package de.pinneddown.server.services;

import de.opengamebackend.quests.model.requests.IncreaseQuestProgressRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.ApplicationListener;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class QuestService extends BackendService implements ApplicationListener<ApplicationReadyEvent> {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public QuestService(DiscoveryClient discoveryClient, HttpHeaders httpHeaders) {
        super(discoveryClient, httpHeaders);
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        // Get service.
        discoverService("open-game-backend-quests");
    }

    public void increaseQuestProgress(String playerId, String questDefinitionId, int progressMade) {
        if (!hasService()) {
            return;
        }

        IncreaseQuestProgressRequest request = new IncreaseQuestProgressRequest();
        request.setProgressMade(progressMade);

        sendRequest("/server/increasequestprogress/" + playerId + "/" + questDefinitionId, request);

        logger.info("Increased quest {} progress of player {} by {}.", questDefinitionId, playerId, progressMade);
    }
}
