package Model.Ship.Components;

import Model.Enums.Card;
import Model.Enums.ConnectorType;
import Model.Enums.AlienColor;
import Model.Enums.Side;
import com.google.gson.JsonObject;

import java.io.PrintStream;
import java.util.List;

/**
 * Represents an Alien Life Support component.
 * Supports specific alien crew members (brown or purple), enhancing nearby cabins.
 */
public class AlienLifeSupport extends SpaceshipComponent {
    private final AlienColor color; // Indicates which alien color this life support supports

    /**
     * Constructor with explicit parameters.
     */
    public AlienLifeSupport(Card Type, ConnectorType front, ConnectorType rear, ConnectorType left, ConnectorType right, AlienColor color) {
        super(Type, front, rear, left, right);
        this.color = color;
    }

    /**
     * Constructor to initialize an AlienLifeSupport from a JSON object.
     * Used by ComponentFactory to dynamically instantiate the component.
     */
    public AlienLifeSupport(JsonObject json) {
        super(
                Card.valueOf(json.get("type").getAsString().toUpperCase()),
                ConnectorType.valueOf(json.getAsJsonObject("connectors").get("front").getAsString()),
                ConnectorType.valueOf(json.getAsJsonObject("connectors").get("rear").getAsString()),
                ConnectorType.valueOf(json.getAsJsonObject("connectors").get("left").getAsString()),
                ConnectorType.valueOf(json.getAsJsonObject("connectors").get("right").getAsString()),
                json.get("imagePath").getAsString()
        );

        if (json.has("alienColor")) {
            this.color = AlienColor.valueOf(json.get("alienColor").getAsString().toUpperCase());
        }else{
            throw new RuntimeException("Missing alienColor in AlienLifeSupport JSON configuration at"+
                    " " + json.get("imagePath").getAsString());
        }

    }

    @Override
    public void visualize() {
        super.visualize();
        System.out.println("Alien Supports Color: " + color);
        System.out.println("==========================");
        System.out.printf("\n\n\n\n");
    }

    /**
     * Returns the color of alien this module supports.
     */
    public AlienColor getColor() {
        return color;
    }

    @Override
    public void added(){
        if(getShipBoard().getCondensedShip().getAlienSupports().contains(this)){
            throw new RuntimeException("Alien Support already added to the ship.");
        } else {
            getShipBoard().getCondensedShip().addAlienSupport(this);
        }

        List<Cabin> allCabins = getShipBoard().getCondensedShip().getCabins();
        for(Cabin cabin : allCabins) {
            if(getShipBoard().areComponentsConnected(this, cabin)) {
                if(this.getColor() == AlienColor.BROWN){
                    cabin.incrementCanContainBrown();
                } else if(this.getColor() == AlienColor.PURPLE) {
                    cabin.incrementCanContainPurple();
                }
            }
        }
    }

    @Override
    public void removed() {
        if(!getShipBoard().getCondensedShip().getAlienSupports().contains(this)){
            throw new RuntimeException("Alien Support not found in the ship.");
        } else {
            getShipBoard().getCondensedShip().removeAlienSupport(this);
        }
        for(Cabin cabin : getShipBoard().getCondensedShip().getCabins()) {
            if(getShipBoard().areComponentsConnected(this, cabin)) {
                if(this.getColor() == AlienColor.BROWN){
                    cabin.decrementCanContainBrown();
                } else if(this.getColor() == AlienColor.PURPLE) {
                    cabin.decrementCanContainPurple();
                }
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
        if (this.getColor() == AlienColor.BROWN) {
            righe[1] = String.format("%s BAL %s", sx, dx);
        } else if (this.getColor() == AlienColor.PURPLE) {
            righe[1] = String.format("%s PAL %s", sx, dx);
        } else {
            righe[1] = String.format("%s ?AL %s", sx, dx); // Fallback case, should not happen
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

            righe[1] = "║ SUPPORT ║";

        if (color == AlienColor.BROWN) {
            righe[2] = "║  BROWN  ║";
        } else {
            righe[2] = "║  PURPLE ║";
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
    public AlienLifeSupport clone() {
        AlienLifeSupport clone = new AlienLifeSupport(this.getType(), this.getConnectorAt(Side.FRONT), this.getConnectorAt(Side.REAR), this.getConnectorAt(Side.LEFT), this.getConnectorAt(Side.RIGHT), this.color);

        clone.orientation = this.getOrientation();

        return clone;
    }
}
