package Model;

import Model.Enums.Card;
import Model.Enums.ConnectorType;
import Model.Enums.Crewmates;
import Model.Exceptions.InvalidMethodParameters;
import Model.Ship.Components.Cabin;
import Model.Ship.Coordinates;
import Model.Ship.ShipBoard;

import java.io.Serializable;

// Classe principale del giocatore
public final class Player implements Serializable, Cloneable {
    private final String name;
    private ShipBoard shipBoard;
    private int credits;
    private int junk;
    private boolean error = false;
    private String errorMessage=null;





    /**
     * Constructor for Player.
     * Initializes the player's name, shipBoard, credits, and junk.
     * Sets the central cabin with default image path.
     *
     * @deprecated
     *
     * @param name The name of the player.
     */
    @Deprecated
    public Player(String name) {
        this.name = name;
        this.shipBoard = new ShipBoard();
        //set central cabin on the Ship
        Cabin centralCabin = new Cabin(Card.CABIN, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, Crewmates.EMPTY, "src/main/resources/pics/tiles/1.jpg");
        centralCabin.setShipBoard(shipBoard);
        try {
            shipBoard.addComponent(centralCabin, new Coordinates(7,7));
        } catch (InvalidMethodParameters e) {
            throw new RuntimeException(e);
        }
        centralCabin.added();

        this.credits = 0;
        this.junk = 0;
    }

    public Player(String name, Cabin centralCabin) {
        this.name = name;
        this.shipBoard = new ShipBoard();
        //set central cabin on the Ship
        centralCabin.setShipBoard(shipBoard);
        try {
            shipBoard.addComponent(centralCabin, new Coordinates(7,7));
        } catch (InvalidMethodParameters e) {
            throw new RuntimeException(e);
        }
        centralCabin.added();

        this.credits = 0;
        this.junk = 0;
    }

    public String getName() {
        return name;
    }

    public ShipBoard getShipBoard() {
        return shipBoard;
    }

    /**
     * Allows external classes (like GameBuilder) to assign a shipBoard of custom dimensions.
     */
    public void setShipBoard(ShipBoard shipBoard) {
        this.shipBoard = shipBoard;
    }

    public void deltaCredits(int delta) {
        credits += delta;
    }

    public int getCredits() {
        return credits;
    }

    public void addJunk() {
        junk++;
    }

    public int getJunk() {
        return junk;
    }

    @Override
    public Player clone() {
        return new Player(this);
    }


    private Player(Player old){
        this.name = old.name;
        this.shipBoard = (old.shipBoard != null) ? old.shipBoard.clone() : null;
        this.credits = old.credits;
        this.junk = old.junk;
        this.errorMessage = old.errorMessage;
    }

    public boolean isError() {return error;}

    public void setError(boolean error) {this.error = error;}

    public void setErrorMessage(String errorMessage) {this.errorMessage = errorMessage;}
    public String getErrorMessage() {return errorMessage;}
}
