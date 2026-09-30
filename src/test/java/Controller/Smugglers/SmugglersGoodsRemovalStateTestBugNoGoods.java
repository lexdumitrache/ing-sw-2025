package Controller.Smugglers;

import Controller.Enums.DoubleType;
import Controller.Enums.MatchLevel;
import Model.AdventureCardLoader;
import Model.Board.AdventureCards.AdventureCardFilip;
import Model.Board.AdventureCards.Smugglers;
import Model.Game;
import Model.Player;
import TestUtils.GameSnapshot;
import TestUtils.TestStateManager;
import org.junit.jupiter.api.Test;
import Controller.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SmugglersGoodsRemovalStateTestBugNoGoods {

    @Test
    public void bug(){
        GameSnapshot snapshot = TestStateManager.flightPhase2Players(MatchLevel.LEVEL2);
        System.out.println(snapshot.description);
        Controller controller= snapshot.getController();
        Game model= controller.getModel();
        List<AdventureCardFilip> adventureCardFilipList= AdventureCardLoader.loadAdventureCards(MatchLevel.LEVEL2, false);
        Smugglers smugglers=null;
        for (AdventureCardFilip filip : adventureCardFilipList) {
            if(filip.getId()==40){
                smugglers=(Smugglers) filip;
            }
        }
        assertNotNull(smugglers);

        List<Player> allPlayers=model.getPlayers();

        for(Player p : allPlayers){
            p.getShipBoard().render(MatchLevel.LEVEL2);
        }

        smugglers.visualize();

        smugglers.accept(new CardResolverVisitor(), controller);

        assertInstanceOf(SmugglersPowerDeclarationState.class, model.getState());
        assertDoesNotThrow(()->controller.declaresDouble("Anna", DoubleType.CANNONS, 5.0));
        assertDoesNotThrow(()->controller.declaresDouble("Bob", DoubleType.CANNONS, 4.0));
        assertInstanceOf(SecondSmugglersBatteryRemovalState.class, model.getState());


    }

}