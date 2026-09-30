package Model.Board.AdventureCards;

import Model.AdventureCardOption;
import Model.Board.AdventureCards.Components.CombatZoneLine;
import Model.Board.AdventureCards.Components.Planet;
import Model.Board.AdventureCards.Penalties.CrewPenalty;
import Model.Board.AdventureCards.Penalties.DaysPenalty;
import Model.Board.AdventureCards.Penalties.GoodsPenalty;
import Model.Board.AdventureCards.Projectiles.CannonShot;
import Model.Board.AdventureCards.Projectiles.Meteor;
import Model.Board.AdventureCards.Rewards.Credits;
import Model.Board.AdventureCards.Rewards.Goods;
import Model.Enums.CardLevel;
import Model.Enums.Criteria;
import Model.Enums.Good;
import Model.Enums.Side;
import Model.Utils.CardLevelMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AdventureCardTest {

    @Test
    public void testCardLevelMapper() {
        assertEquals(CardLevel.LEARNER, CardLevelMapper.mapJsonLevelToCardLevel("TRIAL"));
        assertEquals(CardLevel.LEVEL_ONE, CardLevelMapper.mapJsonLevelToCardLevel("LEVEL1"));
        assertEquals(CardLevel.LEVEL_TWO, CardLevelMapper.mapJsonLevelToCardLevel("LEVEL2"));
        assertEquals(CardLevel.LEVEL_THREE, CardLevelMapper.mapJsonLevelToCardLevel("LEVEL3"));
        assertThrows(IllegalArgumentException.class, () -> CardLevelMapper.mapJsonLevelToCardLevel("INVALID"));
    }

    @Test
    public void testOpenSpace() {
        // Test constructor
        OpenSpace card = new OpenSpace(1, CardLevel.LEARNER);
        assertEquals(1, card.getId());
        assertEquals(CardLevel.LEARNER, card.getLevel());
        assertEquals("Spazio Aperto", card.getName());
        assertEquals("", card.getDescription());

        // Test JSON constructor
        JsonObject json = new JsonObject();
        json.addProperty("id", 2);
        json.addProperty("level", "LEARNER");
        json.addProperty("imagePath", "test.png");
        OpenSpace jsonCard = new OpenSpace(json);
        assertEquals(2, jsonCard.getId());
        assertEquals(CardLevel.LEARNER, jsonCard.getLevel());
    }

    @Test
    public void testMeteorSwarm() {
        // Test constructor
        List<Meteor> meteors = new ArrayList<>();
        meteors.add(new Meteor(true, Side.FRONT));
        meteors.add(new Meteor(false, Side.LEFT));
        MeteorSwarm card = new MeteorSwarm(1, CardLevel.LEVEL_TWO, meteors);
        assertEquals(1, card.getId());
        assertEquals(CardLevel.LEVEL_TWO, card.getLevel());
        assertEquals("Pioggia di Meteoriti", card.getName());
        assertEquals("", card.getDescription());

        // Test JSON constructor with meteors array
        JsonObject json = new JsonObject();
        json.addProperty("id", 2);
        json.addProperty("level", "LEVEL_TWO");
        json.addProperty("imagePath", "test.png");
        
        JsonArray meteorsArray = new JsonArray();
        JsonObject meteor1 = new JsonObject();
        meteor1.addProperty("large", true);
        meteor1.addProperty("direction", "front");
        meteorsArray.add(meteor1);
        
        JsonObject meteor2 = new JsonObject();
        meteor2.addProperty("large", false);
        meteor2.addProperty("direction", "left");
        meteorsArray.add(meteor2);
        
        json.add("meteors", meteorsArray);
        
        MeteorSwarm jsonCard = new MeteorSwarm(json);
        assertEquals(2, jsonCard.getId());
        assertEquals(CardLevel.LEVEL_TWO, jsonCard.getLevel());
        
        int count = 0;
        for (Meteor m : jsonCard) {
            count++;
        }
        assertEquals(2, count);
    }

    @Test
    public void testStardust() {
        // Test constructor
        Stardust card = new Stardust(1, CardLevel.LEVEL_TWO);
        assertEquals(1, card.getId());
        assertEquals(CardLevel.LEVEL_TWO, card.getLevel());
        assertEquals("Polvere Stellare", card.getName());
        assertEquals("", card.getDescription());

        // Test JSON constructor
        JsonObject json = new JsonObject();
        json.addProperty("id", 2);
        json.addProperty("level", "LEVEL_TWO");
        json.addProperty("imagePath", "test.png");
        Stardust jsonCard = new Stardust(json);
        assertEquals(2, jsonCard.getId());
        assertEquals(CardLevel.LEVEL_TWO, jsonCard.getLevel());
    }

    @Test
    public void testAbandonedShip() {
        // Test constructor
        AbandonedShip card = new AbandonedShip(1, CardLevel.LEVEL_TWO, 2, 3, 1);
        assertEquals(1, card.getId());
        assertEquals(CardLevel.LEVEL_TWO, card.getLevel());
        assertEquals("Nave Abbandonata", card.getName());
        assertEquals("", card.getDescription());
        assertEquals(2, card.getCrew());
        assertEquals(3, card.getLandingReward().getAmount());
        assertEquals(1, card.getLandingPenalty().getAmount());

        // Test JSON constructor
        JsonObject json = new JsonObject();
        json.addProperty("id", 2);
        json.addProperty("level", "LEVEL_TWO");
        json.addProperty("crewRequired", 3); // Add required field
        json.addProperty("imagePath", "test.png");
        JsonObject reward = new JsonObject();
        reward.addProperty("credits", 4);
        json.add("reward", reward);
        json.addProperty("days", 1);
        AbandonedShip jsonCard = new AbandonedShip(json);
        assertEquals(2, jsonCard.getId());
        assertEquals(CardLevel.LEVEL_TWO, jsonCard.getLevel());
    }

    @Test
    public void testAbandonedStation() {
        // Test constructor
        List<Good> goods = new ArrayList<>();
        goods.add(Good.RED);
        goods.add(Good.BLUE);
        AbandonedStation card = new AbandonedStation(1, CardLevel.LEVEL_TWO, 2, goods, 1);
        assertEquals(1, card.getId());
        assertEquals(CardLevel.LEVEL_TWO, card.getLevel());
        assertEquals("Stazione Abbandonata", card.getName());
        assertEquals("", card.getDescription());
        assertEquals(2, card.getCrew());
        assertEquals(1, card.getLandingPenalty().getAmount());

        // Test JSON constructor
        JsonObject json = new JsonObject();
        json.addProperty("id", 2);
        json.addProperty("level", "LEVEL_TWO");
        json.addProperty("crewRequired", 3); // Add required field
        json.addProperty("imagePath", "test.png");
        JsonObject reward = new JsonObject();
        JsonArray goods2 = new JsonArray();
        goods2.add("RED");
        goods2.add("BLUE");
        goods2.add("GREEN");
        reward.add("goods", goods2); // Use goods instead of cargo
        json.add("reward", reward);
        json.addProperty("days", 1); // Add required days field
        AbandonedStation jsonCard = new AbandonedStation(json);
        assertEquals(2, jsonCard.getId());
        assertEquals(CardLevel.LEVEL_TWO, jsonCard.getLevel());
    }

    @Test
    public void testCombatZone() {
        // Test constructor
        List<CombatZoneLine> lines = new ArrayList<>();
        lines.add(new CombatZoneLine(Criteria.FIRE_POWER, new DaysPenalty(2)));
        CombatZone card = new CombatZone(1, CardLevel.LEVEL_TWO, lines);
        assertEquals(1, card.getId());
        assertEquals(CardLevel.LEVEL_TWO, card.getLevel());
        assertEquals("Zona di Guerra", card.getName());
        assertEquals("", card.getDescription());

        // Test JSON constructor with lines array
        JsonObject json = new JsonObject();
        json.addProperty("id", 2);
        json.addProperty("level", "LEVEL_TWO");
        json.addProperty("imagePath", "test.png");
        
        JsonArray linesArray = new JsonArray();
        JsonObject line = new JsonObject();
        line.addProperty("criteria", "FIRE_POWER");
        JsonObject penalty = new JsonObject();
        penalty.addProperty("type", "DaysPenalty");
        penalty.addProperty("value", 2);
        line.add("penalty", penalty);
        linesArray.add(line);
        json.add("lines", linesArray);
        
        CombatZone jsonCard = new CombatZone(json);
        assertEquals(2, jsonCard.getId());
        assertEquals(CardLevel.LEVEL_TWO, jsonCard.getLevel());
    }

    @Test
    public void testEpidemic() {
        // Test constructor
        Epidemic card = new Epidemic(1, CardLevel.LEVEL_TWO);
        assertEquals(1, card.getId());
        assertEquals(CardLevel.LEVEL_TWO, card.getLevel());
        assertEquals("Epidemia", card.getName());
        assertEquals("", card.getDescription());

        // Test JSON constructor
        JsonObject json = new JsonObject();
        json.addProperty("id", 2);
        json.addProperty("level", "LEVEL_TWO");
        json.addProperty("imagePath", "test.png");
        Epidemic jsonCard = new Epidemic(json);
        assertEquals(2, jsonCard.getId());
        assertEquals(CardLevel.LEVEL_TWO, jsonCard.getLevel());
    }

    @Test
    public void testPlanets() {
        // Test constructor
        List<Planet> planets = new ArrayList<>();
        List<Good> goods = new ArrayList<>();
        goods.add(Good.RED);
        goods.add(Good.YELLOW);
        planets.add(new Planet("Test Planet", goods));
        Planets card = new Planets(1, CardLevel.LEVEL_TWO, 2, planets);
        assertEquals(1, card.getId());
        assertEquals(CardLevel.LEVEL_TWO, card.getLevel());
        assertEquals("Pianeti", card.getName());
        assertEquals("", card.getDescription());

        // Test JSON constructor
        JsonObject json = new JsonObject();
        json.addProperty("id", 2);
        json.addProperty("level", "LEVEL_TWO");
        json.addProperty("imagePath", "test.png");
        
        JsonArray planetsArray = new JsonArray();
        JsonObject planet = new JsonObject();
        planet.addProperty("name", "Test Planet");
        JsonArray goodsArray = new JsonArray();
        goodsArray.add(new JsonPrimitive("RED"));
        goodsArray.add(new JsonPrimitive("BLUE"));
        planet.add("goods", goodsArray);
        planetsArray.add(planet);
        json.add("planets", planetsArray);
        
        JsonObject landingPenalty = new JsonObject();
        landingPenalty.addProperty("value", 2);
        json.add("landingPenalty", landingPenalty);
        
        Planets jsonCard = new Planets(json);
        assertEquals(2, jsonCard.getId());
        assertEquals(CardLevel.LEVEL_TWO, jsonCard.getLevel());
    }

    @Test
    public void testPirates() {
        // Test constructor
        List<CannonShot> shots = new ArrayList<>();
        shots.add(new CannonShot(false, Side.FRONT));
        Pirates card = new Pirates(1, CardLevel.LEVEL_TWO, 4, shots, 2, 3);
        assertEquals(1, card.getId());
        assertEquals(CardLevel.LEVEL_TWO, card.getLevel());
        assertEquals("Pirati", card.getName());
        assertEquals("", card.getDescription());
        assertEquals(4, card.getPower());

        // Test JSON constructor
        JsonObject json = new JsonObject();
        json.addProperty("id", 2);
        json.addProperty("level", "LEVEL_TWO");
        json.addProperty("imagePath", "test.png");
        json.addProperty("power", 5);
        
        JsonObject reward = new JsonObject();
        reward.addProperty("credits", 4);
        json.add("reward", reward);
        
        JsonObject penalty = new JsonObject();
        penalty.addProperty("days", 3);
        JsonArray shots2 = new JsonArray();
        JsonObject shot = new JsonObject();
        shot.addProperty("isLarge", false);
        shot.addProperty("direction", "FRONT");
        shots2.add(shot);
        penalty.add("shots", shots2);
        json.add("penalty", penalty);
        
        Pirates jsonCard = new Pirates(json);
        assertEquals(2, jsonCard.getId());
        assertEquals(CardLevel.LEVEL_TWO, jsonCard.getLevel());
        assertEquals(5, jsonCard.getPower());
    }

    @Test
    public void testSlavers() {
        // Test constructor
        Slavers card = new Slavers(1, CardLevel.LEVEL_TWO, 5, 2, 1, 4);
        assertEquals(1, card.getId());
        assertEquals(CardLevel.LEVEL_TWO, card.getLevel());
        assertEquals("Schiavisti", card.getName());
        assertEquals("", card.getDescription());
        assertEquals(5, card.getPower());

        // Test JSON constructor
        JsonObject json = new JsonObject();
        json.addProperty("id", 2);
        json.addProperty("level", "LEVEL_TWO");
        json.addProperty("imagePath", "test.png");
        json.addProperty("power", 6);
        
        JsonObject reward = new JsonObject();
        reward.addProperty("credits", 5);
        json.add("reward", reward);
        
        JsonObject penalty = new JsonObject();
        penalty.addProperty("crewLoss", 3);
        penalty.addProperty("days", 1);
        json.add("penalty", penalty);
        
        Slavers jsonCard = new Slavers(json);
        assertEquals(2, jsonCard.getId());
        assertEquals(CardLevel.LEVEL_TWO, jsonCard.getLevel());
        assertEquals(6, jsonCard.getPower());
    }

    @Test
    public void testSmugglers() {
        // Test constructor
        List<Good> goods = Arrays.asList(Good.RED, Good.BLUE, Good.GREEN);
        Smugglers card = new Smugglers(1, CardLevel.LEVEL_TWO, 3, 1, 2, goods);
        assertEquals(1, card.getId());
        assertEquals(CardLevel.LEVEL_TWO, card.getLevel());
        assertEquals("Contrabbandieri", card.getName());
        assertEquals("", card.getDescription());
        assertEquals(3, card.getPower());

        // Test JSON constructor
        JsonObject json = new JsonObject();
        json.addProperty("id", 2);
        json.addProperty("level", "LEVEL_TWO");
        json.addProperty("imagePath", "test.png");
        json.addProperty("power", 4);
        
        // Add required penalty field
        JsonObject penalty = new JsonObject();
        penalty.addProperty("stealGoodsOnLoss", 2);
        penalty.addProperty("days", 1);
        JsonArray shots = new JsonArray();
        JsonObject shot = new JsonObject();
        shot.addProperty("isLarge", false);
        shot.addProperty("direction", "FRONT");
        shots.add(shot);
        penalty.add("shots", shots);
        json.add("penalty", penalty);
        
        JsonArray rewardGoods = new JsonArray();
        rewardGoods.add(new JsonPrimitive("RED"));
        rewardGoods.add(new JsonPrimitive("BLUE"));
        rewardGoods.add(new JsonPrimitive("GREEN"));
        json.add("rewardGoods", rewardGoods);
        
        Smugglers jsonCard = new Smugglers(json);
        assertEquals(2, jsonCard.getId());
        assertEquals(CardLevel.LEVEL_TWO, jsonCard.getLevel());
        assertEquals(4, jsonCard.getPower());
    }


}