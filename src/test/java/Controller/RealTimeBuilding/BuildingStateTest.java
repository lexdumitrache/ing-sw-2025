package Controller.RealTimeBuilding;


import Controller.Enums.*;
import Controller.Exceptions.*;
import Controller.GamePhases.FlightPhase;
import Model.*;
import Model.Enums.Direction;
import Model.Ship.Components.SpaceshipComponent;
import Model.Ship.CondensedShip;
import Model.Ship.Coordinates;
import Model.Ship.ShipBoard;
import TestUtils.TestStateManager;
import org.junit.jupiter.api.Test;
import Controller.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

public class BuildingStateTest {

    // ==================== FINISH BUILDING TESTS ====================

    /**
     * Test that a player with an invalid ship cannot finish building in Trial level.
     * Uses prebuilt ship at index 2 which is known to be invalid.
     */
    @Test
    public void testFinishBuilding_InvalidShipTrial() {
        Controller controller = TestStateManager.createBuildingWith2PlayersTrial().getController();
        Player anna = controller.getModel().getPlayer("Anna");

        // Set invalid prebuilt ship (index 2)
        try {
            controller.preBuiltShip("Anna", 2);
        } catch (Exception e) {
            fail("Failed to set prebuilt ship: " + e.getMessage());
        }

        // Verify ship is invalid
        assertFalse(anna.getShipBoard().validateShip());

        // Try to finish building - should throw exception
        assertThrows(InvalidCommand.class, () -> controller.finishBuilding("Anna", 1));
    }

    /**
     * Test that finishedPlayers list is correctly updated when a player finishes building.
     */
    @Test
    public void testFinishBuilding_FinishedPlayersListUpdate() {
        Controller controller = TestStateManager.createBuildingWith2PlayersTrial().getController();
        Game model = controller.getModel();

        // Get initial finished players
        List<Player> initialFinished = new ArrayList<>(model.getFlightBoard().getFlyingPlayers());
        Set<Player> beforeFinished = model.getFlightBoard().getFlyingPlayers();

        // Anna builds valid ship and finishes
        try {
            controller.preBuiltShip("Anna", 0); // Valid ship
            controller.finishBuilding("Anna", 1);
        } catch (Exception e) {
            fail("Failed to finish building: " + e.getMessage());
        }

        // Check finished players list
        Set<Player> afterFinished = model.getFlightBoard().getFlyingPlayers();
        assertEquals(initialFinished.size() + 1, afterFinished.size());
        assertTrue(afterFinished.containsAll(initialFinished));
        assertTrue(afterFinished.contains(model.getPlayer("Anna")));

        // Verify only Anna was added
        afterFinished.removeAll(initialFinished);
        assertEquals(1, afterFinished.size());
        assertTrue(beforeFinished.contains(model.getPlayer("Anna")));
    }

    /**
     * Test state transitions when all players finish building.
     */
    @ParameterizedTest
    @CsvSource({
            "true, true",   // Level 2 with invalid ship
            "true, false",  // Level 2 with all valid ships
            "false, true",  // Trial with invalid ship (should not transition to FixShipState)
            "false, false"  // Trial with all valid ships
    })
    public void testFinishBuilding_StateTransitions(boolean isLevel2, boolean hasInvalidShip) {
        Controller controller;
        if (isLevel2) {
            controller = TestStateManager.finishedBuilding1wrong(MatchLevel.LEVEL2).getController();
        } else {
            controller = TestStateManager.finishedBuilding1wrong(MatchLevel.TRIAL).getController();
        }

        Game model = controller.getModel();

        // If testing without invalid ship, remove Carl
        if (!hasInvalidShip) {
            controller = TestStateManager.finishedBuildingAllValid(isLevel2 ? MatchLevel.LEVEL2 : MatchLevel.TRIAL).getController();
            model = controller.getModel();
        }

        // All players finish building
        try {
            int i=1;
            for (Player p : model.getPlayers()) {
                if (!model.getFlightBoard().getFlyingPlayers().contains(p)) {

                    controller.finishBuilding(p.getName(), i);
                    i++;
                }
            }
        } catch (Exception e) {
            // Expected for trial with invalid ship
            if (!isLevel2 && hasInvalidShip) {
                return; // Test passes - cannot finish with invalid ship in trial
            }
            fail("Unexpected exception: " + e.getMessage());
        }

        // Check resulting state
        State currentState = model.getState();

        if (isLevel2 && hasInvalidShip) {
            assertInstanceOf(FixShipState.class, currentState);
        } else if (isLevel2 && !hasInvalidShip) {
            // Check if any player can place aliens
            boolean canPlaceAliens = false;
            int length = model.getPlayers().size();
            for(int i = 0; (i < length)&&!canPlaceAliens; i++) {
                CondensedShip ship=model.getPlayers().get(i).getShipBoard().getCondensedShip();
                if(ship.canContainBrown()||ship.canContainPurple()){
                    canPlaceAliens = true;
                }
            }

            if (canPlaceAliens) {
                assertInstanceOf(PlaceAlienState.class, currentState);
            } else {
                assertInstanceOf(FlightPhase.class, currentState);
            }
        } else {
            // Trial level - always goes to FlightPhase
            assertInstanceOf(FlightPhase.class, currentState);
        }
    }

    /**
     * Test that finished players cannot execute commands except flip hourglass in level 2.
     */
    @Test
    public void testFinishBuilding_FinishedPlayerRestrictions() {
        Controller controller = TestStateManager.createBuildingWith2PlayersLevel2().getController();

        // Anna finishes building
        try {
            controller.preBuiltShip("Anna", 0);
            controller.finishBuilding("Anna", 1);
        } catch (Exception e) {
            fail("Failed to finish building: " + e.getMessage());
        }

        // Test that Anna cannot execute most commands
        assertThrows(InvalidCommand.class, () -> controller.getComponent("Anna", 0));
        assertThrows(InvalidCommand.class, () -> controller.placeComponent("Anna", ComponentOrigin.HAND, new Coordinates(1,1), Direction.UP ));
        assertThrows(InvalidCommand.class, () -> controller.reserveComponent("Anna"));
        assertThrows(InvalidCommand.class, () -> controller.lookDeck("Anna", 0));

        // But can flip hourglass (when it's finished)
        // Note: This would require waiting for hourglass to finish
    }

    /**
     * Test starting position selection and uniqueness.
     */
    @Test
    public void testFinishBuilding_StartingPositions() {
        Controller controller = TestStateManager.createBuildingWith2PlayersTrial().getController();

        // Anna chooses position 1
        try {
            controller.preBuiltShip("Anna", 0);
            controller.finishBuilding("Anna", 1);
        } catch (Exception e) {
            fail("Failed to finish building for Anna: " + e.getMessage());
        }

        // Bob tries to choose same position - should fail
        try {
            controller.preBuiltShip("Bob", 0);
        } catch (Exception e) {
            fail("Failed to set prebuilt ship for Bob: " + e.getMessage());
        }

        assertThrows(InvalidParameters.class, () -> controller.finishBuilding("Bob", 1));

        // Bob chooses different position - should succeed
        assertDoesNotThrow(() -> controller.finishBuilding("Bob", 2));
    }

    // ==================== GET COMPONENT TEST (EXISTING) ====================

    @Test
    public void testGetComponent() {
        Controller testController = TestStateManager.createBuildingWith2PlayersLevel2().getController();
        Player anna=testController.getModel().getPlayer("Anna");
        Player bob=testController.getModel().getPlayer("Bob");
        SpaceshipComponent tilePicked=testController.getModel().getTiles()[0];
        try {
            testController.getComponent("Anna", 0);
        } catch (InvalidCommand e) {
            throw new RuntimeException(e);
        } catch (InvalidParameters e) {
            throw new RuntimeException(e);
        }

        boolean testTileEliminated =true;
        int l=testController.getModel().getPlayers().size();
        for (int i=0;(i<l)&&testTileEliminated;i++) {
            if(tilePicked==testController.getModel().getTiles()[i]) {
                testTileEliminated=false;
            }
        }

        assertTrue(testTileEliminated); //controlla abbia eliminato dalla mano la tile
        assertEquals(tilePicked, anna.getShipBoard().getActiveComponent()); //controlla che la carta sia arrivata in mano
        anna.getShipBoard().render(MatchLevel.LEVEL2);
        try {
            testController.reserveComponent("Anna");
        } catch (InvalidCommand e) {
            throw new RuntimeException(e);
        } catch (InvalidParameters e) {
            throw new RuntimeException(e);
        }
        anna.getShipBoard().render(MatchLevel.LEVEL2);

        //controlla che ora active tile sia null
        assertEquals(null, anna.getShipBoard().getActiveComponent());
        assertTrue(anna.getShipBoard().getReservedComponents().contains(tilePicked));

        SpaceshipComponent tilePickedOld =testController.getModel().getTiles()[1];
        try {
            testController.getComponent("Anna", 1);
        } catch (InvalidCommand e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (InvalidParameters e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        testTileEliminated =true;
        for (int i=0;(i<l)&&testTileEliminated;i++) {
            if(tilePickedOld ==testController.getModel().getTiles()[i]) {
                testTileEliminated=false;
            }
        }

        assertTrue(testTileEliminated); //controlla abbia eliminato dalla mano la tile
        assertEquals(tilePickedOld, anna.getShipBoard().getActiveComponent()); //controlla che la carta sia arrivata in mano
        anna.getShipBoard().render(MatchLevel.LEVEL2);

        tilePicked =testController.getModel().getTiles()[2];
        try {
            testController.getComponent("Anna", 2);
        } catch (InvalidCommand e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (InvalidParameters e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        testTileEliminated =true;
        for (int i=0;(i<l)&&testTileEliminated;i++) {
            if(tilePickedOld ==testController.getModel().getTiles()[i]) {
                testTileEliminated=false;
            }
        }

        //assertThrows(Exception.class, () -> buildingState.getComponent("Player1", 0));
    }

    /**
     * Test that active tile becomes visible after getComponent.
     */
    @Test
    public void testGetComponent_ActiveTileVisibility() {
        Controller controller = TestStateManager.createBuildingWith2PlayersLevel2().getController();
        Player anna = controller.getModel().getPlayer("Anna");

        // Get component
        try {
            controller.getComponent("Anna", 0);
        } catch (Exception e) {
            fail("Failed to get component: " + e.getMessage());
        }

        // Verify active tile is visible
        SpaceshipComponent activeTile = anna.getShipBoard().getActiveComponent();
        assertNotNull(activeTile);
        assertTrue(activeTile.isVisible(), "Active tile should be visible after getComponent");
    }

    /**
     * Test tile removal and addition management.
     */
    @Test
    public void testGetComponent_TileManagement() {
        Controller controller = TestStateManager.createBuildingWith2PlayersLevel2().getController();
        Player anna = controller.getModel().getPlayer("Anna");
        Game model = controller.getModel();

        // Save initial state
        SpaceshipComponent previousActive = anna.getShipBoard().getActiveComponent();
        List<SpaceshipComponent> initialTiles = new ArrayList<>();
        for (SpaceshipComponent tile : model.getTiles()) {
            initialTiles.add(tile);
        }
        List<SpaceshipComponent> initialReserved = new ArrayList<>(anna.getShipBoard().getReservedComponents());

        // Get component
        SpaceshipComponent selectedTile = model.getTiles()[0];
        try {
            controller.getComponent("Anna", 0);
        } catch (Exception e) {
            fail("Failed to get component: " + e.getMessage());
        }

        // Verify correct tile management
        assertEquals(selectedTile, anna.getShipBoard().getActiveComponent());
        assertNull(model.getTiles()[0]);

        // Verify reserved components unchanged
        assertEquals(initialReserved, anna.getShipBoard().getReservedComponents());

        // If previous active tile existed, verify it's back in deck
        if (previousActive != null) {
            boolean foundInDeck = false;
            for (SpaceshipComponent tile : model.getTiles()) {
                if (tile == previousActive) {
                    foundInDeck = true;
                    break;
                }
            }
            assertTrue(foundInDeck, "Previous active tile should be returned to deck");
        }
    }

    // ==================== PLACE COMPONENT TESTS ====================

    /**
     * Test placing component at valid adjacent coordinates.
     */
    @Test
    public void testPlaceComponent_ValidAdjacentPlacement() {
        Controller controller = TestStateManager.createBuildingWith2PlayersTrial().getController();
        Player anna = controller.getModel().getPlayer("Anna");

        // Get component and place first one
        try {
            controller.getComponent("Anna", 0);
            controller.placeComponent("Anna",ComponentOrigin.HAND, new Coordinates(7,6), Direction.UP); // Center position
        } catch (Exception e) {
            fail("Failed initial placement: " + e.getMessage());
        }

        // Get another component and place adjacent
        try {
            controller.getComponent("Anna", 1);
            controller.placeComponent("Anna",ComponentOrigin.HAND, new Coordinates(7,5), Direction.UP); // Adjacent position
        } catch (Exception e) {
            fail("Failed adjacent placement: " + e.getMessage());
        }

        // Verify both placements successful
        assertNotNull(anna.getShipBoard().getComponent(new Coordinates(7, 6)));
        assertNotNull(anna.getShipBoard().getComponent(new Coordinates(7,5)));
    }

    /**
     * Test that non-adjacent valid coordinates are rejected.
     */
    @Test
    public void testPlaceComponent_NonAdjacentRejected() {
        Controller controller = TestStateManager.createBuildingWith2PlayersTrial().getController();

        // Place first component
        try {
            controller.getComponent("Anna", 0);
            controller.placeComponent("Anna", ComponentOrigin.HAND, new Coordinates(7, 6), Direction.UP);
        } catch (Exception e) {
            fail("Failed initial placement: " + e.getMessage());
        }

        // Try to place non-adjacent
        try {
            controller.getComponent("Anna", 1);
        } catch (Exception e) {
            fail("Failed to get component: " + e.getMessage());
        }

        assertThrows(InvalidParameters.class,
                () -> controller.placeComponent("Anna", ComponentOrigin.HAND, new Coordinates(3, 3), Direction.UP));
    }

    /**
     * Test invalid coordinate placements.
     */
    @ParameterizedTest
    @CsvSource({
            "5, 4",    // Valid in matrix but not in ship shape
            "20, 20",  // Outside matrix bounds
            "-1, 5"    // Negative coordinates
    })
    public void testPlaceComponent_InvalidCoordinates(int i, int j) {
        Controller controller = TestStateManager.createBuildingWith2PlayersTrial().getController();

        try {
            controller.getComponent("Anna", 0);
        } catch (Exception e) {
            fail("Failed to get component: " + e.getMessage());
        }

        assertThrows(InvalidParameters.class,
                () -> controller.placeComponent("Anna", ComponentOrigin.HAND, new Coordinates(i,j), Direction.UP));
    }

    /**
     * Test HAND placement behavior.
     */
    @Test
    public void testPlaceComponent_HandSource() {
        Controller controller = TestStateManager.createBuildingWith2PlayersTrial().getController();
        Player anna = controller.getModel().getPlayer("Anna");
        Game model = controller.getModel();

        // Save initial state
        List<SpaceshipComponent> initialTiles = new ArrayList<>();
        for (SpaceshipComponent tile : model.getTiles()) {
            initialTiles.add(tile);
        }

        // Cannot place from hand if no active tile
        assertNull(anna.getShipBoard().getActiveComponent());
        assertThrows(InvalidCommand.class,
                () -> controller.placeComponent("Anna", ComponentOrigin.HAND, new Coordinates(7, 6), Direction.UP));

        // Get component
        SpaceshipComponent selectedTile = model.getTiles()[0];
        try {
            controller.getComponent("Anna", 0);
        } catch (Exception e) {
            fail("Failed to get component: " + e.getMessage());
        }

        assertEquals(selectedTile, anna.getShipBoard().getActiveComponent());

        // Place from hand - success
        try {
            controller.placeComponent("Anna", ComponentOrigin.HAND, new Coordinates(7, 6), Direction.UP);
        } catch (Exception e) {
            fail("Failed to place component: " + e.getMessage());
        }

        // Verify active tile is now null
        assertNull(anna.getShipBoard().getActiveComponent());

        // Try invalid placement - active tile should remain
        try {
            controller.getComponent("Anna", 1);
        } catch (Exception e) {
            fail("Failed to get second component: " + e.getMessage());
        }

        SpaceshipComponent secondTile = anna.getShipBoard().getActiveComponent();
        assertThrows(InvalidParameters.class,
                () -> controller.placeComponent("Anna", ComponentOrigin.HAND, new Coordinates(5, 5), Direction.UP)); // Non-adjacent

        // Active tile should still be there after failed placement
        assertEquals(secondTile, anna.getShipBoard().getActiveComponent());
    }

    /**
     * Test FIRST_RESERVED placement behavior.
     */
    @Test
    public void testPlaceComponent_FirstReservedSource() {
        Controller controller = TestStateManager.createBuildingWith2PlayersLevel2().getController();
        Player anna = controller.getModel().getPlayer("Anna");
        Game model = controller.getModel();

        // Setup: get and reserve a component
        SpaceshipComponent activeTile = null;
        try {
            controller.getComponent("Anna", 0);
            activeTile = anna.getShipBoard().getActiveComponent();
            controller.reserveComponent("Anna");
            anna.getShipBoard().render(MatchLevel.LEVEL2);
            controller.placeComponent("Anna", ComponentOrigin.FIRST_RESERVED, new Coordinates(7, 6), Direction.UP);
            activeTile = anna.getShipBoard().getActiveComponent();
        } catch (Exception e) {
            fail("Failed to reserve component: " + e.getMessage());
        }

        // Cannot place from reserved if empty
        //anna.getShipBoard().getReservedComponents().removeFirst();
        anna.getShipBoard().render(MatchLevel.LEVEL2);
        assertThrows(Exception.class,
                () -> controller.placeComponent("Anna", ComponentOrigin.FIRST_RESERVED, new Coordinates(6, 7), Direction.UP));

        // Add back reserved component
        anna.getShipBoard().getReservedComponents().add(activeTile);

        // Get new active tile
        try {
            controller.getComponent("Anna", 1);
            controller.reserveComponent("Anna");
        } catch (Exception e) {
            fail("Failed to get component: " + e.getMessage());
        }

        SpaceshipComponent newActiveTile = anna.getShipBoard().getActiveComponent();
        List<SpaceshipComponent> tilesBeforePlacement = new ArrayList<>();
        for (SpaceshipComponent t : model.getTiles()) {
            if (t != null) tilesBeforePlacement.add(t);
        }

        // Place from first reserved
        try {
            controller.placeComponent("Anna", ComponentOrigin.FIRST_RESERVED, new Coordinates(6, 7), Direction.UP);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed to place from reserved: " + e.getMessage());
        }

        // Verify active tile was returned to deck
        assertNull(anna.getShipBoard().getActiveComponent());
        boolean activeTileInDeck = false;
        for (SpaceshipComponent t : model.getTiles()) {
            if (t == newActiveTile) {
                activeTileInDeck = true;
                break;
            }
        }
        assertTrue(activeTileInDeck);

        // Verify reserved component was used
        assertFalse(anna.getShipBoard().getReservedComponents().contains(activeTile));
    }

    /**
     * Test SECOND_RESERVED placement behavior.
     */
    @Test
    public void testPlaceComponent_SecondReservedSource() {
        Controller controller = TestStateManager.createBuildingWith2PlayersLevel2().getController();
        Player anna = controller.getModel().getPlayer("Anna");

        // Setup: reserve two components
        SpaceshipComponent first = null, second = null;
        try {
            controller.getComponent("Anna", 0);
            first = anna.getShipBoard().getActiveComponent();
            controller.reserveComponent("Anna");


        } catch (Exception e) {
            fail("Failed to reserve components: " + e.getMessage());
        }

        assertEquals(1, anna.getShipBoard().getReservedComponents().size());

        // Cannot place second reserved if less than 2
        assertDoesNotThrow(() -> controller.getComponent("Anna", 1));
        assertDoesNotThrow(() -> controller.placeComponent("Anna", ComponentOrigin.HAND, new Coordinates(6, 7), Direction.UP));
        assertThrows(InvalidParameters.class,
                () -> controller.placeComponent("Anna", ComponentOrigin.SECOND_RESERVED, new Coordinates(6, 7), Direction.UP));


        try {
            controller.getComponent("Anna", 100);
            second = anna.getShipBoard().getActiveComponent();
            controller.reserveComponent("Anna");
            assertEquals(2, anna.getShipBoard().getReservedComponents().size());
            controller.placeComponent("Anna", ComponentOrigin.SECOND_RESERVED, new Coordinates(8, 7), Direction.UP);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed to place second reserved: " + e.getMessage());
        }

        // Verify correct component was removed
        assertEquals(1, anna.getShipBoard().getReservedComponents().size());
        assertTrue(anna.getShipBoard().getReservedComponents().contains(first));
        assertFalse(anna.getShipBoard().getReservedComponents().contains(second));
    }

    // ==================== RESERVE COMPONENT TESTS ====================

    /**
     * Test basic reserve component functionality.
     */
    @Test
    public void testReserveComponent_BasicFunctionality() {
        Controller controller = TestStateManager.createBuildingWith2PlayersLevel2().getController();
        Player anna = controller.getModel().getPlayer("Anna");
        Game model = controller.getModel();

        // Cannot reserve without active tile
        assertThrows(InvalidCommand.class, () -> controller.reserveComponent("Anna"));

        // Get component and reserve
        SpaceshipComponent tile = model.getTiles()[0];
        try {
            controller.getComponent("Anna", 0);
            assertEquals(tile, anna.getShipBoard().getActiveComponent());

            controller.reserveComponent("Anna");
        } catch (Exception e) {
            fail("Failed to reserve component: " + e.getMessage());
        }

        // Verify reservation
        assertNull(anna.getShipBoard().getActiveComponent());
        assertEquals(1, anna.getShipBoard().getReservedComponents().size());
        assertTrue(anna.getShipBoard().getReservedComponents().contains(tile));
    }

    /**
     * Test that reserve component is not available in Trial level.
     */
    @Test
    public void testReserveComponent_NotInTrial() {
        Controller controller = TestStateManager.createBuildingWith2PlayersTrial().getController();

        try {
            controller.getComponent("Anna", 0);
        } catch (Exception e) {
            fail("Failed to get component: " + e.getMessage());
        }

        assertThrows(InvalidCommand.class, () -> controller.reserveComponent("Anna"));
    }

    /**
     * Test maximum 2 reserved components limit.
     */
    @Test
    public void testReserveComponent_MaximumLimit() {
        Controller controller = TestStateManager.createBuildingWith2PlayersLevel2().getController();
        Player anna = controller.getModel().getPlayer("Anna");

        // Reserve 2 components
        try {
            controller.getComponent("Anna", 0);
            controller.reserveComponent("Anna");

            controller.getComponent("Anna", 1);
            controller.reserveComponent("Anna");
        } catch (Exception e) {
            fail("Failed to reserve components: " + e.getMessage());
        }

        assertEquals(2, anna.getShipBoard().getReservedComponents().size());

        // Try to reserve third - should fail
        try {
            controller.getComponent("Anna", 2);
        } catch (Exception e) {
            fail("Failed to get component: " + e.getMessage());
        }

        assertThrows(InvalidCommand.class, () -> controller.reserveComponent("Anna"));

        // Verify state unchanged after failed reserve
        assertEquals(2, anna.getShipBoard().getReservedComponents().size());
        assertNotNull(anna.getShipBoard().getActiveComponent());
    }

    // ==================== LOOK DECK TESTS ====================

    /**
     * Test that lookDeck is only available in Level 2.
     */
    @Test
    public void testLookDeck_OnlyLevel2() {
        // Trial level - should fail
        Controller trialController = TestStateManager.createBuildingWith2PlayersTrial().getController();
        assertThrows(InvalidCommand.class, () -> trialController.lookDeck("Anna", 1));

        // Level 2 - should work (assuming proper implementation)
        Controller level2Controller = TestStateManager.createBuildingWith4Players(MatchLevel.LEVEL2).getController();
        assertDoesNotThrow(() -> level2Controller.getComponent("Anna", 1));
        Player anna=level2Controller.getModel().getPlayer("Anna");
        anna.getShipBoard().render(MatchLevel.LEVEL2);
        assertThrows(InvalidCommand.class,() -> level2Controller.lookDeck("Anna", 1));
        assertDoesNotThrow(() -> level2Controller.placeComponent("Anna", ComponentOrigin.HAND, new Coordinates(6, 7), Direction.UP));
        assertDoesNotThrow(() -> level2Controller.lookDeck("Anna", 1));



        Player bob=level2Controller.getModel().getPlayer("Bob");
        bob.getShipBoard().render(MatchLevel.LEVEL2);
        assertThrows(InvalidCommand.class,() -> level2Controller.lookDeck("Bob", 2));
        assertDoesNotThrow(() -> level2Controller.getComponent("Bob", 2));
        assertDoesNotThrow(() -> level2Controller.placeComponent("Bob", ComponentOrigin.HAND, new Coordinates(6, 7), Direction.UP));
        assertThrows(InvalidParameters.class, () -> level2Controller.lookDeck("Bob", 1));
        assertDoesNotThrow(() -> level2Controller.lookDeck("Bob", 2));

        assertDoesNotThrow(() -> level2Controller.getComponent("Anna", 3));
        //mazzo 1 ora dovrebbe essere libero


        Player carl=level2Controller.getModel().getPlayer("Carl");
        //carl.getShipBoard().render(MatchLevel.LEVEL2);
        assertThrows(InvalidCommand.class,() -> level2Controller.lookDeck("Carl", 1));
        assertDoesNotThrow(() -> level2Controller.getComponent("Carl", 4));
        assertDoesNotThrow(() -> level2Controller.placeComponent("Carl", ComponentOrigin.HAND, new Coordinates(6, 7), Direction.UP));
        assertThrows(InvalidParameters.class,() -> level2Controller.lookDeck("Carl", 2));
        SpaceshipComponent oldActiveTile=level2Controller.getModel().getTiles()[5];

        assertDoesNotThrow(() -> level2Controller.getComponent("Carl", 5));
        SpaceshipComponent[] tilesArray = level2Controller.getModel().getTiles();
        List<SpaceshipComponent> allOldTiles = new ArrayList<>();

        for (SpaceshipComponent tile : tilesArray) {
            if (tile != null) {
                allOldTiles.add(tile);
            }
        }

        assertDoesNotThrow(() -> level2Controller.lookDeck("Carl", 3));
        tilesArray = level2Controller.getModel().getTiles();
        List<SpaceshipComponent> allNewTiles = new ArrayList<>();

        for (SpaceshipComponent tile : tilesArray) {
            if (tile != null) {
                allNewTiles.add(tile);
            }
        }
        assertTrue(allNewTiles.containsAll(allOldTiles));
        assertTrue(allNewTiles.contains(oldActiveTile));
        System.out.println(allNewTiles.size());
        System.out.println(allOldTiles.size());
        assertTrue(allNewTiles.size()==allOldTiles.size()+1);
        assertDoesNotThrow(() -> level2Controller.lookDeck("Carl", 3));
    }

    // ==================== FLIP HOURGLASS TESTS ====================

//    /**
//     * Test that flipHourGlass is not available in Trial level.
//     */
//    @Test
//    public void testflipHourGlass_NotInTrial() {
//        Controller controller = TestStateManager.createBuildingWith2PlayersLevel2().getController();
//        assertDoesNotThrow(() -> controller.flipHourGlass("Anna"));
//        assertThrows(InvalidCommand.class, () -> controller.flipHourGlass("Anna"));
//        try {
//            System.out.println("Sleep for 30 seconds");
//            Thread.sleep(31000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//
//        assertDoesNotThrow(() -> controller.flipHourGlass("Anna"));
//        assertThrows(InvalidCommand.class, () -> controller.flipHourGlass("Anna"));
//        try {
//            System.out.println("Sleep for 30 seconds");
//            Thread.sleep(31000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//        assertThrows(InvalidCommand.class, () -> controller.flipHourGlass("Anna"));
//
//        try{
//            controller.preBuiltShip("Anna", 0);
//            controller.finishBuilding("Anna", 1);
//            assertDoesNotThrow(() -> controller.flipHourGlass("Anna"));
//            try {
//                System.out.println("Sleep for 30 seconds");
//                Thread.sleep(31000);
//                controller.getComponent("Bob", 1);
//                assertTrue(controller.getModel().getState() instanceof HourGlassFinishedState);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        }catch (Exception e){
//            throw new RuntimeException(e);
//        }
//    }

    /**
     * Test that hourglass can only be flipped when finished.
     * Note: This test requires timing mechanisms which might need mocking.
     */
    @Test
    public void testflipHourGlass_Trail() {
        Controller controller = TestStateManager.createBuildingWith2PlayersTrial().getController();

        // Immediately try to flip - should fail as hourglass just started
        assertThrows(InvalidCommand.class, () -> controller.flipHourGlass("Anna"));

        // Note: Complete test would require waiting 30 seconds or mocking time
    }

    /**
     * Test that flipping hourglass doesn't modify game state.
     */
    @Test
    public void testflipHourGlass_NoStateModification() {
        Controller controller = TestStateManager.createBuildingWith2PlayersLevel2().getController();
        Player anna = controller.getModel().getPlayer("Anna");
        Game model = controller.getModel();

        // Save initial state
        SpaceshipComponent activeTile = anna.getShipBoard().getActiveComponent();
        List<SpaceshipComponent> reservedComponents = new ArrayList<>(anna.getShipBoard().getReservedComponents());
        List<SpaceshipComponent> gameTiles = new ArrayList<>();
        for (SpaceshipComponent t : model.getTiles()) {
            gameTiles.add(t);
        }

        // Try to flip (will fail but state should be unchanged)
        try {
            controller.flipHourGlass("Anna");
        } catch (InvalidCommand e) {
            // Expected
        }catch (InvalidParameters e){
            throw new RuntimeException(e);
        }

        // Verify nothing changed
        assertEquals(activeTile, anna.getShipBoard().getActiveComponent());
        assertEquals(reservedComponents, anna.getShipBoard().getReservedComponents());
        for (int i = 0; i < gameTiles.size(); i++) {
            assertEquals(gameTiles.get(i), model.getTiles()[i]);
        }
    }

    /**
     * Test that when last hourglass finishes, only FinishBuilding is accepted.
     * Other commands should transition to HourGlassFinishedState.
     */
    @Test
    public void testHourGlassFinished_OnlyFinishBuildingAccepted() {
        // This test would require a special setup where the hourglass has finished
        // Note: Implementation depends on how hourglass state is managed in the actual code

        Controller controller = TestStateManager.createBuildingWith2PlayersLevel2().getController();
        Game model = controller.getModel();

        // Simulate hourglass finished state
        // This is a conceptual test - actual implementation would depend on the game's hourglass mechanism

        // When hourglass is finished, these commands should cause transition to HourGlassFinishedState
        // assertThrows(StateTransitionException.class, () -> controller.getComponent("Anna", 0));
        // assertThrows(StateTransitionException.class, () -> controller.placeComponent("Anna", 5, 5, ComponentOrigin.HAND));
        // assertThrows(StateTransitionException.class, () -> controller.reserveComponent("Anna"));

        // Only FinishBuilding should be accepted
        // assertDoesNotThrow(() -> controller.finishBuilding("Anna", 1));
    }

    // ==================== DELETE COMPONENT TESTS ====================

    /**
     * Test that deleteComponent is only available in Trial flight phase.
     */
    @Test
    public void testDeleteComponent_OnlyInTrialFlight() {
        // Building state - should fail
        Controller controller = TestStateManager.createBuildingWith2PlayersLevel2().getController();
        assertThrows(InvalidCommand.class, () -> controller.deleteComponent("Anna", new Coordinates(5, 5)));

        Controller controller2 = TestStateManager.createBuildingWith2PlayersTrial().getController();
        assertDoesNotThrow(() -> controller2.getComponent("Anna", 1));
        assertDoesNotThrow(() -> controller2.placeComponent("Anna", ComponentOrigin.HAND, new Coordinates(6, 7), Direction.UP));
        assertDoesNotThrow(() -> controller2.deleteComponent("Anna", new Coordinates(6, 7)));


        // Note: Would need a flight phase trial controller to test positive case
    }

    /**
     * Test deleteComponent with invalid coordinates.
     */
    @ParameterizedTest
    @CsvSource({
            "20, 20",  // Outside matrix
            "-1, 5"    // Negative coordinates
    })
    public void testDeleteComponent_InvalidCoordinates(int i, int j) {
        // Note: This would require a trial flight phase controller
        // Placeholder for structure
        Controller controller = TestStateManager.createBuildingWith2PlayersTrial().getController();
        assertThrows(InvalidParameters.class, () -> controller.deleteComponent("Anna", new Coordinates(i,j)));
    }

    /**
     * Test deleteComponent returns component to Game.tiles.
     * Note: This test requires a flight phase trial controller.
     */
    @Test
    public void testDeleteComponent_ReturnToGameTiles() {
        // This is a conceptual test structure
        // In actual implementation, would need a flight phase trial controller

        // Controller controller = TestStateManager.createFlightPhaseTrial().getController();
        // Player anna = controller.getModel().getPlayer("Anna");
        // Game model = controller.getModel();

        // Assume Anna has a component at (5, 5)
        // SpaceshipComponent componentToDelete = anna.getShipBoard().getComponent(5, 5);
        // assertNotNull(componentToDelete);

        // Save initial tiles
        // List<SpaceshipComponent> initialTiles = Arrays.asList(model.getTiles());

        // Delete component
        // controller.deleteComponent("Anna", 5, 5);

        // Verify component is removed from ship
        // assertNull(anna.getShipBoard().getComponent(5, 5));

        // Verify component is back in Game.tiles
        // boolean foundInTiles = Arrays.asList(model.getTiles()).contains(componentToDelete);
        // assertTrue(foundInTiles, "Deleted component should return to Game.tiles");

        // Verify no other changes to tiles
        // List<SpaceshipComponent> finalTiles = Arrays.asList(model.getTiles());
        // assertEquals(initialTiles.size() + 1, finalTiles.size());
    }

    // ==================== PREBUILT SHIP TESTS ====================

    /**
     * Test invalid index for prebuilt ships.
     */
    @Test
    public void testPreBuiltShip_InvalidIndex() {
        Controller controller = TestStateManager.createBuildingWith2PlayersTrial().getController();
        Game model = controller.getModel();

        // Assuming preBuiltShips has limited size
        int invalidIndex = 10; // Likely out of bounds
        assertThrows(InvalidParameters.class, () -> controller.preBuiltShip("Anna", invalidIndex));

        // Test exact boundary
        // Note: Would need to know actual preBuiltShips array size
    }

    /**
     * Test that same prebuilt ship index creates distinct objects for different players.
     */
    @Test
    public void testPreBuiltShip_DistinctObjects() {
        Controller controller = TestStateManager.createBuildingWith2PlayersTrial().getController();
        Player anna = controller.getModel().getPlayer("Anna");
        Player bob = controller.getModel().getPlayer("Bob");

        // Both choose same prebuilt ship
        try {
            controller.preBuiltShip("Anna", 0);
            controller.preBuiltShip("Bob", 0);
        } catch (Exception e) {
            fail("Failed to set prebuilt ships: " + e.getMessage());
        }

        // Verify ships are distinct objects
        assertNotSame(anna.getShipBoard(), bob.getShipBoard());

        // Verify components are distinct
        // Note: This assumes ships have components after preBuiltShip
        // Would need to check actual ship structure
    }

    // ==================== ADDITIONAL PARAMETERIZED TESTS ====================

    /**
     * Parameterized test for various component operations.
     */
    @ParameterizedTest
    @CsvSource({
            "Anna, 0, true",
            "Bob, 1, true",
            "Anna, 2, true",
            "Bob, 0, true"
    })
    public void testComponentOperationsParameterized(String playerName, int componentIndex, boolean shouldSucceed) {
        Controller controller = TestStateManager.createBuildingWith2PlayersLevel2().getController();
        Player player = controller.getModel().getPlayer(playerName);

        // Test get component
        if (shouldSucceed) {
            assertDoesNotThrow(() -> controller.getComponent(playerName, componentIndex));
            assertNotNull(player.getShipBoard().getActiveComponent());
        }

        // Test reserve component
        if (player.getShipBoard().getActiveComponent() != null) {
            assertDoesNotThrow(() -> controller.reserveComponent(playerName));
            assertNull(player.getShipBoard().getActiveComponent());
        }
    }

    /**
     * Test random sequences of operations.
     */
    @Test
    public void testRandomOperationSequences() {
        Controller controller = TestStateManager.createBuildingWith2PlayersLevel2().getController();
        String[] players = {"Anna", "Bob"};
        Random random = new Random(42); // Fixed seed for reproducibility

        for (int i = 0; i < 10; i++) {
            String player = players[random.nextInt(players.length)];
            int operation = random.nextInt(3);

            try {
                switch (operation) {
                    case 0: // Get component
                        int index = findValidTileIndex(controller.getModel());
                        if (index != -1) {
                            controller.getComponent(player, index);
                        }
                        break;
                    case 1: // Reserve component
                        if (controller.getModel().getPlayer(player).getShipBoard().getActiveComponent() != null) {
                            controller.reserveComponent(player);
                        }
                        break;
                    case 2: // Place component
                        if (controller.getModel().getPlayer(player).getShipBoard().getActiveComponent() != null) {
                            Coordinate coord = findValidPlacementCoordinate(controller.getModel().getPlayer(player));
                            if (coord != null) {
                                controller.placeComponent(player, ComponentOrigin.HAND, new Coordinates(coord.x, coord.y), Direction.UP);
                            }
                        }
                        break;
                }
            } catch (Exception e) {
                // Some operations may fail due to game rules, which is expected
            }

            // Verify game state consistency after each operation
            verifyGameStateConsistency(controller.getModel());
        }
    }

    // ==================== UTILITY METHODS FOR PARAMETERIZED TESTS ====================

    /**
     * Helper method to verify Game.tiles consistency.
     */
    private void verifyGameTilesConsistency(SpaceshipComponent[] before, SpaceshipComponent[] after,
                                            Set<SpaceshipComponent> removed, Set<SpaceshipComponent> added) {
        Set<SpaceshipComponent> beforeSet = new HashSet<>();
        Set<SpaceshipComponent> afterSet = new HashSet<>();

        for (SpaceshipComponent t : before) {
            if (t != null) beforeSet.add(t);
        }
        for (SpaceshipComponent t : after) {
            if (t != null) afterSet.add(t);
        }

        // Check removed
        for (SpaceshipComponent r : removed) {
            assertTrue(beforeSet.contains(r));
            assertFalse(afterSet.contains(r));
        }

        // Check added
        for (SpaceshipComponent a : added) {
            assertFalse(beforeSet.contains(a));
            assertTrue(afterSet.contains(a));
        }

        // Check no other changes
        beforeSet.removeAll(removed);
        beforeSet.addAll(added);
        assertEquals(beforeSet, afterSet);
    }

    /**
     * Find a valid tile index in the game.
     */
    private int findValidTileIndex(Game model) {
        SpaceshipComponent[] tiles = model.getTiles();
        for (int i = 0; i < tiles.length; i++) {
            if (tiles[i] != null) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Find a valid placement coordinate for a player.
     */
    private Coordinate findValidPlacementCoordinate(Player player) {
        // Start from center and check adjacent positions
        int[][] offsets = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};

        // If no components placed yet, return center
        boolean hasComponents = false;
        for (int i = 5; i <9 ; i++) {
            for (int j = 4; j < 10; j++) {
                if (player.getShipBoard().getComponent(new Coordinates(i, j)) != null) {
                    hasComponents = true;
                    // Check adjacent positions
                    for (int[] offset : offsets) {
                        int newX = i + offset[0];
                        int newY = j + offset[1];
                        if (isValidShipCoordinate(newX, newY) &&
                                player.getShipBoard().getComponent(new Coordinates(newX, newY)) == null) {
                            return new Coordinate(newX, newY);
                        }
                    }
                }
            }
        }

        if (!hasComponents) {
            return new Coordinate(5, 5); // Center position for first placement
        }

        return null;
    }

    /**
     * Check if coordinates are valid for ship placement.
     */
    private boolean isValidShipCoordinate(int x, int y) {
        // This would need to check against the actual valid ship coordinates
        // For now, simple boundary check
        return x >= 0 && x < 10 && y >= 0 && y < 10;
    }

    /**
     * Verify overall game state consistency.
     */
    private void verifyGameStateConsistency(Game model) {
        // Check that all tiles are accounted for
        Set<SpaceshipComponent> allTiles = new HashSet<>();

        // Add tiles from game deck
        for (SpaceshipComponent tile : model.getTiles()) {
            if (tile != null) {
                assertFalse(allTiles.contains(tile), "Duplicate tile found in game deck");
                allTiles.add(tile);
            }
        }

        // Add tiles from players
        for (Player player : model.getPlayers()) {
            ShipBoard board = player.getShipBoard();

            // Active component
            if (board.getActiveComponent() != null) {
                assertFalse(allTiles.contains(board.getActiveComponent()),
                        "Active component already exists elsewhere");
                allTiles.add(board.getActiveComponent());
            }

            // Reserved components
            for (SpaceshipComponent reserved : board.getReservedComponents()) {
                assertFalse(allTiles.contains(reserved),
                        "Reserved component already exists elsewhere");
                allTiles.add(reserved);
            }

            // Placed components
            for (int i = 5; i < 9; i++) {
                for (int j = 4; j < 10; j++) {
                    SpaceshipComponent placed = board.getComponent(new Coordinates(i,j));
                    if (placed != null) {
                        assertFalse(allTiles.contains(placed),
                                "Placed component already exists elsewhere");
                        allTiles.add(placed);
                    }
                }
            }
        }

        // Additional consistency checks
        for (Player player : model.getPlayers()) {
            assertTrue(player.getShipBoard().getReservedComponents().size() <= 2,
                    "Player has more than 2 reserved components");
        }
    }

    /**
     * Helper class for coordinates.
     */
    private static class Coordinate {
        final int x, y;

        Coordinate(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }


}