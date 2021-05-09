package de.pinneddown.server.systems;

import de.pinneddown.server.*;
import de.pinneddown.server.services.QuestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QuestProgressSystem {
    private final PlayerManager playerManager;
    private final QuestService questService;

    @Autowired
    public QuestProgressSystem(EventManager eventManager, PlayerManager playerManager, QuestService questService) {
        this.playerManager = playerManager;
        this.questService = questService;

        eventManager.addEventHandler(EventType.VICTORY, this::onVictory);
    }

    private void onVictory(GameEvent gameEvent) {
        for (Player player : playerManager.getPlayers()) {
            questService.increaseQuestProgress(player.getPlayerId(), "ThreeWins", 1);
        }
    }
}
