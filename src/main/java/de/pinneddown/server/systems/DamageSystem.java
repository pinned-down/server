package de.pinneddown.server.systems;

import de.pinneddown.server.*;
import de.pinneddown.server.components.*;
import de.pinneddown.server.events.CardRemovedEvent;
import de.pinneddown.server.events.DefeatEvent;
import de.pinneddown.server.events.StarshipDamagedEvent;
import de.pinneddown.server.events.StarshipDefeatedEvent;
import de.pinneddown.server.util.PlayerUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

@Component
public class DamageSystem {
    private EventManager eventManager;
    private EntityManager entityManager;
    private BlueprintManager blueprintManager;
    private Random random;
    private PlayerUtils playerUtils;

    private long damageDeckEntityId;
    private HashSet<Long> damageEntities;
    private boolean godModeEnabled;

    public DamageSystem(EventManager eventManager, EntityManager entityManager, BlueprintManager blueprintManager,
                        Random random, PlayerUtils playerUtils) {
        this.eventManager = eventManager;
        this.entityManager = entityManager;
        this.blueprintManager = blueprintManager;
        this.random = random;
        this.playerUtils = playerUtils;

        this.eventManager.addEventHandler(EventType.READY_TO_START, this::onReadyToStart);
        this.eventManager.addEventHandler(EventType.STARSHIP_DEFEATED, this::onStarshipDefeated);
        this.eventManager.addEventHandler(ActionType.GOD_CHEAT, this::onGodCheat);
    }

    private void onReadyToStart(GameEvent gameEvent) {
        damageEntities = new HashSet<>();

        DeckList deckList = getDeckList();
        CardPile attackDeck = CardPile.createFromDecklist(deckList, random);

        damageDeckEntityId = entityManager.createEntity();

        CardPileComponent cardPileComponent = new CardPileComponent();
        cardPileComponent.setCardPile(attackDeck);

        entityManager.addComponent(damageDeckEntityId, cardPileComponent);

        godModeEnabled = false;
    }

    private void onGodCheat(GameEvent gameEvent) {
        godModeEnabled = !godModeEnabled;
    }

    private void onStarshipDefeated(GameEvent gameEvent) {
        StarshipDefeatedEvent eventData = (StarshipDefeatedEvent)gameEvent.getEventData();

        if (godModeEnabled) {
            return;
        }

        // Check if player starship.
        long entityId = eventData.getEntityId();
        OwnerComponent ownerComponent = entityManager.getComponent(entityId, OwnerComponent.class);

        if (ownerComponent == null) {
            return;
        }

        if (!eventData.isOverpowered()) {
            // Create damage.
            CardPileComponent cardPileComponent =
                    entityManager.getComponent(damageDeckEntityId, CardPileComponent.class);

            if (cardPileComponent.getCardPile().isEmpty()) {
                cardPileComponent.getDiscardPile().shuffleInto(cardPileComponent.getCardPile(), random);
            }

            String damageBlueprintId = cardPileComponent.getCardPile().pop();
            long damageEntityId = blueprintManager.createEntity(damageBlueprintId);
            damageEntities.add(damageEntityId);

            // Add damage.
            AttachmentComponent attachmentComponent = new AttachmentComponent(damageEntityId, entityId);
            entityManager.addComponent(damageEntityId, attachmentComponent);

            StructureComponent starshipStructureComponent =
                    entityManager.getComponent(entityId, StructureComponent.class);
            StructureComponent damageStructureComponent =
                    entityManager.getComponent(damageEntityId, StructureComponent.class);

            starshipStructureComponent.setStructureModifier(starshipStructureComponent.getStructureModifier() +
                    damageStructureComponent.getStructureModifier());

            // Notify listeners.
            StarshipDamagedEvent starshipDamagedEvent =
                    new StarshipDamagedEvent(entityId, damageEntityId, damageBlueprintId);
            eventManager.queueEvent(EventType.STARSHIP_DAMAGED, starshipDamagedEvent);

            // Check structure.
            if (starshipStructureComponent.getCurrentStructure() <= 0) {
                destroyStarship(entityId);
            }
        } else {
            // Destory immediately.
            destroyStarship(entityId);
        }
    }

    private DeckList getDeckList() {
        DeckList deckList = new DeckList();
        HashMap<String, Integer> cards = new HashMap<>();

        cards.put("DirectHit", 10);

        deckList.setCards(cards);
        return deckList;
    }

    private void destroyStarship(long entityId) {
        // Remove all damage.
        CardPileComponent cardPileComponent =
                entityManager.getComponent(damageDeckEntityId, CardPileComponent.class);

        for (long otherDamageEntityId : damageEntities) {
            AttachmentComponent otherAttachmentComponent = new AttachmentComponent(otherDamageEntityId, entityId);

            if (otherAttachmentComponent.getAttachedTo() == entityId) {
                BlueprintComponent blueprintComponent = entityManager.getComponent(otherDamageEntityId, BlueprintComponent.class);
                cardPileComponent.getDiscardPile().push(blueprintComponent.getBlueprintId());

                eventManager.queueEvent(EventType.CARD_REMOVED, new CardRemovedEvent(otherDamageEntityId));
                entityManager.removeEntity(otherDamageEntityId);
            }
        }

        // Destroy starship.
        OwnerComponent ownerComponent = entityManager.getComponent(entityId, OwnerComponent.class);
        BlueprintComponent blueprintComponent = entityManager.getComponent(entityId, BlueprintComponent.class);

        playerUtils.addCardToDiscardPile(ownerComponent.getOwner(), blueprintComponent.getBlueprintId());

        // Check defeat condition.
        GameplayTagsComponent gameplayTagsComponent = entityManager.getComponent(entityId, GameplayTagsComponent.class);

        if (gameplayTagsComponent != null &&
                gameplayTagsComponent.getInitialGameplayTags().contains(GameplayTags.KEYWORD_FLAGSHIP)) {
            DefeatEvent defeatEvent = new DefeatEvent(DefeatReason.FLAGSHIP_DESTROYED, entityId);
            eventManager.queueEvent(EventType.DEFEAT, defeatEvent);
            return;
        }

        // Remove entity.
        eventManager.queueEvent(EventType.CARD_REMOVED, new CardRemovedEvent(entityId));
        entityManager.removeEntity(entityId);
    }
}
