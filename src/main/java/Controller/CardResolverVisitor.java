package Controller;

import Controller.AbandonedShip.AbandonedShipDecidingState;
import Controller.AbandonedStation.AbandonedStationDecidingState;
import Controller.CombatZone.Level_ONE.CombatZone1EngineDeclarationState;
import Controller.CombatZone.Level_TWO.CombatZone2EngineDeclarationState;
import Controller.CombatZone.Level_TWO.CombatZone2PowerDeclarationState;
import Controller.Exceptions.InvalidParameters;
import Controller.GamePhases.FlightPhase;
import Controller.MeteorsSwarm.MeteorsState;
import Controller.OpenSpace.OpenSpaceEngineDeclarationState;
import Controller.Pirates.PiratesPowerDeclarationState;
import Controller.Planets.ChoosePlanetState;
import Controller.Slavers.SlaversPowerDeclarationState;
import Controller.Smugglers.SmugglersPowerDeclarationState;
import Model.Board.AdventureCards.*;
import Model.Board.AdventureCards.Penalties.RegularPenalty;
import Model.Enums.CardLevel;
import Model.Enums.ConnectorType;
import Model.Enums.Crewmates;
import Model.Enums.Side;
import Model.Exceptions.InvalidMethodParameters;
import Model.Game;
import Model.Player;
import Model.Ship.Components.Cabin;
import Model.Ship.Components.SpaceshipComponent;
import Model.Ship.Coordinates;
import Model.Ship.ShipBoard;
//import com.sun.jdi.connect.Connector;

import java.util.*;

/**
 * CardResolverVisitor is a visitor class that handles the resolution of various adventure cards in the game.
 * Each method corresponds to a specific card type and defines the first state of the card and create the context for it.
 */

public class CardResolverVisitor {

    public CardResolverVisitor() {}

    /**
     * Visits an AbandonedShip card and sets the game state to AbandonedShipDecidingState.
     * This state allows players to decide how to handle the abandoned ship event.
     *
     * @param card The AbandonedShip card being visited
     * @param controller The game controller managing the game state
     */
    public void visit(AbandonedShip card, Controller controller) {
        /*
        si fa uno alla volta, fino a che qualcuno effettivamente la svolge, in ordine di rotta
        può rinunciare a tot quipaggio per dei crediti e perdere giorni di volo
         */
        Context context = new Context(controller, card);
        controller.getModel().setState(new AbandonedShipDecidingState(context));
    }

    /**
     * Visits an AbandonedStation card and sets the game state to AbandonedStationDecidingState.
     * This state allows players to decide how to handle the abandoned station event.
     *
     * @param card The AbandonedStation card being visited
     * @param controller The game controller managing the game state
     */
    public void visit(AbandonedStation card, Controller controller) {
        /*
        si fa uno alla volta, fino a che qualcuno effettivamente la svolge, in ordine di rotta
        se il giocatore ha abbastanza equipaggio, può caricare le merci dove vuole e ridistribuirle
        il giocatore perde giorni di volo
         */
        Context context = new Context(controller, card);
        controller.getModel().setState(new AbandonedStationDecidingState(context));
    }

    /**
     * Visits a Planets card and sets the game state to ChoosePlanetState.
     * This state allows players to choose a planet to interact with.
     *
     * @param card The Planets card being visited
     * @param controller The game controller managing the game state
     */
    public void visit( Planets card, Controller controller) {
        /*

         */
        Context context = new Context(controller, card);
        controller.getModel().setState(new ChoosePlanetState(context));
    }

    /**
     * Visits a CombatZone card and handles the combat zone event based on the card level.
     * For level 1, it calculates the player with the least firepower and deducts flight days.
     * For level 2, it sets the state to CombatZone2PowerDeclarationState for power declaration.
     *
     * @param card The CombatZone card being visited
     * @param controller The game controller managing the game state
     * @throws InvalidMethodParameters If the card level is invalid
     * @throws InvalidParameters If there are issues with player parameters
     */
    public void visit(CombatZone card, Controller controller) throws InvalidMethodParameters, InvalidParameters {
        /*
        1. si calcola il giocatore con meno potenza di fuoco e si perdono giorni di volo
        2. si calcola il giocatore con meno potenza motrice e si perdono delle merci
           le merci vanno tolte le più preziose (penso che a parità si faccia decidere al giocatore)
        3. si calcola il giocatore con meno equipaggio e si gestiscono le cannonate
           le cannonate piccole si parano solo con gli scudi, quelle grandi si prega
        in caso di parità, il più avanti nella rotta sconta la penalità
         */
        Context context = new Context(controller, card);
        CombatZone Ccard = (CombatZone) card;
        if(Ccard.getLevel() == CardLevel.LEARNER){
            int numPlayers = controller.getModel().getFlightBoard().getTurnOrder().length;
            if(numPlayers == 1){
                controller.getModel().setState(new FlightPhase(controller));
                
                return;
            }

            Player currentPlayer = controller.getModel().getFlightBoard().getTurnOrder()[0];
            for(int i = 0; i<numPlayers-1; i++){
                Player nextPlayer = controller.getModel().getFlightBoard().getTurnOrder()[(i+1)];
                if(nextPlayer.getShipBoard().getCondensedShip().getTotalCrew() > currentPlayer.getShipBoard().getCondensedShip().getTotalCrew()){
                    currentPlayer = nextPlayer;
                }
            }
            RegularPenalty penalty = (RegularPenalty) Ccard.iterator().next().getPenalty();
            controller.getModel().getFlightBoard().deltaFlightDays(currentPlayer, -penalty.getAmount());
            controller.getModel().setState(new CombatZone1EngineDeclarationState(context));
            
        } else if( Ccard.getLevel() == CardLevel.LEVEL_TWO) {
            controller.getModel().setState(new CombatZone2PowerDeclarationState(context));
            
        } else{
            throw new InvalidMethodParameters("Invalid card level for CombatZone: " + Ccard.getLevel());
        }

    }

    /**
     * Visits an Epidemic card and processes the epidemic event.
     * It iterates through each player's ship board, checks cabins for occupants,
     * and removes a crew member if the cabin is adjacent to another cabin.
     *
     * @param card The Epidemic card being visited
     * @param controller The game controller managing the game state
     */
    public void visit( Epidemic card, Controller controller) {
        /*
        si scorrono le cabine, e se essa è adiacente a un'altra cabina, si rimuove un membro dell'equipaggio (alieno o umano)
         */
        Game model=controller.getModel();

        for(Player player : model.getFlightBoard().getTurnOrder()){
            ShipBoard playerShipBoard = player.getShipBoard();
            List<Cabin> allCabins= player.getShipBoard().getCondensedShip().getCabins();
            int length=allCabins.size();
            boolean removed=false;
            for(int i = 0; (i <length)&&!removed; i++){
                Cabin thisCabin=allCabins.get(i);
                Crewmates thisCabinOccupants =thisCabin.getOccupants();
                if (thisCabinOccupants != Crewmates.EMPTY) {
                    for(int j = 0; j <length&&!removed; j++){
                        Cabin otherCabin=allCabins.get(j);
                        if(playerShipBoard.areComponentsConnected(thisCabin, otherCabin)){
                            removed=true;
                            if(thisCabinOccupants ==Crewmates.DOUBLE_HUMAN){
                                thisCabin.setOccupants(Crewmates.SINGLE_HUMAN);
                            }else{
                                thisCabin.setOccupants(Crewmates.EMPTY);
                            }
                        }
                    }
                }
            }

        }

        model.setState(new FlightPhase(controller));
    }

    /**
     * Visits a MeteorSwarm card and sets the game state to MeteorsState.
     * This state handles the meteor swarm event, where players must deal with incoming meteors.
     *
     * @param card The MeteorSwarm card being visited
     * @param controller The game controller managing the game state
     */
    public void visit(MeteorSwarm card, Controller controller) {
        /*
        il leader tira due dadi e determina riga e colonna che vengono colpite
        si controlla se ogni giocatore viene colpito
        se è un piccolo meteorite, si controlla il side dove colpisce: se è esposto è un problema
        il giocatore può nel caso decidere se usare lo scudo o no (-1 batteria)
        se si rompe il componente, aggiungere al Junk un pezzo
        fare la check integrity della nave
        Per il grosso meteorite si controllano eventuali cannoni che possono sparare ( se è doppio -1 batteria)


         */

        Context context = new Context(controller, card);
        controller.getModel().setState(new MeteorsState(context));
    }

    /**
     * Visits an OpenSpace card and sets the game state to OpenSpaceEngineDeclarationState.
     * This state allows players to declare their engine power for the open space event.
     *
     * @param card The OpenSpace card being visited
     * @param controller The game controller managing the game state
     */
    public void visit( OpenSpace card, Controller controller) {
        /*
        deve ricevere in qualche modo il numero di batterie che ciascun giocatore vuole usare
        poi semplicemente si calcola la potenza motrice di ciascun giocatore e si muove la nave sulla flighboard
        */
        Context context = new Context(controller, card);
        controller.getModel().setState(new OpenSpaceEngineDeclarationState(context));
    }

    /**
     * Visits a Pirates card and sets the game state to PiratesPowerDeclarationState.
     * This state allows players to declare their power against pirates.
     *
     * @param card The Pirates card being visited
     * @param controller The game controller managing the game state
     */
    public void visit( Pirates card, Controller controller) {
        /*
        come i controabbandieri
        si vincono crediti e se si perde si ricevono cannonate
         */

        Context context = new Context(controller, card);
        controller.getModel().setState(new PiratesPowerDeclarationState(context));
    }

    /**
     * Visits a Slavers card and sets the game state to SlaversPowerDeclarationState.
     * This state allows players to declare their power against slavers.
     *
     * @param card The Slavers card being visited
     * @param controller The game controller managing the game state
     */
    public void visit( Slavers card, Controller controller) {
        /*
        come i controabbandieri
        si vincono crediti e di perde equipaggio
         */

        Context context = new Context(controller, card);
        controller.getModel().setState(new SlaversPowerDeclarationState(context));
    }

    /**
     * Visits a Smugglers card and sets the game state to SmugglersPowerDeclarationState.
     * This state allows players to declare their power against smugglers.
     *
     * @param card The Smugglers card being visited
     * @param controller The game controller managing the game state
     */
    public void visit( Smugglers card, Controller controller) {
        /*
        in ordine di rotta, si decide se il giocatore vince/perde o si va avanti, in base alla potenza di fuoco
        1. se vince, il nemico è sconfitto, il giocatore può reclamare le merci e spostarle anche, perdendo giorni di volo
        2. se perde, si paga la penalità e si passa al giocatore dopo
         */

        Context context = new Context(controller, card);
        controller.getModel().setState(new SmugglersPowerDeclarationState(context));
    }

    /**
     * Visits a Stardust card and processes the Stardust event.
     * It iterates through players in reverse turn order, checking for exposed connectors
     * and deducting flight days based on the number of exposed connectors.
     *
     * @param card The Stardust card being visited
     * @param controller The game controller managing the game state
     * @throws InvalidMethodParameters If there are issues with method parameters
     */
    public void visit( Stardust card, Controller controller) throws InvalidMethodParameters {
        /*
        in ordine inverso di rotta si scorre per vedere se un giocatore ha connettori esposti, e
        in base al loro tipo si perdono giorni di rotta
         */
        // Get players in reverse turn order for Stardust event processing
        System.out.println("Stardust");
        List<Player> playersReversed = new ArrayList<>(List.of(controller.getModel().getFlightBoard().getTurnOrder()));
        Collections.reverse(playersReversed);
        for(Player p : playersReversed){
            int counter = 0;

            for (Map.Entry<Coordinates, List<Side>> entry : p.getShipBoard().getExposedConnectors().entrySet()) {
                Coordinates position = entry.getKey();
                List<Side> sides = entry.getValue();

                if (p.getShipBoard().getComponent(position) != null) {
                    for (Side side : sides) {
                        ConnectorType connector = p.getShipBoard().getComponent(position).getConnectorAt(side); // supponendo che Position abbia questo metodo

                        if (!(connector == ConnectorType.NONE)) {
                            counter++;
                        }
                    }
                }
            }
            controller.getModel().getFlightBoard().deltaFlightDays(p, -counter);
        }


    }

    public interface SpaceshipComponentVisitor {
        void visit(Cabin cabin);
        // You can add more components later as needed
    }


}
