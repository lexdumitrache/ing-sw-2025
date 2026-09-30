package Model.Ship.Components;

import Controller.Exceptions.InvalidContextualAction;
import Model.Enums.Card;
import Model.Enums.ConnectorType;
import Model.Enums.Side;
import com.google.gson.JsonObject;

/**
 * Represents a Battery Compartment component on the ship.
 * Provides energy used by other components like shields, cannons, or engines.
 */
public class BatteryCompartment extends SpaceshipComponent {
    private final int capacity;  // Maximum number of batteries the compartment can hold
    private int batteries;       // Current number of batteries available
    /**
     * Standard constructor for BatteryCompartment with explicit parameters.
     */
    public BatteryCompartment(Card Type, ConnectorType front, ConnectorType rear, ConnectorType left, ConnectorType right, int capacity) {
        super(Type, front, rear, left, right);
        this.capacity = capacity;
        this.batteries = capacity;
    }

    /**
     * Constructor that initializes a BatteryCompartment from a JSON object.
     * Used by the ComponentFactory to dynamically load components from configuration.
     */
    public BatteryCompartment(JsonObject json) {
        super(
                Card.valueOf(json.get("type").getAsString().toUpperCase()),
                ConnectorType.valueOf(json.getAsJsonObject("connectors").get("front").getAsString()),
                ConnectorType.valueOf(json.getAsJsonObject("connectors").get("rear").getAsString()),
                ConnectorType.valueOf(json.getAsJsonObject("connectors").get("left").getAsString()),
                ConnectorType.valueOf(json.getAsJsonObject("connectors").get("right").getAsString()),
                json.get("imagePath").getAsString()
        );

        if (json.has("capacity")) {
            this.capacity  = json.get("capacity").getAsInt();
            this.batteries = this.capacity;
        }else{
            throw new RuntimeException("Missing capacity in BatteryCompartment JSON configuration" +
                    " at " + json.get("imagePath").getAsString());
        }

    }

    @Override
    public void visualize() {
        super.visualize();
        System.out.println("Battery Compartment Capacity: " + capacity);
        System.out.println("Current Batteries: " + batteries);
        System.out.println("==========================");
        System.out.printf("\n\n\n\n");
    }

    public int getCapacity() {
        return capacity;
    }

    public int getBatteries() {
        return batteries;
    }

    public void setBatteries(int batteries) {
        this.batteries = batteries;
    }

    /**
     * Consumes one battery if available. Throws an exception if no batteries remain.
     */
    public void removeBattery() throws InvalidContextualAction {
        if (batteries > 0) {
            batteries--;
        } else {
            throw new InvalidContextualAction("No batteries left to remove");
        }
    }

    @Override
    public void added(){
        if(getShipBoard().getCondensedShip().getBatteryCompartments().contains(this)){
            throw new RuntimeException("Battery Compartment already added to the ship.");
        } else {
            getShipBoard().getCondensedShip().addBatteryCompartment(this);
        }
    }

    @Override
    public void removed() {
        if(!getShipBoard().getCondensedShip().getBatteryCompartments().contains(this)){
            throw new RuntimeException("Battery Compartment not found in the ship.");
        } else {
            getShipBoard().getCondensedShip().removeBatteryCompartment(this);
        }

    }

    public String[] renderSmall() {
        String[] righe = new String[3];
        righe[0] = String.format("╔═ %s ═╗",
                this.getConnectorAt(Side.FRONT).getNumero() > 0 ?
                        String.valueOf(this.getConnectorAt(Side.FRONT).getNumero()) : "═");
        String sx = (this.getConnectorAt(Side.LEFT).getNumero() > 0 ? String.valueOf(this.getConnectorAt(Side.LEFT).getNumero()) : "║");
        String dx = (this.getConnectorAt(Side.RIGHT).getNumero() > 0 ? String.valueOf(this.getConnectorAt(Side.RIGHT).getNumero()) : "║");
        righe[1] = String.format("%s BAT %s", sx, dx);
        righe[2] = String.format("╚═ %s ═╝", this.getConnectorAt(Side.REAR).getNumero() > 0 ?
                        String.valueOf(this.getConnectorAt(Side.REAR).getNumero()) : "═");
        return righe;
    }

    public String[] renderBig() {
        String[] righe = new String[6];

        // Riga superiore
        righe[0] = String.format("╔══  %s  ══╗",
                this.getConnectorAt(Side.FRONT).getNumero() > 0 ?
                        String.valueOf(this.getConnectorAt(Side.FRONT).getNumero()) : "═");

        righe[1] = "║ BATTERY ║";
        righe[2] = "║ COMPART ║";

        righe[3] = String.format("%s%s%s",
                (this.getConnectorAt(Side.LEFT).getNumero() > 0 ?
                        String.valueOf(this.getConnectorAt(Side.LEFT).getNumero()) : "║"),
                "    " + this.getOrientation().getFreccia(),
                "    " + (this.getConnectorAt(Side.RIGHT).getNumero() > 0 ?
                        String.valueOf(this.getConnectorAt(Side.RIGHT).getNumero()) : "║")
        );

        righe[4] = String.format("║    %d    ║", this.getBatteries());

        // Riga inferiore
        righe[5] = String.format("╚══  %s  ══╝",
                this.getConnectorAt(Side.REAR).getNumero() > 0 ?
                        String.valueOf(this.getConnectorAt(Side.REAR).getNumero()) : "═");

        return righe;
    }

    @Override
    public BatteryCompartment clone() {
        BatteryCompartment clone = new BatteryCompartment(this.getType(), this.getConnectorAt(Side.FRONT), this.getConnectorAt(Side.REAR), this.getConnectorAt(Side.LEFT), this.getConnectorAt(Side.RIGHT), this.capacity);

        clone.batteries = this.batteries;
        clone.orientation = this.getOrientation();

        return clone;
    }
}
