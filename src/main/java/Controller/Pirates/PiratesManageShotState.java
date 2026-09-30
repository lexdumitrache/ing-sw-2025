package Controller.Pirates;
import Controller.Context;
import Controller.Controller;
import Controller.Enums.ItemType;
import Controller.Exceptions.InvalidContextualAction;
import Controller.Exceptions.InvalidParameters;
import Controller.GamePhases.FlightPhase;
import Controller.State;
import Model.Board.AdventureCards.Projectiles.CannonShot;
import Model.Enums.ConnectorType;
import Model.Enums.Side;
import Model.Exceptions.InvalidMethodParameters;
import Model.Player;
import Model.Ship.Components.BatteryCompartment;
import Model.Ship.Components.Cannon;
import Model.Ship.Components.SpaceshipComponent;
import Model.Ship.Coordinates;

import java.util.List;

/**
 * Manages the resolution of a cannon shot fired by pirates at a special player during the flight phase.
 *
 * <p>This state handles both the damage resolution (destroying components from the ship) and
 * the defensive attempt using shields (powered by batteries). It iterates over special players,
 * applying the shot effects or allowing them to block the shot if conditions are met.</p>
 */
public class PiratesManageShotState extends State{

    int number;
    /**
     * The turn of the player who is to be hit by the cannon shot.
     */
    int turn;
    boolean hit = false;

    /**
     * Constructs a PiratesManageShotState with the specified context, number, and turn.
     *
     * @param context The context providing access to the current game context.
     * @param number  The row or column hit by the cannon shot.
     * @param turn    The turn of the special player who is to be hit by the cannon shot.
     */
    public PiratesManageShotState(Context context, int number, int turn) {
        super(context);
        this.number = number;
        this.turn = turn;
        this.setPlayerInTurn(context.getSpecialPlayers().get(turn));
    }

    /**
     * Resolves the effects of a cannon shot on a player's ship.
     *
     * <p>The method checks the side of the incoming shot and removes ship components
     * along the corresponding row or column. If the ship becomes disconnected,
     * it transitions to a ship integrity check. If not, it proceeds to the next
     * player or shot depending on the situation.</p>
     *
     * @param playerName the name of the player ending their reaction to the shot
     */
    @Override
    public void end(String playerName) throws InvalidMethodParameters, InvalidParameters {
        Controller controller = context.getController();
        Player player = controller.getModel().getPlayer(playerName);
        if (turn != context.getSpecialPlayers().indexOf(player)) {
            
            throw new InvalidParameters("It's not the player's turn");
        }
        CannonShot shot = (CannonShot) context.getProjectile(0);
        if (shot == null) {
            
            throw new InvalidParameters("The shot is null");
        }
        SpaceshipComponent component = null;

        switch (shot.getSide()) {
            case Side.FRONT:   //arriva da davanti
                for (int i = 5; (i <= 9)&&(!hit); i++) {
                    Coordinates coordinates = new Coordinates(i, number);
                    component = player.getShipBoard().getComponent(coordinates);
                    if (component != null) {
                        hit = true;
                        player.getShipBoard().removeComponent(coordinates);
                        player.addJunk();
                    }
                }
            case Side.RIGHT:
                for (int i = 10; (i >= 4)&&(!hit); i--) {
                    Coordinates coordinates = new Coordinates(number, i);
                    component = player.getShipBoard().getComponent(coordinates);
                    if (component != null) {
                        hit = true;
                        player.getShipBoard().removeComponent(coordinates);
                        player.addJunk();
                    }
                }
            case Side.LEFT:
                for (int i = 4; (i <= 10)&&(!hit); i++) {
                    Coordinates coordinates = new Coordinates(number, i);
                    component = player.getShipBoard().getComponent(coordinates);
                    if (component != null) {
                        hit = true;
                        player.getShipBoard().removeComponent(coordinates);
                        player.addJunk();
                    }
                }
            case Side.REAR:
                for (int i = 9; (i >= 5)&&(!hit); i--) {
                    Coordinates coordinates = new Coordinates(i, number);
                    component = player.getShipBoard().getComponent(coordinates);
                    if (component != null) {
                        hit = true;
                        player.getShipBoard().removeComponent(coordinates);
                        player.addJunk();
                    }
                }
        }

        if(hit){
            boolean brokenShip = player.getShipBoard().checkIntegrity();
            if (!brokenShip) {
                controller.getModel().setState(new PiratesCheckShipState(context, number, turn));
                return;
            }
        }
        turn++;
        if (turn > context.getSpecialPlayers().size()) {  //tutti i giocatori sono stati colpiti da questo shot
            context.removeProjectile(shot);
            if (context.getProjectiles().isEmpty()) {     //tutti i colpi sono stati sparati
                controller.getModel().setState(new FlightPhase(controller));
                
                return;
            }
            controller.getModel().setState(new PiratesCannonShotsState(context));
            
        } else {
            controller.getModel().setState(new PiratesManageShotState(context, number, turn));
            
        }

    }

    /**
     * Allows a player to attempt to block an incoming cannon shot using a battery-powered shield.
     *
     * <p>If the shot is small and the ship has an active shield in the shot's direction,
     * the player can use a battery to activate the shield and block the shot.
     * The game then proceeds to the next targeted player or shot.</p>
     *
     * @param playerName the name of the player using an item
     * @param itemType the type of item used (must be a battery)
     * @param coordinates the coordinates of the battery being used
     */
    @Override
    public void useItem(String playerName, ItemType itemType, Coordinates coordinates ) throws InvalidContextualAction, InvalidParameters {
        Controller controller = context.getController();
        if (itemType != ItemType.BATTERIES) {
            
            throw new InvalidParameters("Invalid item type, expected BATTERIES");
        }
        Player player = controller.getModel().getPlayer(playerName);
        if (turn != context.getSpecialPlayers().indexOf(player)) {
            
            throw new InvalidParameters("It's not the player's turn");
        }
        CannonShot shot = (CannonShot) context.getProjectile(0);
        if (shot == null) {
            
            throw new InvalidParameters("The shot is null");
        }
        SpaceshipComponent component = null;
        if(shot.isBig()){
            
            throw new InvalidContextualAction("Cannot use batteries on a big shot");
        }
        boolean shieldFound = false;
        switch (shot.getSide()){
            case Side.FRONT:
                if(player.getShipBoard().getCondensedShip().getShields().getNorthShields() > 0){
                    shieldFound = true;
                }
                break;
            case Side.RIGHT:
                if(player.getShipBoard().getCondensedShip().getShields().getWestShields() > 0){
                    shieldFound = true;
                }
                break;
            case Side.LEFT:
                if(player.getShipBoard().getCondensedShip().getShields().getEastShields() > 0){
                    shieldFound = true;
                }
                break;
            case Side.REAR:
                if(player.getShipBoard().getCondensedShip().getShields().getSouthShields() > 0){
                    shieldFound = true;
                }
                break;
        }

        if(shieldFound) {
            SpaceshipComponent component2 = player.getShipBoard().getComponent(coordinates);
            if(!player.getShipBoard().getCondensedShip().getBatteryCompartments().contains(component2)) {   //non è un Battery
                
                throw new InvalidContextualAction("Invalid component type, expected BatteryCompartment");
            }
            BatteryCompartment compartment = (BatteryCompartment) component2;
            compartment.removeBattery();

            turn++;
            if (turn >= context.getSpecialPlayers().size()) {  //tutti i giocatori sono stati colpiti da questo shot
                context.removeProjectile(shot);
                if (context.getProjectiles().isEmpty()) {     //tutti i colpi sono stati sparati
                    controller.getModel().setState(new FlightPhase(controller));
                    
                } else {
                    controller.getModel().setState(new PiratesCannonShotsState(context));
                    
                }
            } else {
                controller.getModel().setState(new PiratesManageShotState(context, number, turn));
                
            }
        } else {
            
            throw new InvalidParameters("No shield found to use");
        }

    }

    public List<String> getAvailableCommands(){
        return List.of( "End",
                        "UseBattery");
    }

}
