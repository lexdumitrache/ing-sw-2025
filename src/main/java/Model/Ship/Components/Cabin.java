package Model.Ship.Components;

import Model.Enums.*;
import Model.Ship.Coordinates;
import com.google.gson.JsonObject;

import java.util.List;

/**
 * Represents a Cabin component on the spaceship. Cabins can host standard crew
 * members and may support brown or purple alien crew members depending on
 * their special abilities.
 */
public class Cabin extends SpaceshipComponent {
    private Crewmates occupants;
    private int canContainBrown;   // True if the cabin can host brown aliens
    private int canContainPurple;  // True if the cabin can host purple aliens

    // New fields to support aliens actually present in the cabin

    /**
     * Standard constructor for Cabin with explicit parameters, with no match with image path.
     */
    public Cabin(Card Type, ConnectorType front, ConnectorType rear, ConnectorType left, ConnectorType right, Crewmates occupants) {
        super(Type, front, rear, left, right);
        this.occupants = Crewmates.EMPTY; // Default to empty even if parameter is passed
    }

    /**
     * Constructor for Cabin with image path.
     */
    public Cabin(Card Type, ConnectorType front, ConnectorType rear, ConnectorType left, ConnectorType right, Crewmates occupants, String imagePath) {
        super(Type, front, rear, left, right, imagePath);
        this.occupants = Crewmates.EMPTY; // Default to empty even if parameter is passed
    }



    /**
     * Constructor to initialize a Cabin from a JSON object.
     * This is used by the ComponentFactory to load spaceship components from JSON configuration.
     */
    public Cabin(JsonObject json) {
        super(
                Card.valueOf(json.get("type").getAsString().toUpperCase()),
                ConnectorType.valueOf(json.getAsJsonObject("connectors").get("front").getAsString()),
                ConnectorType.valueOf(json.getAsJsonObject("connectors").get("rear").getAsString()),
                ConnectorType.valueOf(json.getAsJsonObject("connectors").get("left").getAsString()),
                ConnectorType.valueOf(json.getAsJsonObject("connectors").get("right").getAsString()),
                json.get("imagePath").getAsString()
        );

        this.occupants = Crewmates.EMPTY; // No crew by default when loading from JSON

        // Parse abilities from JSON if present
        this.canContainBrown = 0;
        this.canContainPurple = 0;
    }

    @Override
    public void visualize() {
        super.visualize();
        System.out.println("Occupants: " + occupants);
        System.out.println("Can Contain Brown: " + canContainBrown);
        System.out.println("Can Contain Purple: " + canContainPurple);
        System.out.println("==========================");
        System.out.printf("\n\n\n\n");
    }
    public boolean getCanContainBrown() {
        return canContainBrown>0;
    }

    public boolean getCanContainPurple() {
        return canContainPurple>0;
    }

    public void setCanContainBrown(int canContainBrown) {
        this.canContainBrown = canContainBrown;
    }

    public void setCanContainPurple(int canContainPurple) {
        this.canContainPurple = canContainPurple;
    }

    public void incrementCanContainBrown() {
        this.canContainBrown++;
    }
    public void incrementCanContainPurple() {
        this.canContainPurple++;
    }

    public void decrementCanContainBrown() {
        if (this.canContainBrown > 0) {
            this.canContainBrown--;
            if(this.occupants==Crewmates.BROWN_ALIEN) {

                if (this.canContainBrown == 0) {
                    this.occupants = Crewmates.EMPTY;
                    this.getShipBoard().getCondensedShip().getAliens().setBrownAlien(false);
                }

            }
        } else {
            throw new IllegalArgumentException("Can't decrement canContainBrown below zero");
        }
    }

    public void decrementCanContainPurple() {
        if (this.canContainPurple > 0) {
            this.canContainPurple--;
            if(this.occupants==Crewmates.PURPLE_ALIEN) {

                if (this.canContainPurple == 0) {
                    this.occupants = Crewmates.EMPTY;
                    this.getShipBoard().getCondensedShip().getAliens().setPurpleAlien(false);
                }

            }
        } else {
            throw new IllegalArgumentException("Can't decrement canContainPurple below zero");
        }
    }

    public void setOccupants(Crewmates occupants) {
        this.occupants = occupants;
    }

    public Crewmates getOccupants() {
        return occupants;
    }




    @Override
    public void added() {
        if(getShipBoard().getCondensedShip().getCabins().contains(this)){
            throw new RuntimeException("Cabin already added to the ship.");
        } else {
            getShipBoard().getCondensedShip().addCabin(this);
        }
        Coordinates myCoordinates= super.getShipBoard().getIndex(this);

        List<AlienLifeSupport> allAlienLifeSupports = getShipBoard().getCondensedShip().getAlienSupports();

        for(AlienLifeSupport alienLifeSupport : allAlienLifeSupports) {
            if(getShipBoard().areComponentsConnected(this, alienLifeSupport)) {
                if(alienLifeSupport.getColor() == AlienColor.BROWN){
                    this.incrementCanContainBrown();
                } else if(alienLifeSupport.getColor() == AlienColor.PURPLE) {
                    this.incrementCanContainPurple();
                }
            }
        }



    }

    @Override
    public void removed() {
        if (!getShipBoard().getCondensedShip().getCabins().contains(this)) {
            throw new RuntimeException("Cabin not found in the ship.");
        } else {
            getShipBoard().getCondensedShip().removeCabin(this);
        }
    }

    public String[] renderSmall() {
        String[] righe = new String[3];
        righe[0] = String.format("╔═ %s ═╗",
                this.getConnectorAt(Side.FRONT).getNumero() > 0 ?
                        String.valueOf(this.getConnectorAt(Side.FRONT).getNumero()) : "═");
        String sx = (this.getConnectorAt(Side.LEFT).getNumero() > 0 ? String.valueOf(this.getConnectorAt(Side.LEFT).getNumero()) : "║");
        String dx = (this.getConnectorAt(Side.RIGHT).getNumero() > 0 ? String.valueOf(this.getConnectorAt(Side.RIGHT).getNumero()) : "║");
        righe[1] = String.format("%s CAB %s", sx, dx);
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

        righe[1] = "║  CABIN  ║";
        righe[2] = "║         ║";

        righe[3] = String.format("%s%s%s",
                (this.getConnectorAt(Side.LEFT).getNumero() > 0 ?
                        String.valueOf(this.getConnectorAt(Side.LEFT).getNumero()) : "║"),
                "    " + this.getOrientation().getFreccia(),
                "    " + (this.getConnectorAt(Side.RIGHT).getNumero() > 0 ?
                        String.valueOf(this.getConnectorAt(Side.RIGHT).getNumero()) : "║")
        );

        switch (this.getOccupants()) {
            case EMPTY:
                righe[4] = "║         ║";
                break;
            case BROWN_ALIEN:
                righe[4] = "║  BROWN  ║";
                break;
            case PURPLE_ALIEN:
                righe[4] = "║ PURPLE  ║";
                break;
            case SINGLE_HUMAN:
                righe[4] = "║  1 HUM  ║";
                break;
            case DOUBLE_HUMAN:
                righe[4] = "║  2 HUM  ║";
                break;
            default:
                righe[4] = "║   ?     ║"; // Fallback case, should not happen
        }

        // Riga inferiore
        righe[5] = String.format("╚══  %s  ══╝",
                this.getConnectorAt(Side.REAR).getNumero() > 0 ?
                        String.valueOf(this.getConnectorAt(Side.REAR).getNumero()) : "═");

        return righe;
    }

    @Override
    public Cabin clone(){
        Cabin clone = new Cabin(this.getType(), this.getConnectorAt(Side.FRONT), this.getConnectorAt(Side.REAR), this.getConnectorAt(Side.LEFT), this.getConnectorAt(Side.RIGHT), null, this.getImagePath());

        clone.occupants = this.occupants;
        clone.canContainBrown = this.canContainBrown;
        clone.canContainPurple = this.canContainPurple;
        clone.orientation = this.getOrientation();

        return clone;
    }
}
