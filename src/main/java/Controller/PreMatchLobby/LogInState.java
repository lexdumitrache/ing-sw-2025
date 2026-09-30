package Controller.PreMatchLobby;

import Controller.Controller;
import Controller.Exceptions.*;
import Controller.RealTimeBuilding.BuildingState;
import Controller.State;
import Model.Game;
import Model.Player;

import java.util.List;

/**
 * Represents the LogInState of the game, where players can log in and manage their connection to the game.
 * This state handles player login, logout, and starting the game.
 */
public class LogInState extends State {

//    @Override
//    public void execute(Map<String, Object> command, Controller controller) {
//        // Handle the lobby phase actions here
//        String action = (String) command.get("action");
//        switch (action) {
//            case "PlayerConnected":
//                String playerName = (String) command.get("playerName");
//                Game game = controller.getModel();
//                game.addPlayer(playerName); //il controllo è gia nel model
//                break;
//            case "StartConstruction":
//                if(controller.getModel().getPlayers().size() == 4)
//                    controller.getModel().setState(new BuildingState());
//                break;
//
//            case "Testing":
//                System.out.println("Testing Command");
//                System.out.println("Name: " + command.get("playerName"));
//                System.out.println("Age: " + command.get("Age"));
//                break;
//            default:
//                //do nothing
//                break;
//        }
//    }


    public LogInState(Controller controller){
        super(controller);
    }



    /**
     * Allows a player to log in to the game.
     * If the player is already connected or the game is full, an exception is thrown.
     *
     * @param name The name of the player logging in.
     * @throws InvalidParameters If the player with the given name is already connected or if the game is full.
     * @throws InvalidCommand If the command is invalid in the current context.
     */
    @Override
    public void login(String name) throws InvalidParameters, InvalidCommand{

        if(name==null){
            throw new InvalidParameters("Name cannot be null");
        }

        Player test= this.getController().getModel().getPlayer(name);
        if(test != null){
            throw new InvalidParameters("Player with this name already connected");
        }
        if(this.getController().getModel().getPlayers().size() == 4){
            throw new InvalidParameters("Game is full");
        }

        this.getController().getModel().addPlayer(name);

        // Handle the login action here
        System.out.println("Player " + name + " logged in to game " + this.getController().getGameID());
        // You can add more logic here if needed
    }

    /**
     * Allows a player to log out from the game.
     * If the player is not connected, an exception is thrown.
     *
     * @param name The name of the player logging out.
     * @throws InvalidParameters If the player with the given name is not connected.
     */
    @Override
    public void logout(String name) throws InvalidParameters {
        final Game model = this.getController().getModel();

        Player removed_player = model.getPlayer(name);
        if(removed_player == null){
            throw new InvalidParameters("Player with this name not connected");
        }
        model.removePlayer(name);
        if(model.getPlayers().isEmpty()){
            model.setState(new OffState(this.getController()));
        }
        System.out.println("Player " + name + " logged out");
    }

    /**
     * Starts the game if the player is the admin and there are enough players.
     * If the player is not the admin or there are not enough players, an exception is thrown.
     *
     * @param name The name of the player starting the game.
     * @throws InvalidParameters If the player is not the admin.
     * @throws InvalidCommand If there are not enough players to start the game.
     */
    @Override
    public void startGame(String name) throws InvalidParameters, InvalidCommand {
        Player admin= this.getController().getModel().getPlayers().get(0);
        if(!admin.getName().equals(name)){
            throw new InvalidParameters("Only the admin can start the game");
        }

        if(this.getController().getModel().getPlayers().size() < 2){
            throw new InvalidCommand("Not enough players to start the game");
        }

        this.getController().getModel().setState(new BuildingState(this.getController()));

        System.out.println("Game started by admin " + name);
        // You can add more logic here if needed
    }

    /**
     * Returns a list of available commands for the LogInState.
     * These commands include "Leave" and "StartGame".
     *
     * @return List of available commands.
     */
    public List<String> getAvailableCommands(){
        return List.of("Leave", "StartGame");
    }
}
