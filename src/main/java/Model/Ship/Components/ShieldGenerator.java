package Model.Ship.Components;

import Model.Enums.Card;
import Model.Enums.ConnectorType;
import Model.Enums.Direction;
import Model.Enums.Side;
import com.google.gson.JsonObject;

/**
 * Represents a Shield Generator component in Galaxy Trucker.
 * Shield Generators provide directional defense, often requiring battery power.
 */
public class ShieldGenerator extends SpaceshipComponent {
    /**
     * Constructor with explicit parameters.
     */
    public ShieldGenerator(Card type, ConnectorType front, ConnectorType rear, ConnectorType left, ConnectorType right, Direction direction) {
        super(type, front, rear, left, right);
    }


    /**
     * Constructor to initialize a ShieldGenerator from a JSON object.
     * Used by ComponentFactory for dynamic component creation.
     */
    public ShieldGenerator(JsonObject json) {
        super(
                Card.valueOf(json.get("type").getAsString().toUpperCase()),
                ConnectorType.valueOf(json.getAsJsonObject("connectors").get("front").getAsString()),
                ConnectorType.valueOf(json.getAsJsonObject("connectors").get("rear").getAsString()),
                ConnectorType.valueOf(json.getAsJsonObject("connectors").get("left").getAsString()),
                ConnectorType.valueOf(json.getAsJsonObject("connectors").get("right").getAsString()),
                json.get("imagePath").getAsString()
        );
    }

    @Override
    public void visualize() {
        super.visualize();
        System.out.println("==========================");
        System.out.printf("\n\n\n\n");
    }

    @Override
    public void added(){
        switch(orientation) {
            case UP:
                getShipBoard().getCondensedShip().getShields().incrementNorthShields();
                getShipBoard().getCondensedShip().getShields().incrementEastShields();
                break;
            case RIGHT:
                getShipBoard().getCondensedShip().getShields().incrementSouthShields();
                getShipBoard().getCondensedShip().getShields().incrementEastShields();
                break;
            case DOWN:
                getShipBoard().getCondensedShip().getShields().incrementSouthShields();
                getShipBoard().getCondensedShip().getShields().incrementWestShields();
                break;
            case LEFT:
                getShipBoard().getCondensedShip().getShields().incrementNorthShields();
                getShipBoard().getCondensedShip().getShields().incrementWestShields();
                break;
        }
    }

    @Override
    public void removed() {
        switch(orientation) {
            case UP:
                getShipBoard().getCondensedShip().getShields().decrementNorthShields();
                getShipBoard().getCondensedShip().getShields().decrementEastShields();
                break;
            case RIGHT:
                getShipBoard().getCondensedShip().getShields().decrementSouthShields();
                getShipBoard().getCondensedShip().getShields().decrementEastShields();
                break;
            case DOWN:
                getShipBoard().getCondensedShip().getShields().decrementSouthShields();
                getShipBoard().getCondensedShip().getShields().decrementWestShields();
                break;
            case LEFT:
                getShipBoard().getCondensedShip().getShields().decrementNorthShields();
                getShipBoard().getCondensedShip().getShields().decrementWestShields();
                break;
        }
    }

    public String[] renderSmall() {
        String[] righe = new String[3];
        righe[0] = String.format("╔═ %s ═╗",
                this.getConnectorAt(Side.FRONT).getNumero() > 0 ?
                        String.valueOf(this.getConnectorAt(Side.FRONT).getNumero()) : "═");
        String sx = (this.getConnectorAt(Side.LEFT).getNumero() > 0 ? String.valueOf(this.getConnectorAt(Side.LEFT).getNumero()) : "║");
        String dx = (this.getConnectorAt(Side.RIGHT).getNumero() > 0 ? String.valueOf(this.getConnectorAt(Side.RIGHT).getNumero()) : "║");
        switch (this.getOrientation()){
            case UP:
                righe[1] = String.format("%s S↑→ %s", sx, dx);
                break;
            case RIGHT:
                righe[1] = String.format("%s S→↓ %s", sx, dx);
                break;
            case DOWN:
                righe[1] = String.format("%s S↓← %s", sx, dx);
                break;
            case LEFT:
                righe[1] = String.format("%s S←↑ %s", sx, dx);
                break;
        }
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

        righe[1] = "║  SHIELD ║";

        righe[2] = String.format("%s%s%s",
                (this.getConnectorAt(Side.LEFT).getNumero() > 0 ?
                        String.valueOf(this.getConnectorAt(Side.LEFT).getNumero()) : "║"),
                "         ",
                (this.getConnectorAt(Side.RIGHT).getNumero() > 0 ?
                        String.valueOf(this.getConnectorAt(Side.RIGHT).getNumero()) : "║")
        );

        switch (this.getOrientation()) {
            case UP:
                righe[3] = "║   ↑ →   ║";
                break;
            case RIGHT:
                righe[3] = "║   → ↓   ║";
                break;
            case DOWN:
                righe[3] = "║   ↓ ←   ║";
                break;
            case LEFT:
                righe[3] = "║   ← ↑   ║";
                break;
        }
        righe[4] = "║         ║";

        // Riga inferiore
        righe[5] = String.format("╚══  %s  ══╝",
                this.getConnectorAt(Side.REAR).getNumero() > 0 ?
                        String.valueOf(this.getConnectorAt(Side.REAR).getNumero()) : "═");

        return righe;
    }

    @Override
    public ShieldGenerator clone() {
        ShieldGenerator clone = new ShieldGenerator(this.getType(), this.getConnectorAt(Side.FRONT), this.getConnectorAt(Side.REAR), this.getConnectorAt(Side.LEFT), this.getConnectorAt(Side.RIGHT), this.getOrientation());

        clone.orientation = this.getOrientation();

        return clone;
    }
}
