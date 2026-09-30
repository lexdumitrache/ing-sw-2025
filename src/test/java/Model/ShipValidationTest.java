package Model;

import Controller.Enums.MatchLevel;
import Model.Ship.ShipBoard;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ShipValidationTest {

    @Test
    public void testTrialShipsValidation() {
        Game game = new Game(MatchLevel.TRIAL);
        List<ShipBoard> ships = game.getPreBuiltShips();
        
        System.out.println("=== TRIAL SHIPS VALIDATION ===");
        for (int i = 0; i < ships.size(); i++) {
            ShipBoard ship = ships.get(i);
            boolean isValid = ship.validateShip();
            
            System.out.println("Ship " + (i + 1) + ":");
            System.out.println("  Valid: " + isValid);
            System.out.println("  Components: " + ship.getAllComponents().size());
            System.out.println("  Engines: " + ship.getCondensedShip().getEngines().getSingleEngines() + " single, " + ship.getCondensedShip().getEngines().getDoubleEngines() + " double");
            System.out.println("  Cabins: " + ship.getCondensedShip().getCabins().size());
            System.out.println("  Total Crew: " + ship.getCondensedShip().getTotalCrew());
            
            if (!isValid) {
                System.out.println("  ❌ INVALID SHIP!");
                System.out.println("  ⚠️  Ship " + (i + 1) + " needs fixing in ships.json");
            } else {
                System.out.println("  ✅ Valid ship");
            }
            System.out.println();
        }
    }

    @Test
    public void testLevel2ShipsValidation() {
        try {
            Game game = new Game((MatchLevel) MatchLevel.LEVEL2);
            List<ShipBoard> ships = game.getPreBuiltShips();
            
            System.out.println("=== LEVEL2 SHIPS VALIDATION ===");
            for (int i = 0; i < ships.size(); i++) {
                ShipBoard ship = ships.get(i);
                boolean isValid = ship.validateShip();
                
                System.out.println("Ship " + (i + 1) + ":");
                System.out.println("  Valid: " + isValid);
                System.out.println("  Components: " + ship.getAllComponents().size());
                System.out.println("  Engines: " + ship.getCondensedShip().getEngines().getSingleEngines() + " single, " + ship.getCondensedShip().getEngines().getDoubleEngines() + " double");
                System.out.println("  Cabins: " + ship.getCondensedShip().getCabins().size());
                System.out.println("  Total Crew: " + ship.getCondensedShip().getTotalCrew());
                
                if (!isValid) {
                    System.out.println("  ❌ INVALID SHIP!");
                    System.out.println("  ⚠️  Ship " + (i + 1) + " needs fixing in ships.json");
                } else {
                    System.out.println("  ✅ Valid ship");
                }
                System.out.println();
            }
        } catch (Exception e) {
            System.out.println("Failed to load LEVEL2 ships: " + e.getMessage());
        }
    }

    @Test
    public void testShipRendering() {
        Game game = new Game(MatchLevel.TRIAL);
        List<ShipBoard> ships = game.getPreBuiltShips();
        
        System.out.println("=== SHIP RENDERING TEST ===");
        for (int i = 0; i < ships.size(); i++) {
            ShipBoard ship = ships.get(i);
            System.out.println("Rendering Ship " + (i + 1) + ":");
            try {
                ship.render(game.getLevel());
                System.out.println("✅ Ship rendered successfully");
            } catch (Exception e) {
                System.out.println("❌ Ship rendering failed: " + e.getMessage());
            }
            System.out.println();
        }
    }
}