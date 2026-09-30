package Controller.Commands;

import Controller.Controller;
import Controller.Enums.DoubleType;
import Controller.Exceptions.InvalidCommand;
import Controller.Exceptions.InvalidContextualAction;
import Controller.Exceptions.InvalidParameters;
import Model.Exceptions.InvalidMethodParameters;

import java.util.List;
import java.util.Map;

/**
 * Command for declaring the use of double components (engines or cannons).
 * Players declare how many double components they want to activate.
 */
public class DeclaresDoubleCommand extends Command {
    /** The type of double component being declared */
    private final DoubleType doubleType;
    /** The amount of double components to activate */
    private final double amount;
    
    /**
     * Constructs a new DeclaresDoubleCommand.
     *
     * @param playerName the name of the player making the declaration
     * @param doubleType the type of double component
     * @param amount the amount to declare
     */
    public DeclaresDoubleCommand(String playerName, DoubleType doubleType, double amount) {
        super(playerName);
        this.doubleType = doubleType;
        this.amount = amount;
    }
    
    /**
     * Executes the declares double command by calling the controller's declaresDouble method.
     *
     * @param controller the controller to execute the command on
     */
    @Override
    public void execute(Controller controller) throws InvalidCommand, InvalidParameters, InvalidMethodParameters, InvalidContextualAction {
        controller.declaresDouble(getPlayerName(), doubleType, amount);
    }
    
    /**
     * Gets the double type.
     *
     * @return the double type
     */
    public DoubleType getDoubleType() {
        return doubleType;
    }
    
    /**
     * Gets the amount.
     *
     * @return the amount
     */
    public double getAmount() {
        return amount;
    }

    public static CommandConstructor getCannonConstructor() {
        return new CommandConstructor() {
            @Override
            public Command create(String username, Map<String, String> args) throws IllegalArgumentException {
                final Double amount;
                try{
                    amount = Double.parseDouble(args.get("amount"));
                } catch (NumberFormatException e){
                    throw new IllegalArgumentException("Could not parse the amount. Did you provide an Integer?");
                }

                return new DeclaresDoubleCommand(username, DoubleType.CANNONS ,amount);
            }

            @Override
            public List<String> getArguments() {
                return List.of("amount");
            }
        };
    }

    public static CommandConstructor getEngineConstructor() {
        return new CommandConstructor() {
            @Override
            public Command create(String username, Map<String, String> args) throws IllegalArgumentException {
                final Double amount;
                try{
                    amount = Double.parseDouble(args.get("amount"));
                } catch (NumberFormatException e){
                    throw new IllegalArgumentException("Could not parse the amount. Did you provide an Double?");
                }

                return new DeclaresDoubleCommand(username, DoubleType.ENGINES, amount);
            }

            @Override
            public List<String> getArguments() {
                return List.of("amount");
            }
        };
    }
}