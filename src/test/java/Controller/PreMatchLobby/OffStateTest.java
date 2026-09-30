package Controller.PreMatchLobby;

import Controller.Controller;
import Controller.Enums.ComponentOrigin;
import Controller.Enums.MatchLevel;
import Controller.Exceptions.InvalidCommand;
import Model.Enums.Direction;
import Model.Ship.Coordinates;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for OffState functionality.
 * Verifies that all commands throw InvalidCommand exception in this state.
 */
public class OffStateTest {

    private Controller controller;
    private OffState offState;

    @BeforeEach
    void setUp() {
        // Create controller in OffState
        controller = new Controller(MatchLevel.TRIAL, 999);
        offState = new OffState(controller);
        controller.getModel().setState(offState);
    }

    @Test
    @DisplayName("Should reject login command in OffState")
    void testLoginThrowsInvalidCommand() {
        InvalidCommand exception = assertThrows(InvalidCommand.class,
                () -> offState.login("Player1"));
        assertTrue(exception.getMessage().contains("Invalid command: login"));
    }

    @Test
    @DisplayName("Should reject logout command in OffState")
    void testLogoutThrowsInvalidCommand() {
        InvalidCommand exception = assertThrows(InvalidCommand.class,
                () -> offState.logout("Player1"));
        assertTrue(exception.getMessage().contains("Invalid command: logout"));
    }

    @Test
    @DisplayName("Should reject startGame command in OffState")
    void testStartGameThrowsInvalidCommand() {
        InvalidCommand exception = assertThrows(InvalidCommand.class,
                () -> offState.startGame("Player1"));
        assertTrue(exception.getMessage().contains("Invalid command: startGame"));
    }

    @Test
    @DisplayName("Should reject getComponent command in OffState")
    void testGetComponentThrowsInvalidCommand() {
        InvalidCommand exception = assertThrows(InvalidCommand.class,
                () -> offState.getComponent("Player1", 0));
        assertTrue(exception.getMessage().contains("Invalid command: getComponent"));
    }

    @Test
    @DisplayName("Should reject reserveComponent command in OffState")
    void testReserveComponentThrowsInvalidCommand() {
        InvalidCommand exception = assertThrows(InvalidCommand.class,
                () -> offState.reserveComponent("Player1"));
        assertTrue(exception.getMessage().contains("Invalid command: reserveComponent"));
    }

    @Test
    @DisplayName("Should reject placeComponent command in OffState")
    void testPlaceComponentThrowsInvalidCommand() {
        InvalidCommand exception = assertThrows(InvalidCommand.class,
                () -> offState.placeComponent("Player1", ComponentOrigin.HAND,
                        new Coordinates(5, 5), Direction.UP));
        assertTrue(exception.getMessage().contains("Invalid command: placeComponent"));
    }

    @Test
    @DisplayName("Should reject lookDeck command in OffState")
    void testLookDeckThrowsInvalidCommand() {
        InvalidCommand exception = assertThrows(InvalidCommand.class,
                () -> offState.lookDeck("Player1", 0));
        assertTrue(exception.getMessage().contains("Invalid command: lookDeck"));
    }

    @Test
    @DisplayName("Should reject deleteComponent command in OffState")
    void testDeleteComponentThrowsInvalidCommand() {
        InvalidCommand exception = assertThrows(InvalidCommand.class,
                () -> offState.deleteComponent("Player1", new Coordinates(5, 5)));
        assertTrue(exception.getMessage().contains("Invalid command: deleteComponent"));
    }

    @Test
    @DisplayName("Should reject flipHourGlass command in OffState")
    void testFlipHourGlassThrowsInvalidCommand() {
        InvalidCommand exception = assertThrows(InvalidCommand.class,
                () -> offState.flipHourGlass("Player1"));
        assertTrue(exception.getMessage().contains("Invalid command: flipHourGlass"));
    }

    @Test
    @DisplayName("Should reject getPreBuiltShip command in OffState")
    void testGetPreBuiltShipThrowsInvalidCommand() {
        InvalidCommand exception = assertThrows(InvalidCommand.class,
                () -> offState.preBuiltShip("Player1", 0));
        assertTrue(exception.getMessage().contains("Invalid command: preBuiltShip"));
    }

    @Test
    @DisplayName("Should reject finishAssembly command in OffState")
    void testFinishAssemblyThrowsInvalidCommand() {
        InvalidCommand exception = assertThrows(InvalidCommand.class,
                () -> offState.finishBuilding("Player1", 1));
        assertTrue(exception.getMessage().contains("Invalid command: finishBuilding"));
    }

    @Test
    @DisplayName("Verify state cannot transition to any other state")
    void testStateRemainsClosed() {
        // Try all commands to ensure state doesn't change
        try { offState.login("Player1"); } catch (Exception e) {}
        try { offState.logout("Player1"); } catch (Exception e) {}
        try { offState.startGame("Player1"); } catch (Exception e) {}
        try { offState.getComponent("Player1", 0); } catch (Exception e) {}

        // State should still be OffState
        assertTrue(controller.getModel().getState() instanceof OffState);
    }
}