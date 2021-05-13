package de.pinneddown.server.services;

import de.opengamebackend.quests.model.requests.IncreaseQuestProgressRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
@DependsOn({"AuthService"})
public class QuestService implements ApplicationListener<ApplicationReadyEvent> {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final BackendServiceInterface serviceInterface;

    private URI serviceUri;

    @Autowired
    public QuestService(BackendServiceInterface serviceInterface) {
        this.serviceInterface = serviceInterface;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        // Get service.
        serviceUri = serviceInterface.discoverService("open-game-backend-quests").orElse(null);
    }

    public void increaseQuestProgress(String playerId, String questDefinitionId, int progressMade) {
        if (serviceUri == null) {
            return;
        }

        IncreaseQuestProgressRequest request = new IncreaseQuestProgressRequest();
        request.setProgressMade(progressMade);

        serviceInterface.sendRequest
                (serviceUri, "/server/increasequestprogress/" + playerId + "/" + questDefinitionId, request);

        logger.info("Increased quest {} progress of player {} by {}.", questDefinitionId, playerId, progressMade);
    }
}
