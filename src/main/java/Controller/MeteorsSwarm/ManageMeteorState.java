package Controller.MeteorsSwarm;

import Controller.Context;
import Controller.Controller;
import Controller.Enums.ItemType;
import Controller.Exceptions.InvalidContextualAction;
import Controller.Exceptions.InvalidParameters;
import Controller.GamePhases.FlightPhase;
import Controller.State;
import Model.Board.AdventureCards.Projectiles.Meteor;
import Model.Enums.ConnectorType;
import Model.Enums.Direction;
import Model.Enums.Side;
import Model.Exceptions.InvalidMethodParameters;
import Model.Player;
import Model.Ship.Components.BatteryCompartment;
import Model.Ship.Components.Cannon;
import Model.Ship.Components.SpaceshipComponent;
import Model.Ship.Coordinates;

import java.util.List;

/**
 * Game state that handles the impact of a meteor on a player's spaceship.
 * This state checks if a component of the ship is hit, whether it is protected
 * by cannons or shields, and processes any resulting damage.
 */
public class ManageMeteorState extends State {
    /**
     * The context of the game, which contains the current state and player information.
     */

    /** The row or column index (depending on the meteor's direction) targeted by the meteor. */
    private final int number;

    /** Flag indicating whether a component of the ship has been hit. */
    private boolean hit = false;

    /**
     * Constructs a new ManageMeteorState.
     *
     * @param context the game context
     * @param number the index (row or column) that the meteor will impact
     */
    public ManageMeteorState(Context context, int number) {
        super(context);
        this.number = number;
        this.setPlayerInTurn(context.getSpecialPlayers().getFirst());
    }

    /**
     * Handles the logic when the player decides not to do anything during their turn.
     * It checks whether the meteor hits any component of the ship, processes
     * the damage, and transitions to the next appropriate game state.
     *
     * @param playerName the name of the player whose turn is ending
     */
    @Override
    public void end(String playerName) throws InvalidMethodParameters, InvalidParameters {
        Controller controller = context.getController();
        Player player = controller.getModel().getPlayer(playerName);
        if (!player.equals(context.getSpecialPlayers().getFirst())) {
            
            throw new InvalidMethodParameters("Player " + playerName + " is not in turn.");
        }

        Meteor meteor = (Meteor) context.getProjectile(0);
        if (meteor == null) {
            
            throw new InvalidParameters("Meteors are empty");
        }

        SpaceshipComponent component = null;
        switch (meteor.getSide()) {
            case Side.FRONT:   //arriva da davanti
                for (int i = 5; i <= 9; i++) {
                    Coordinates coordinates = new Coordinates(i, number);
                    component = player.getShipBoard().getComponent(coordinates);
                    if (component != null) {
                        hit = true;
                        break;
                    }
                }
                break;

            case Side.RIGHT:
                for (int i = 10; i >= 4; i--) {
                    Coordinates coordinates = new Coordinates(number, i);
                    component = player.getShipBoard().getComponent(coordinates);
                    if (component != null) {
                        hit = true;
                        break;
                    }
                }
                break;

            case Side.LEFT:
                for (int i = 4; i <= 10; i++) {
                    Coordinates coordinates = new Coordinates(number, i);
                    component = player.getShipBoard().getComponent(coordinates);
                    if (component != null) {
                        hit = true;
                        break;
                    }
                }
                break;

            case Side.REAR:
                for (int i = 9; i >= 4; i--) {
                    Coordinates coordinates = new Coordinates(i, number);
                    component = player.getShipBoard().getComponent(coordinates);
                    if (component != null) {
                        hit = true;
                        break;
                    }
                }
                break;

            default:
                break;
        }
        boolean cannonFound = false;

        // if he gets hit by a meteor, check if he has a cannon that can be used to protect the ship (in case of a big meteor), or a shield (in case of a small meteor)
        if(hit) {
            if (meteor.isBig()) {

                switch (meteor.getSide()) {
                    case Side.FRONT:
                        for (int i = 5; i <= 9; i++) {
                            SpaceshipComponent component2 = player.getShipBoard().getComponent(new Coordinates(i, number));
                            if (component2 != null && player.getShipBoard().getCondensedShip().getCannons().contains(component2)) {  //se è un cannone...
                                Cannon cannon = (Cannon) component2;
                                //il cannone è singolo perchè non vuole usare batterie.
                                if ((!cannon.isDouble() && cannon.getOrientation() == Direction.UP)) {
                                    cannonFound = true;
                                    break;
                                }
                            }
                        }
                        break;
                    case Side.RIGHT:
                        for (int i = 10; i >= 4; i--) {
                            SpaceshipComponent component2 = player.getShipBoard().getComponent(new Coordinates(number, i));
                            if (component2 != null && player.getShipBoard().getCondensedShip().getCannons().contains(component2)) {  //se è un cannone...
                                Cannon cannon = (Cannon) component2;
                                if (!cannon.isDouble() && cannon.getOrientation() == Direction.RIGHT) {
                                    cannonFound = true;
                                    break;
                                }
                            }
                            component2 = player.getShipBoard().getComponent(new Coordinates(number + 1, i));
                            if (component2 != null && player.getShipBoard().getCondensedShip().getCannons().contains(component2)) {  //se è un cannone...
                                Cannon cannon = (Cannon) component2;
                                if (!cannon.isDouble() && cannon.getOrientation() == Direction.RIGHT) {
                                    cannonFound = true;
                                    break;
                                }
                            }
                            component2 = player.getShipBoard().getComponent(new Coordinates(number - 1, i));
                            if (component2 != null && player.getShipBoard().getCondensedShip().getCannons().contains(component2)) {  //se è un cannone...
                                Cannon cannon = (Cannon) component2;
                                if (!cannon.isDouble() && cannon.getOrientation() == Direction.RIGHT) {
                                    cannonFound = true;
                                    break;
                                }
                            }
                        }
                        break;
                    case Side.LEFT:
                        for (int i = 4; i <= 10; i++) {
                            SpaceshipComponent component2 = player.getShipBoard().getComponent(new Coordinates(number, i));
                            if (component2 != null && player.getShipBoard().getCondensedShip().getCannons().contains(component2)) {  //se è un cannone...
                                Cannon cannon = (Cannon) component2;
                                if (!cannon.isDouble() && cannon.getOrientation() == Direction.LEFT) {
                                    cannonFound = true;
                                    break;
                                }
                            }
                            component2 = player.getShipBoard().getComponent(new Coordinates(number + 1, i));
                            if (component2 != null && player.getShipBoard().getCondensedShip().getCannons().contains(component2)) {  //se è un cannone...
                                Cannon cannon = (Cannon) component2;
                                if (!cannon.isDouble() && cannon.getOrientation() == Direction.LEFT) {
                                    cannonFound = true;
                                    break;
                                }
                            }
                            component2 = player.getShipBoard().getComponent(new Coordinates(number - 1, i));
                            if (component2 != null && player.getShipBoard().getCondensedShip().getCannons().contains(component2)) {  //se è un cannone...
                                Cannon cannon = (Cannon) component2;
                                if (!cannon.isDouble() && cannon.getOrientation() == Direction.LEFT) {
                                    cannonFound = true;
                                    break;
                                }
                            }
                        }
                        break;
                    case Side.REAR:
                        for (int i = 9; i >= 4; i--) {
                            SpaceshipComponent component2 = player.getShipBoard().getComponent(new Coordinates(i, number));
                            if (component2 != null && player.getShipBoard().getCondensedShip().getCannons().contains(component2)) {  //se è un cannone...
                                Cannon cannon = (Cannon) component2;
                                if (!cannon.isDouble() && cannon.getOrientation() == Direction.DOWN) {
                                    cannonFound = true;
                                    break;
                                }
                            }

                        }
                        break;

                    default:
                        break;
                }
            }
        }
        /// Se è grande e non si trova un cannone singolo, oppure se è piccolo e il lato non è liscio (c'era un bug mon ci entrava quando c'era meteorite piccolo con connettore esposto, e annessa rimozione del componente)
        if(hit && ((meteor.isBig() && !cannonFound) || (!meteor.isBig() &&  (component != null && component.getConnectorAt(meteor.getSide()) != ConnectorType.NONE)))){
            player.getShipBoard().removeComponent(player.getShipBoard().getIndex(component));
            player.addJunk();
            boolean validShip = player.getShipBoard().checkIntegrity();
            if (!validShip) {
                controller.getModel().setState(new MeteorsCheckShipState(context, number));
                
                return;
            }
        }

        context.removeSpecialPlayer(player);
        if (context.getSpecialPlayers().isEmpty()) {  //tutti i giocatori sono stati colpiti da questo shot
            context.removeProjectile(meteor);
            if (context.getProjectiles().isEmpty()) {     //tutti i colpi sono stati sparati
                controller.getModel().setState(new FlightPhase(controller));
                
            } else {
                controller.getModel().setState(new MeteorsState(context));
                
            }
        } else {
            controller.getModel().setState(new ManageMeteorState(context,number));
            
        }


    }

    /**
     * Handles the use of an item during the meteor state. Specifically processes
     * the use of a battery to activate a double cannon or shield to block a meteor.
     *
     * @param playerName the name of the player using the item
     * @param itemType the type of item used (must be BATTERIES)
     * @param coordinates the coordinates where the item is used (must be a battery compartment)
     */
    @Override
    public void useItem(String playerName, ItemType itemType, Coordinates coordinates ) throws InvalidParameters, InvalidContextualAction {
        if (itemType != ItemType.BATTERIES) {
            return; // Handle the case where the item is not a cannon shot
        }
        Controller controller = context.getController();
        Player player = controller.getModel().getPlayer(playerName);
        if (player != context.getSpecialPlayers().getFirst()) {
            return; // Handle the case where it's not the player's turn
        }
        Meteor meteor = (Meteor) context.getProjectile(0);
        if (meteor == null) {
            return; // Handle the case where there are no projectiles
        }
        SpaceshipComponent component = null;

        boolean cannonOrShieldFound = false;
        switch (meteor.getSide()){
            case Side.FRONT:
                if(meteor.isBig()){
                    for (int i = 5; i <= 9; i++){
                        component = player.getShipBoard().getComponent(new Coordinates(i, number));
                        if(player.getShipBoard().getCondensedShip().getCannons().contains(component)){  //se è un cannone...
                            Cannon cannon = (Cannon) component;
                            cannonOrShieldFound = cannon.isDouble();   //... e se non è doppio
                            if(!cannon.isDouble()){
                                break;
                            }
                        }
                    }
                } else {
                    if(player.getShipBoard().getCondensedShip().getShields().getNorthShields() > 0){
                        cannonOrShieldFound = true;
                    }
                }

                break;
            case Side.RIGHT:
                if(meteor.isBig()){
                    for (int i = 10; i >= 4; i--){
                        component = player.getShipBoard().getComponent(new Coordinates(i, number));
                        if(player.getShipBoard().getCondensedShip().getCannons().contains(component)){  //se è un cannone...
                            Cannon cannon = (Cannon) component;
                            cannonOrShieldFound = cannon.isDouble();   //... e se non è doppio
                            if(!cannon.isDouble()){
                                break;
                            }
                        }
                    }
                } else {
                    if(player.getShipBoard().getCondensedShip().getShields().getEastShields() > 0){
                        cannonOrShieldFound = true;
                    }
                }
                break;
            case Side.LEFT:
                if(meteor.isBig()){
                    for (int i = 4; i <= 10; i++){
                        component = player.getShipBoard().getComponent(new Coordinates(i, number));
                        if(player.getShipBoard().getCondensedShip().getCannons().contains(component)){  //se è un cannone...
                            Cannon cannon = (Cannon) component;
                            cannonOrShieldFound = cannon.isDouble();   //... e se non è doppio
                            if(!cannon.isDouble()){
                                break;
                            }
                        }
                    }
                } else {
                    if(player.getShipBoard().getCondensedShip().getShields().getWestShields() > 0){
                        cannonOrShieldFound = true;
                    }
                }
                break;
            case Side.REAR:
                if(meteor.isBig()){
                    for (int i = 9; i >= 4; i--){
                        component = player.getShipBoard().getComponent(new Coordinates(i, number));
                        if(player.getShipBoard().getCondensedShip().getCannons().contains(component)){  //se è un cannone...
                            Cannon cannon = (Cannon) component;
                            cannonOrShieldFound = cannon.isDouble();   //... e se non è doppio
                            if(!cannon.isDouble()){
                                break;
                            }
                        }
                    }
                } else {
                    if(player.getShipBoard().getCondensedShip().getShields().getSouthShields() > 0){
                        cannonOrShieldFound = true;
                    }
                }
                break;

            default:
                break;
        }

        if(cannonOrShieldFound) {
            SpaceshipComponent component2 = player.getShipBoard().getComponent(coordinates);
            if(!player.getShipBoard().getCondensedShip().getBatteryCompartments().contains(component2)) {   //non è un Battery
                
                throw new InvalidParameters("The component is not a BatteryCompartment");
            }
            BatteryCompartment compartment = (BatteryCompartment) component2;
            compartment.removeBattery();

            context.removeSpecialPlayer(player);
            if (context.getSpecialPlayers().isEmpty()) {  //tutti i giocatori sono stati colpiti da questo shot
                context.removeProjectile(meteor);
                if (context.getProjectiles().isEmpty()) {     //tutti i colpi sono stati sparati
                    controller.getModel().setState(new FlightPhase(controller));
                    return;
                }
                controller.getModel().setState(new MeteorsState(context));
            } else {
                controller.getModel().setState(new ManageMeteorState(context, number));
            }
        } else {
            
            throw new InvalidParameters("No shield or cannon found to use");
        }
    }

    public List<String> getAvailableCommands(){
        return List.of(
            "End",
            "UseBattery"
        );
    }
}
