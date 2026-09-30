package Controller.CombatZone.Level_ONE;

import Controller.Context;
import Controller.Controller;
import Controller.Enums.DoubleType;
import Controller.Exceptions.InvalidContextualAction;
import Controller.Exceptions.InvalidParameters;
import Controller.GamePhases.FlightPhase;
import Controller.State;
import Model.Board.AdventureCards.Components.CombatZoneLine;
import Model.Player;

import java.util.List;

/**
 * Represents the state in which a player declares engine power during the combat zone phase of the game.
 *
 * <p>This state allows a player to declare the amount of engine power they wish to use,
 * and if the declaration is valid, it transitions to the next state for battery removal or crew removal.</p>
 */
public class CombatZone1EngineDeclarationState extends State {
    /**
     * The declared worst engine power among the players, used to determine the player with the lowest power.
     */
    private double worst;

    public CombatZone1EngineDeclarationState(Context context) {
        super(context);
        this.worst = -1;
        this.setPlayerInTurn(context.getPlayers().getFirst());
    }

    public CombatZone1EngineDeclarationState(Context context, double worst) {
        super(context);
        this.worst = worst;
        this.setPlayerInTurn(context.getPlayers().getFirst());
    }

    /**
     * Called when a player enters this state, checking if there is only one player left in the turn order.
     * If so, it transitions to the flight phase of the game.
     */
    @Override
    public void onEnter() {
        Controller controller = context.getController();
        if(controller.getModel().getFlightBoard().getTurnOrder().length == 1){
            controller.getModel().setState(new FlightPhase(controller));
            
        }
    }

    /**
     * Allows a player to declare their total engine during the combat zone phase.
     * Validates the double type, amount, and player's turn before proceeding with the declaration.
     *
     * <p>If the declaration is valid, it transitions to the next state for battery removal or crew removal.</p>
     *
     * @param playerName the name of the player declaring the engine power
     * @param doubleType the type of double engine being declared (must be {@code DoubleType.ENGINES})
     * @param amount the amount of engine power being declared
     * @throws InvalidContextualAction if it is not the player's turn to declare engine power
     * @throws InvalidParameters if the double type is invalid or the amount is out of bounds
     */
    @Override
    public void declaresDouble(String playerName, DoubleType doubleType, double amount) throws InvalidContextualAction, InvalidParameters {
        Controller controller = context.getController();
        if(doubleType != DoubleType.ENGINES){
            
            throw new InvalidParameters("Invalid double type, only ENGINES are allowed");
        }

        if(amount < 0){
            
            throw new InvalidParameters("Invalid amount of double, only non negative integers are allowed");
        }

        Player player = controller.getModel().getPlayer(playerName);
        if(!player.equals(context.getPlayers().getFirst())) {
            
            throw new InvalidParameters("It's not your turn to throw the dice.");
        }


        int batteries = 0;
        double minPower = player.getShipBoard().getCondensedShip().getBaseThrust();
        double maxPower = player.getShipBoard().getCondensedShip().getMaxThrust();

        if (amount < minPower || amount > maxPower) {
            
            throw new InvalidParameters("Declared amount is out of bounds");
        }


        if ((amount % 1) != (minPower % 1)) {
            
            throw new InvalidParameters("Declared amount must match the ship's base power decimal part");
        }

        int delta = (int) (amount - minPower);

        if(delta % 2 != 0) {
            
            throw new InvalidParameters("Cannot reach the declared amount with double engines");
        }

        int doubleRequired = delta / 2;

        if(player.getShipBoard().getCondensedShip().getEngines().getDoubleEngines()>=doubleRequired){
            batteries = doubleRequired;
        } else {
            
            throw new InvalidParameters("Not enough double engines to declare this amount");
        }



        if(worst < 0){
            if(amount == player.getShipBoard().getCondensedShip().getBaseThrust()){
                context.addSpecialPlayer(player);
                context.removePlayer(player);
                if(context.getPlayers().isEmpty()){
                    controller.getModel().setState(new CombatZone1CrewRemovalState(context));
                    
                } else {
                    controller.getModel().setState(new CombatZone1EngineDeclarationState(context, worst));
                    
                }
            } else {
                controller.getModel().setState(new CombatZone1_E_BatteryRemovalState(context, amount, batteries));
                
            }
        } else {
            if (amount == player.getShipBoard().getCondensedShip().getBaseThrust()) {
                if(amount < worst){
                    context.removeSpecialPlayer(context.getSpecialPlayers().getFirst());
                    context.addSpecialPlayer(player);
                    worst = amount;
                }
                context.removePlayer(player);
                if(context.getPlayers().isEmpty()){
                    controller.getModel().setState(new CombatZone1CrewRemovalState(context));
                    
                } else {
                    controller.getModel().setState(new CombatZone1EngineDeclarationState(context, worst));
                    
                }
            } else {
                controller.getModel().setState(new CombatZone1_E_BatteryRemovalState(context, amount, batteries, worst));
                
            }

        }
    }

    public List<String> getAvailableCommands(){
        return List.of(
            "DeclareEnginePower"
        );
    }
}
