package Controller.CombatZone.Level_ONE;

import Controller.Context;
import Controller.Controller;
import Controller.Enums.ItemType;
import Controller.Exceptions.InvalidContextualAction;
import Controller.Exceptions.InvalidParameters;
import Controller.GamePhases.FlightPhase;
import Controller.State;
import Model.Board.AdventureCards.Components.CombatZoneLine;
import Model.Board.AdventureCards.Projectiles.CannonShot;
import Model.Enums.Side;
import Model.Exceptions.InvalidMethodParameters;
import Model.Player;
import Model.Ship.Components.BatteryCompartment;
import Model.Ship.Components.SpaceshipComponent;
import Model.Ship.Coordinates;

import java.util.List;

/**
 * Represents the state in which a player manages a cannon shot during the combat zone.
 * <p>
 * This state allows a player to handle the effects of a cannon shot on their ship's components,
 * and to use batteries to mitigate damage from the shot.
 * </p>
 */
public class CombatZone1ManageShotState extends State {
    private final int number;
    boolean hit = false;

    public CombatZone1ManageShotState(Context context,  int number) {
        super(context);
        this.number = number;
        this.setPlayerInTurn(context.getSpecialPlayers().getFirst());
    }

    /**
     * Handles the end of a cannon shot by checking the player's ship for damage without using any batteries.
     * <p>
     * Validates that it is the correct player's turn, checks if the shot is valid,
     * and processes the shot's effects on the ship's components.
     * If a component is hit, it is removed and junk is added to the player's inventory.
     * If the ship remains intact, transitions to the next state; otherwise, updates the game state accordingly.
     *
     * @param playerName the name of the player whose ship is being checked
     * @throws InvalidMethodParameters if any input is null or structurally invalid
     * @throws InvalidParameters if it is not the player's turn or if no component is hit
     */
    @Override
    public void end(String playerName) throws InvalidMethodParameters, InvalidParameters {
        Controller controller = context.getController();
        Player player = controller.getModel().getPlayer(playerName);
        if (!player.equals(context.getSpecialPlayers().getFirst())) {
            
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
                for (int i = 4; (i <= 10&&(!hit)); i++) {
                    Coordinates coordinates = new Coordinates(number, i);
                    component = player.getShipBoard().getComponent(coordinates);
                    if (component != null) {
                        hit = true;
                        player.getShipBoard().removeComponent(coordinates);
                        player.addJunk();
                    }
                }
            case Side.REAR:
                for (int i = 9; (i >= 5 &&(!hit)); i--) {
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
                controller.getModel().setState(new CombatZone1CheckShipState(context));
                
                return;
            }
        }
            context.removeProjectile(shot);
            if (context.getProjectiles().isEmpty()) {     //tutti i colpi sono stati sparati
                controller.getModel().setState(new FlightPhase(controller));
                
            } else {
                controller.getModel().setState(new CombatZone1CannonShotsState(context));
                
            }

    }


    /**
     * Uses a battery to mitigate the effects of a cannon shot.
     * <p>
     * Validates that it is the correct player's turn, checks if the item type is BATTERIES,
     * and verifies that a shield exists on the ship. If all checks pass, the battery is used,
     * and the game state is updated accordingly.
     *
     * @param playerName the name of the player using the item
     * @param itemType the type of item being used (expected to be BATTERIES)
     * @param coordinates the coordinates of the battery compartment
     * @throws InvalidContextualAction if batteries cannot be used on a big shot or no shield is found
     * @throws InvalidParameters if it is not the player's turn or if an invalid item type is provided
     */
    @Override
    public void useItem(String playerName, ItemType itemType, Coordinates coordinates ) throws InvalidContextualAction, InvalidParameters {
        Controller controller = context.getController();
        if (itemType != ItemType.BATTERIES) {
            
            throw new InvalidParameters("Invalid item type, expected BATTERIES");
        }
        Player player = controller.getModel().getPlayer(playerName);
        if (player != context.getSpecialPlayers().getFirst()) {
            
            throw new InvalidParameters("It's not the player's turn");
        }
        CannonShot shot = (CannonShot) context.getProjectile(0);
        if (shot == null) {
            
            throw new InvalidParameters("The shot is null");
        }
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
                
                throw new InvalidParameters("The component is not a BatteryCompartment");
            }
            BatteryCompartment compartment = (BatteryCompartment) component2;
            compartment.removeBattery();


                context.removeProjectile(shot);
                if (context.getProjectiles().isEmpty()) {     //tutti i colpi sono stati sparati
                    controller.getModel().setState(new FlightPhase(controller));
                    
                    return;
                }
                controller.getModel().setState(new CombatZone1CannonShotsState(context));
                

        } else {
            
            throw new InvalidParameters("No shield found to use");
        }

    }

    public List<String> getAvailableCommands(){
        return List.of(
            "End",
            "UseBattery"
        );
    }
}
