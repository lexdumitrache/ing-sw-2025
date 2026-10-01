package Controller.GamePhases;

import Model.Board.FlightBoard;
import Controller.Controller;
import Controller.Exceptions.InvalidContextualAction;
import Controller.Exceptions.InvalidParameters;
import Controller.State;
import Model.Board.AdventureCards.AdventureCardFilip;
import Model.Board.CardDeck;
import Model.Exceptions.InvalidMethodParameters;
import Model.Player;
import Controller.CardResolverVisitor;
import View.Client.ClientState;

import java.util.List;

/**
 * Represents the Flight Phase of the game, where players take turns to resolve adventure cards
 * and manage their actions during the flight.
 */
public class FlightPhase extends State {

    public FlightPhase(Controller controller) {
        super(controller);
        controller.setQueuedAction(ClientState::net_Fly);
    }

    /**
     * Checks if any player has to do an early landing.
     */
    @Override
    public void onEnter() {
        final FlightBoard flightBoard = this.getController().getModel().getFlightBoard();
        final Player[] flying = flightBoard.getTurnOrder();
        final CardDeck deck = flightBoard.getUpcomingCardDeck();

        // ships without humans and lapped ships leave the flight
        for(Player p: flying){
            if(p.getShipBoard().getCondensedShip().getTotalHumans() == 0 || flightBoard.getDubbedPlayers().contains(p)){
                flightBoard.removePlayingPlayer(p);
            }
        }

        // the flight is over when nobody is flying any more or no card is left.
        // (A flight without a card deck only exists in unit tests: with nobody flying from the start, there is nothing to end.)
        final boolean nobodyFlying = flightBoard.getTurnOrder().length == 0 && (flying.length > 0 || deck != null);
        final boolean noCardsLeft = deck != null && deck.peekCards().isEmpty();
        if(nobodyFlying || noCardsLeft){
            // entered once: RewardsPhase awards the final credits
            this.getController().getModel().setState(new RewardsPhase(this.getController()));
        }
    }

    /**
     * Allows a player to pick the next adventure card from the deck.
     * The player must be the one whose turn it is to pick a card.
     *
     * @param playerName The name of the player picking the card.
     * @throws InvalidContextualAction If the action is not valid in the current context.
     * @throws InvalidParameters If the parameters provided are invalid.
     * @throws InvalidMethodParameters If the method parameters are invalid.
     */
    @Override
    public void pickNextCard(String playerName) throws InvalidContextualAction, InvalidParameters, InvalidMethodParameters {
        Controller controller = this.getController();
        Player currentPlayer = controller.getModel().getPlayer(playerName);
        if(!currentPlayer.equals(controller.getModel().getFlightBoard().getTurnOrder()[0])){
            
            throw new InvalidParameters("It's not your turn to pick the next card.");
        }
        CardDeck deck;
        deck = controller.getModel().getFlightBoard().getUpcomingCardDeck();
        if(deck.peekCards().isEmpty()){
            // no cards left: the flight is over
            controller.getModel().setState(new RewardsPhase(controller));
            return;
        }
        AdventureCardFilip card = deck.popCard();
        controller.getModel().setCurrentCardImagePath(card.getImagePath());
        card.accept(new CardResolverVisitor(), controller);
        
    }

    /**
     * Allows the player to leave the game early.
     * */
    @Override
    public void leaveRace(String playerName){
        Controller controller = this.getController();
        Player player = controller.getModel().getPlayer(playerName);
        controller.getModel().getFlightBoard().removePlayingPlayer(player);
        controller.getModel().setState(new FlightPhase(controller));
    }

    public List<String> getAvailableCommands(){
        return List.of( "PickNextCard",
                        "LeaveRace");
    }
}