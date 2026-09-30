package Model.Ship.Components;

import Model.Enums.Side;
import com.google.gson.JsonObject;
import Model.Enums.Card;
import Model.Enums.ConnectorType;

/**
 * Cannon – single or double, fires in specific directions based on orientation.
 * Firepower depends on direction, alien bonus, and battery activation for double cannons.
 */
public class Cannon extends SpaceshipComponent {

    private final boolean isDouble;

    public Cannon(Card type, ConnectorType front, ConnectorType rear, ConnectorType left, ConnectorType right, boolean isDouble) {
        super(type, front, rear, left, right);
        this.isDouble = isDouble;
    }

    public Cannon(JsonObject json) {
        super(
                Card.valueOf(json.get("type").getAsString().toUpperCase()),
                ConnectorType.valueOf(json.getAsJsonObject("connectors").get("front").getAsString()),
                ConnectorType.valueOf(json.getAsJsonObject("connectors").get("rear").getAsString()),
                ConnectorType.valueOf(json.getAsJsonObject("connectors").get("left").getAsString()),
                ConnectorType.valueOf(json.getAsJsonObject("connectors").get("right").getAsString()),
                json.get("imagePath").getAsString()
        );


        if (json.has("isDoubleCannon")) {
            this.isDouble = json.get("isDoubleCannon").getAsBoolean();
        }else{
            throw new RuntimeException("Missing isDoubleCannon in Engine JSON configuration"+
                    " at " + json.get("imagePath").getAsString());
        }
    }

    @Override
    public void visualize() {
        super.visualize();
        System.out.println("Cannon is Double: " + isDouble);
        System.out.println("==========================");
        System.out.printf("\n\n\n\n");
    }

//    public void setOrientation(Direction dir) {
//        this.orientation = dir;
//    }

    public boolean isDouble() {
        return isDouble;
    }


//    /**
//     * Computes effective power in a specific direction.
//     * Double cannons return 0 unless activated.
//     * Adds +1 if an alien is onboard.
//     */
//    public int getEffectivePower(Direction fireDirection) {
//        if (getFiringDirections().contains(fireDirection)) {
//            if (isDouble && !activated) {
//                return 0; // double cannon not activated → 0 power
//            }
//            int power = isDouble ? 2 : 1;
//            if (hasAlien) {
//                power += 1;
//            }
//            return power;
//        }
//        return 0;
//    }


    @Override
    public void added(){
        if(getShipBoard().getCondensedShip().getCannons().contains(this)){
            throw new RuntimeException("Cannon already added to the ship.");
        } else {
            getShipBoard().getCondensedShip().addCannon(this);
        }
    }

    @Override
    public void removed() {
        if(!getShipBoard().getCondensedShip().getCannons().contains(this)){
            throw new RuntimeException("Cannon not found in the ship.");
        } else {
            getShipBoard().getCondensedShip().removeCannon(this);
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
            righe[1] = String.format("%s C1%s %s", sx, this.getOrientation().getFreccia(), dx);
        } else {
            righe[1] = String.format("%s C2%s %s", sx, this.getOrientation().getFreccia(), dx);
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

        righe[1] = "║  CANNON ║";
        if (!this.isDouble) {
            righe[2] = "║  SINGLE ║";
        } else {
            righe[2] = "║  DOUBLE ║";
        }

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
    public Cannon clone() {
        Cannon clone = new Cannon(this.getType(), this.getConnectorAt(Side.FRONT), this.getConnectorAt(Side.REAR), this.getConnectorAt(Side.LEFT), this.getConnectorAt(Side.RIGHT), this.isDouble);

        clone.orientation = this.getOrientation();

        return clone;
    }
}
