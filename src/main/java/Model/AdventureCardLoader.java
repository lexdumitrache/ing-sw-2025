package Model;

import Model.Board.AdventureCards.AdventureCardFilip;
import Model.Factories.AdventureCardFactory;
import Model.Enums.CardLevel;
import Model.Ship.Components.SpaceshipComponent;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import Controller.Enums.MatchLevel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Loads and builds adventure cards from a unified JSON file.
 * Supports test injection for unit testing.
 */
public class AdventureCardLoader {

    private static List<AdventureCardFilip> testCards = null;
    private static final String ADVENTURE_CARD_PATH = "adventure_cards.json";

    /**
     * Injects test cards for testing purposes.
     * @param cards list of mock or test adventure cards
     */
    public static void setTestCards(List<AdventureCardFilip> cards) {
        testCards = cards;
    }

    /**
     * Clears test cards so normal loading is restored.
     */
    public static void clearTestCards() {
        testCards = null;
    }



    /**
     * Loads all adventure cards from a JSON file using the specified resource path.
     * Cards are built dynamically using AdventureCardFactory.
     *
     * @return list of parsed AdventureCardFilip objects
     */
    public static List<AdventureCardFilip> loadCards() {
        try {
            JsonArray array = JsonResource.readArray(ADVENTURE_CARD_PATH);
            List<AdventureCardFilip> cards = new ArrayList<>();

            for (JsonElement element : array) {
                JsonObject obj = element.getAsJsonObject();
                cards.add(AdventureCardFactory.fromJson(obj));
            }
            return cards;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Loads and filters cards based on the match level from a single JSON file.
     * Builds the deck according to the official Galaxy Trucker rules.
     * Uses testCards if injected.
     *
     * @param level the difficulty level of the match
     * @return list of cards for the selected level
     */
    public static List<AdventureCardFilip> loadAdventureCards(MatchLevel level, boolean shuffle) {
        //if (testCards != null) return testCards;
        
        List<AdventureCardFilip> allCards = loadCards();
        
        if (allCards == null) {
            throw new RuntimeException("Failed to load adventure cards from JSON");
        }

        return switch (level) {
            case TRIAL -> {
                List<AdventureCardFilip>returnList= allCards.stream()
                        .filter(card -> card.getLevel() == CardLevel.LEARNER)
                        .collect(Collectors.toList());
                if (shuffle) {
                    Collections.shuffle(returnList);
                }
                yield returnList;
            }

            case LEVEL2 -> {
                List<AdventureCardFilip> level1Cards = allCards.stream()
                        .filter(card -> card.getLevel() == CardLevel.LEVEL_ONE)
                        .collect(Collectors.toList());


                List<AdventureCardFilip> level2Cards = allCards.stream()
                        .filter(card -> card.getLevel() == CardLevel.LEVEL_TWO)
                        .collect(Collectors.toList());


                List<AdventureCardFilip> combined = new ArrayList<>();
                combined.addAll(level1Cards);
                combined.addAll(level2Cards);

                if (shuffle) {
                    Collections.shuffle(combined);
                }
                yield combined;
            }
        };
    }
}
