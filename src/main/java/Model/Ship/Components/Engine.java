package Model.Ship.Components;

import Model.Enums.Side;
import com.google.gson.JsonObject;
import Model.Enums.Card;
import Model.Enums.ConnectorType;

/**
 * Class representing an engine module for the spaceship.
 * Supports loading from JSON and logical management of installation/removal.
 * Handles double engine activation via battery.
 */
public class Engine extends SpaceshipComponent {
    private final boolean isDouble;
    //private boolean hasAlien;

    public Engine(Card type, ConnectorType front, ConnectorType rear, ConnectorType left, ConnectorType right, boolean isDouble) {
        super(type, front, rear, left, right);
        this.isDouble = isDouble;
    }

    public Engine(JsonObject json) {
        super(
                Card.valueOf(json.get("type").getAsString().toUpperCase()),
                ConnectorType.valueOf(json.getAsJsonObject("connectors").get("front").getAsString()),
                ConnectorType.valueOf(json.getAsJsonObject("connectors").get("rear").getAsString()),
                ConnectorType.valueOf(json.getAsJsonObject("connectors").get("left").getAsString()),
                ConnectorType.valueOf(json.getAsJsonObject("connectors").get("right").getAsString()),
                json.get("imagePath").getAsString()
        );

        if (json.has("isDoubleEngine")) {
            this.isDouble = json.has("isDoubleEngine") && json.get("isDoubleEngine").getAsBoolean();
        }else {
            throw new RuntimeException("Missing isDoubleEngine in Engine JSON configuration at " +
                    json.get("imagePath").getAsString());
        }
    }

    @Override
    public void visualize() {
        super.visualize();
        System.out.println("Engine is Double: " + isDouble);
        System.out.println("==========================");
        System.out.printf("\n\n\n\n");
    }

    public boolean isDoubleEngine() {
        return isDouble;
    }





    @Override
    public void added(){
        if(getShipBoard().getCondensedShip().getEnginesList().contains(this)){
            throw new RuntimeException("Cargo Hold already added to the ship.");
        } else {
            getShipBoard().getCondensedShip().getEnginesList().add(this);
            if(!isDouble){
                getShipBoard().getCondensedShip().getEngines().incrementSingleEngines();
            } else {
                getShipBoard().getCondensedShip().getEngines().incrementDoubleEngines();
            }
        }
    }

    @Override
    public void removed() {
        if(!getShipBoard().getCondensedShip().getEnginesList().contains(this)){
            throw new RuntimeException("Cargo Hold not found in the ship.");
        } else {
            getShipBoard().getCondensedShip().getEnginesList().remove(this);
            if(!isDouble){
                getShipBoard().getCondensedShip().getEngines().decrementSingleEngines();
            } else {
                getShipBoard().getCondensedShip().getEngines().decrementDoubleEngines();
            }
        }

    }

    public String[] renderSmall() {
        String[] righe = new String[3];
        righe[0] = String.format("╔═ %s ═╗",
                this.getConnectorAt(Side.FRONT).getNumero() > 0 ?
                        String.valueOf(this.getConnectorAt(Side.FRONT).getNumero()) : "═");
        String sx = (this.getConnectorAt(Side.LEFT).getNumero() > 0 ? String.valueOf(this.getConnectorAt(Side.LEFT).getNumero()) : "║");
        String dx = (this.getConnectorAt(Side.RIGHT).getNumero() > 0 ? String.valueOf(this.getConnectorAt(Side.RIGHT).getNumero()) : "║");
        if (!this.isDouble) {
            righe[1] = String.format("%s E1%s %s", sx, this.getOrientation().getFreccia(), dx);
        } else {
            righe[1] = String.format("%s E2%s %s", sx, this.getOrientation().getFreccia(), dx);
        }
        righe[2] = String.format("╚═ %s ═╝", this.getConnectorAt(Side.REAR).getNumero() > 0 ?
                        String.valueOf(this.getConnectorAt(Side.REAR).getNumero()) : "═");
        return righe;
    }

    @Override
    public String[] renderBig() {
        String[] righe = new String[6];

        // Riga superiore
        righe[0] = String.format("╔══  %s  ══╗",
                this.getConnectorAt(Side.FRONT).getNumero() > 0 ?
                        String.valueOf(this.getConnectorAt(Side.FRONT).getNumero()) : "═");

        if (!this.isDouble) {
            righe[1] = "║  SINGLE ║";
        } else {
            righe[1] = "║  DOUBLE ║";
        }
        righe[2] = "║  ENGINE ║";


        righe[3] = String.format("%s%s%s",
                (this.getConnectorAt(Side.LEFT).getNumero() > 0 ?
                        String.valueOf(this.getConnectorAt(Side.LEFT).getNumero()) : "║"),
                "    " + this.getOrientation().getFreccia(),
                "    " + (this.getConnectorAt(Side.RIGHT).getNumero() > 0 ?
                        String.valueOf(this.getConnectorAt(Side.RIGHT).getNumero()) : "║")
        );

        righe[4] = "║         ║";

        // Riga inferiore
        righe[5] = String.format("╚══  %s  ══╝",
                this.getConnectorAt(Side.REAR).getNumero() > 0 ?
                        String.valueOf(this.getConnectorAt(Side.REAR).getNumero()) : "═");

        return righe;
    }

    @Override
    public Engine clone() {
        Engine clone = new Engine(this.getType(), this.getConnectorAt(Side.FRONT), this.getConnectorAt(Side.REAR), this.getConnectorAt(Side.LEFT), this.getConnectorAt(Side.RIGHT), this.isDouble);

        clone.orientation = this.getOrientation();

        return clone;
    }
}
