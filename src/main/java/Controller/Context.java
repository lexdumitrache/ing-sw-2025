package Controller;

import Controller.Exceptions.InvalidParameters;
import Model.Board.AdventureCards.*;
import Model.Board.AdventureCards.Components.CombatZoneLine;
import Model.Board.AdventureCards.Components.Planet;
import Model.Board.AdventureCards.Penalties.*;
import Model.Board.AdventureCards.Projectiles.Meteor;
import Model.Board.AdventureCards.Projectiles.Projectile;
import Model.Enums.CardLevel;
import Model.Enums.Good;
import Model.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
 * The Context class represents the current state of the game, including players, resources, and actions.
 * It is used to manage the game flow and provide information to the controller.
 * It works as the dynamic variant of the card that can change based on the current game situation.
 * This class is serializable to allow saving and restoring game states.
 */
public class Context {
    private Controller controller;
    /**
     * List of players in the game.
     * This list is initialized from the controller's flight board turn order.
     */
    private List<Player> players;
    /**
     * List of some players depending on the Card.
     * This list is initialized as empty and can be modified during the game.
     */
    private List<Player> specialPlayers;

    /**
     * Represents the number of crewmates, power, required goods, and other resources.
     * These values are initialized based on the specific card being played.
     */
    private int crewmates;
    private int power;
    private int requiredGoods;
    /**
     * Represents the default amount of resources, such as goods or crewmates,
     * that can be used in various game scenarios, usually for enemies.
     */
    private int defaultAmount;
    private List<Good> goods;
    private List<Projectile> projectiles;
    private List<Planet> planets;
    private int credits;
    private int daysLost;
    private int diceNumber;
    private Runnable visual;
    /**
     * List of strings to render the visual representation of the context.
     * This is used to display information about the current game state.
     */
    private List<String> render;



    /**
     * Constructs a Context with the given controller, unique for each card.
     * Initializes the players list from the controller's flight board turn order.
     *
     * @param controller the controller managing the game
     */

    public Context(Controller controller) {
        this.controller = controller;
        this.players = new ArrayList<>(Arrays.asList(controller.getModel().getFlightBoard().getTurnOrder()));
        this.specialPlayers = new ArrayList<>();
        this.render = new ArrayList<>();

    }

    /**
     * Constructs a Context for the AbandonedShip card.
     * Initializes crewmates, credits, daysLost, and visual representation.
     *
     * @param controller the controller managing the game
     * @param card the AbandonedShip card being played
     */
    public Context(Controller controller, AbandonedShip card) {
        this(controller);
        this.crewmates = card.getWinPenalty().getAmount();
        this.credits = card.getLandingReward().getAmount();
        this.daysLost = card.getLandingPenalty().getAmount();
        this.visual = () -> {
            this.render.add("Abandoned Ship");
            this.render.add("Crew to lose: " + this.crewmates);
            //winpenalty generica serve dire che e in giorni etc...
            this.render.add("Credits: " + this.credits);
            this.render.add("Days: " + this.daysLost);
            this.render.add("---------------------------------------");
        };
        this.visual.run();

    }

    /**
     * Constructs a Context for the AbandonedStation card.
     * Initializes crewmates, goods, daysLost, and visual representation.
     *
     * @param controller the controller managing the game
     * @param card the AbandonedStation card being played
     */
    public Context(Controller controller, AbandonedStation card) {
        this(controller);
        this.crewmates = card.getCrew();
        this.goods = new ArrayList<>();
        for(Good good : card.getLandingReward()) {
            this.goods.add(good);
        }
        this.daysLost = card.getLandingPenalty().getAmount();
        this.visual = () -> {
            this.render.add("Abandoned Station");
            this.render.add("Crew required: " + this.crewmates);
            //winpenalty generica serve dire che e in giorni etc...
            String s = "Goods:";
            for(Good good : this.goods) {
                s += " " + good;
            }
            this.render.add(s);
            this.render.add("");
            this.render.add("Days: " + this.daysLost);
            this.render.add("---------------------------------------");
        };
        this.visual.run();

    }

    /**
     * Constructs a Context for the MeteorSwarm card.
     * Initializes projectiles, diceNumber, and visual representation.
     *
     * @param controller the controller managing the game
     * @param card the AsteroidField card being played
     */
    public Context(Controller controller, MeteorSwarm card) {
        this(controller);
        this.projectiles = new ArrayList<>();
        for(Meteor meteor : card) {
            this.projectiles.add(meteor);
        }
        this.visual = () -> {
            this.render.add("Meteor Swarm");
            this.render.add("Total Meteors:  " + projectiles.size());
            this.render.add("Details:");
            for (int i = 0; i < projectiles.size(); i++) {
                Projectile m = projectiles.get(i);
                this.render.add("  #" + (i + 1) + " → large=" + m.isBig() + ", dir=" + m.getSide());
            }
            this.render.add("");
            this.render.add("Dices Result : " + this.diceNumber);
            this.render.add("---------------------------------------");
        };
        this.visual.run();

    }

    /**
     * Constructs a Context for the OpenSpace card.
     * Initializes visual representation.
     *
     * @param controller the controller managing the game
     * @param card the OpenSpace card being played
     */
    public Context(Controller controller, OpenSpace card) {
        this(controller);
        this.visual = () -> {
            this.render.add("Open Space");
            this.render.add("---------------------------------------");
        };
        this.visual.run();

    }

    /**
     * Constructs a Context for the Pirates card.
     * Initializes credits, daysLost, projectiles, power, and visual representation.
     *
     * @param controller the controller managing the game
     * @param card the Pirates card being played
     */
    public Context(Controller controller, Pirates card) {
        this(controller);
        this.credits = card.getWinReward().getAmount();
        this.daysLost = card.getWinPenalty().getAmount();
        this.projectiles = new ArrayList<>();
        for (Projectile projectile : card.getLossPenalty()) {
            this.projectiles.add(projectile);
        }
        this.power = card.getPower();
        this.visual = () -> {
            this.render.add("Pirates");
            this.render.add("Power required: " + this.power);
            this.render.add("Total Shots:  " + projectiles.size());
            this.render.add("Details:");
            for (int i = 0; i < projectiles.size(); i++) {
                Projectile m = projectiles.get(i);
                this.render.add("  #" + (i + 1) + " → large=" + m.isBig() + ", dir=" + m.getSide());
            }
            this.render.add("");
            this.render.add("Dices Result : " + this.diceNumber);
            this.render.add("Credits: " + this.credits);
            this.render.add("Days: " + this.daysLost);
            this.render.add("---------------------------------------");
        };
        this.visual.run();

    }

    /**
     * Constructs a Context for the Planets card.
     * Initializes planets, daysLost, and visual representation.
     *
     * @param controller the controller managing the game
     * @param card the Planets card being played
     */
    public Context(Controller controller, Planets card) {
        this(controller);
        this.daysLost = card.getLandingPenalty().getAmount();
        this.planets = new ArrayList<>();
        for (Planet planet : card) {
            this.planets.add(planet);
        }

        this.visual = () -> {
            for(Planet planet : planets) {
                String s = "Planet: " + planet.getName();
                for(Good good : planet.getLandingReward()) {
                    s += " " + good;
                }
                this.render.add(s);
                this.render.add("");
                this.render.add("Days Lost: " + this.daysLost);
                this.render.add("---------------------------------------");
            }
        };
        this.visual.run();

    }

    /**
     * Constructs a Context for the Slavers card.
     * Initializes credits, daysLost, crewmates, power, and visual representation.
     *
     * @param controller the controller managing the game
     * @param card the Slavers card being played
     */
    public Context(Controller controller, Slavers card) {
        this(controller);
        this.credits = card.getWinReward().getAmount();
        this.daysLost = card.getWinPenalty().getAmount();
        this.crewmates = card.getLossPenalty().getAmount();
        this.defaultAmount = card.getLossPenalty().getAmount();
        this.power = card.getPower();
        this.visual = () -> {
            this.render.add("Slavers");
            this.render.add("Power required: " + this.power);
            this.render.add("Crewmates to lose: " + this.crewmates);
            this.render.add("Credits: " + this.credits);
            this.render.add("Days: " + this.daysLost);
            this.render.add("---------------------------------------");
        };
        this.visual.run();

    }

    /**
     * Constructs a Context for the Smugglers card.
     * Initializes goods, daysLost, power, requiredGoods, and visual representation.
     *
     * @param controller the controller managing the game
     * @param card the Smugglers card being played
     */
    public Context(Controller controller, Smugglers card) {
        this(controller);
        this.goods = new ArrayList<>();
        for (Good good : card.getWinReward()) {
            this.goods.add(good);
        }
        this.daysLost = card.getWinPenalty().getAmount();
        this.power = card.getPower();
        this.requiredGoods = card.getLossPenalty().getAmount();
        this.defaultAmount = card.getLossPenalty().getAmount();
        this.visual = () -> {
            this.render.add("Smugglers");
            this.render.add("Power required: " + this.power);
            this.render.add("Goods to lose: " + this.requiredGoods);
            String s = "Available goods:";
            for(Good good : this.goods) {
                s += " " + good;
            }
            this.render.add(s);
            this.render.add("");
            this.render.add("Days: " + this.daysLost);
            this.render.add("---------------------------------------");
        };
        this.visual.run();

    }

    /**
     * Constructs a Context for the CombatZone card.
     * Initializes daysLost, crewmates, projectiles, and visual representation based on the card level.
     *
     * @param controller the controller managing the game
     * @param card the CombatZone card being played
     * @throws InvalidParameters if the card level is invalid
     */
    public Context(Controller controller, CombatZone card) throws InvalidParameters {
        this(controller);
        if( card.getLevel() == CardLevel.LEARNER){
            int i = 0;
            this.projectiles = new ArrayList<>();
            for(CombatZoneLine line : card) {
                switch (i){
                    case 0 -> {
                        this.daysLost = ((DaysPenalty) line.getPenalty()).getAmount();
                        i++;
                        break;
                    }
                    case 1 -> {
                        this.crewmates = ((CrewPenalty) line.getPenalty()).getAmount();
                        i++;
                        break;
                    }
                    case 2 -> {
                        for (Projectile projectile : ((CannonShotPenalty) line.getPenalty())) {
                            this.projectiles.add(projectile);
                        }
                        i++;
                        break;
                    }
                    default -> throw new InvalidParameters("Invalid Combat Zone Line index: " + i);
                }

            }
            this.visual = () -> {
                this.render.add("Combat Zone Level One");
                this.render.add("Lowest Crew loses: " + this.daysLost + " days");
                this.render.add("Lowest Engine Power loses: " + this.crewmates + " crewmates");
                this.render.add("Lowest Fire Power gets hit by: ");
                for (int j = 0; j < projectiles.size(); j++) {
                    Projectile m = projectiles.get(j);
                    this.render.add("  #" + (j + 1) + " → large=" + m.isBig() + ", dir=" + m.getSide());
                }
                this.render.add("");
                this.render.add("Dices Result : " + this.diceNumber);
                this.render.add("---------------------------------------");
            };
        } else if( card.getLevel() == CardLevel.LEVEL_TWO) {
            int i = 0;
            this.projectiles = new ArrayList<>();
            for(CombatZoneLine line : card) {
                switch (i){
                    case 0 -> {
                        this.daysLost = ((DaysPenalty) line.getPenalty()).getAmount();
                        i++;
                        break;
                    }
                    case 1 -> {
                        this.requiredGoods = ((GoodsPenalty) line.getPenalty()).getAmount();
                        i++;
                        break;
                    }
                    case 2 -> {
                        for (Projectile projectile : ((CannonShotPenalty) line.getPenalty())) {
                            this.projectiles.add(projectile);
                        }
                        i++;
                        break;
                    }
                    default -> throw new InvalidParameters("Invalid Combat Zone Line index: " + i);
                }

            }
            this.visual = () -> {
                this.render.add("Combat Zone Level Two");
                this.render.add("Lowest Fire Power loses: " + this.daysLost + " days");
                this.render.add("Lowest Engine Power loses: " + this.requiredGoods+ " goods");
                this.render.add("Lowest Crew gets hit by: ");
                for (int j = 0; j < projectiles.size(); j++) {
                    Projectile m = projectiles.get(j);
                    this.render.add("  #" + (j + 1) + " → large=" + m.isBig() + ", dir=" + m.getSide());
                }
                this.render.add("");
                this.render.add("Dices Result : " + this.diceNumber);
                this.render.add("---------------------------------------");
            };
        } else {
            throw new InvalidParameters("Invalid card level for CombatZone: " + card.getLevel());
        }
        this.visual.run();


    }

    public void render(){
        visual.run();
    }

    public Controller getController() {
        return controller;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }

    public List<Player> getSpecialPlayers() {
        return specialPlayers;
    }

    public void addSpecialPlayer(Player player) {
        if (specialPlayers == null) {
            specialPlayers = new ArrayList<>();
        }
        specialPlayers.add(player);
    }

    public void removeSpecialPlayer(Player player) {
        if (specialPlayers != null) {
            specialPlayers.remove(player);
        }
    }


    public int getCrewmates() {
        return crewmates;
    }

    public void setCrewmates(int crewmates) {
        this.crewmates = crewmates;
    }

    public void removeCrewmate() {
        if (crewmates > 0) {
            crewmates--;
        }
    }

    public List<Good> getGoods() {
        return goods;
    }

    public void setGoods(List<Good> goods) {
        this.goods = goods;
    }


    public void removeGood( Good good) {
        if (goods != null) {
            goods.remove(good);
        }
    }


    public Planet getPlanet(String name) {
        for (Planet planet : planets) {
            if (planet.getName().equals(name)) {
                return planet;
            }
        }
        return null;
    }

    public List<Planet> getPlanets() {
        return planets;
    }



    public int getCredits() {
        return credits;
    }


    public int getDaysLost() {
        return daysLost;
    }

    public int getRequiredGoods() {
        return requiredGoods;
    }

    public void setRequiredGoods(int requiredGoods) {
        this.requiredGoods = requiredGoods;
    }

    public void removeRequiredGood() {
        if (requiredGoods > 0) {
            requiredGoods--;
        }
    }


    public int getPower() {
        return power;
    }

    public Projectile getProjectile(int index){
        if (projectiles != null && index >= 0 && index < projectiles.size()) {
            return projectiles.get(index);
        }
        return null;
    }

    public List<Projectile> getProjectiles() {
        return projectiles;
    }

    public void removeProjectile(Projectile projectile) {
        if (projectiles != null) {
            projectiles.remove(projectile);
        }
    }

    public int getDiceNumber() {
        return diceNumber;
    }
    public void setDiceNumber(int diceNumber) {
        this.diceNumber = diceNumber;
    }

    public int getDefaultAmount() {
        return this.defaultAmount;
    }

    public List<String> getRender() {
        this.render = new ArrayList<>();

        if(visual != null) visual.run();

        return this.render;
    }
}
