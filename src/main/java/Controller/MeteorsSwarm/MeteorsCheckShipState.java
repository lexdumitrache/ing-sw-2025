package Controller.MeteorsSwarm;

import Controller.CombatZone.Level_ONE.CombatZone1CannonShotsState;
import Controller.CombatZone.Level_ONE.CombatZone1CheckShipState;
import Controller.Context;
import Controller.Controller;
import Controller.Exceptions.InvalidParameters;
import Controller.GamePhases.FlightPhase;
import Controller.State;
import Model.Board.AdventureCards.Projectiles.CannonShot;
import Model.Board.AdventureCards.Projectiles.Meteor;
import Model.Exceptions.InvalidMethodParameters;
import Model.Player;
import Model.Ship.Components.SpaceshipComponent;
import Model.Ship.Coordinates;

import java.util.List;

/**
 * Represents the state in which a player checks their ship's integrity
 * after being hit by meteors during the meteors swarm phase of the game.
 *
 * <p>This state allows a player to remove components from their ship
 * and checks if the ship remains intact after the removal.</p>
 */
public class MeteorsCheckShipState extends State {

    /** Holds the number thrown by the dice*/
    private final int number;

    public MeteorsCheckShipState(Context context, int number) {
        super(context);
        this.number = number;
        this.setPlayerInTurn(context.getSpecialPlayers().getFirst());
    }

    /**
     * Deletes a component from the player's ship at the specified coordinates.
     * <p>
     * Validates that it is the correct player's turn, that the coordinates are not null,
     * and that a component exists at those coordinates. If all checks pass, the component is removed,
     * and the player receives junk. The game state is then updated based on the ship's integrity.
     *
     * @param playerName the name of the player attempting to delete a component
     * @param coordinates the coordinates of the component to be deleted
     * @throws InvalidMethodParameters if any input is null or structurally invalid
     * @throws InvalidParameters if it is not the player's turn or if no component exists at the given coordinates
     */
    @Override
    public void deleteComponent(String playerName, Coordinates coordinates) throws InvalidMethodParameters, InvalidParameters {
        Controller controller = context.getController();
        Player player = controller.getModel().getPlayer(playerName);
        if(!player.equals(context.getSpecialPlayers().getFirst())) {
            
            throw new InvalidParameters("It's not the player's turn");
        }
        if(coordinates == null){
            
            throw new InvalidParameters("Coordinates cannot be null");
        }
        SpaceshipComponent component = player.getShipBoard().getComponent(coordinates);
        if(component == null) {
            
            throw new InvalidParameters("No component found at the given coordinates");
        }

        player.getShipBoard().removeComponent(coordinates);
        player.addJunk();

        if(player.getShipBoard().checkIntegrity()){
            Meteor meteor = (Meteor) context.getProjectile(0);
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
        } else {
            controller.getModel().setState(new MeteorsCheckShipState(context, number));
            
        }
    }

    public List<String> getAvailableCommands(){
        return List.of( "DeleteComponent");
    }
}
