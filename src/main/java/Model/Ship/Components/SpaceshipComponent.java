package Model.Ship.Components;

import Model.Enums.Card;
import Model.Enums.ConnectorType;
import Model.Enums.Direction;
import Model.Enums.Side;
import Model.Ship.ShipBoard;

import java.io.Serializable;

/**
 * Abstract class representing a spaceship component.
 * Handles rotation, connectors, and association with the shipboard.
 */
public abstract class SpaceshipComponent implements Serializable, Cloneable {
    private final Card type;
    private ConnectorType frontConnector;
    private ConnectorType rearConnector;
    private ConnectorType leftConnector;
    private ConnectorType rightConnector;
    protected Direction orientation; // UP = standard, RIGHT = 90°, DOWN = 180°, LEFT = 270°
    private transient ShipBoard shipBoard;
    private boolean isVisible;
    private String imagePath;
    private final String backCardImagePath = "src/main/resources/pics/tiles/0.png";

    /**
     * Constructor, that does not set the image path!
     */
    public SpaceshipComponent(Card type, ConnectorType front, ConnectorType rear, ConnectorType left, ConnectorType right) {
        this.type = type;
        this.frontConnector = front;
        this.rearConnector = rear;
        this.leftConnector = left;
        this.rightConnector = right;
        this.orientation = Direction.UP; // Default orientation
        this.isVisible = false;
        this.shipBoard = null;
        this.imagePath=null;
    }

    /**
     * Constructor with image path.
     * This is used by the ComponentFactory to load spaceship components from JSON configuration.
     */
    public SpaceshipComponent(Card type, ConnectorType front, ConnectorType rear, ConnectorType left, ConnectorType right, String imagePath) {
        this.type = type;
        this.frontConnector = front;
        this.rearConnector = rear;
        this.leftConnector = left;
        this.rightConnector = right;
        this.orientation = Direction.UP; // Default orientation
        this.isVisible = false;
        this.shipBoard = null;
        this.imagePath = imagePath;
    }

    public void visualize(){
        System.out.println("==========================");
        System.out.println("Visualizing component: " + this.getClass().getSimpleName());
        System.out.println("Image Path: " + imagePath);
        System.out.println("Front Connector: " + frontConnector);
        System.out.println("Rear Connector: " + rearConnector);
        System.out.println("Left Connector: " + leftConnector);
        System.out.println("Right Connector: " + rightConnector);
        System.out.println("Orientation: " + orientation);
        System.out.println("Visible: " + isVisible);
        System.out.println("Ship Board: " + shipBoard);


    }

    public String getBackCardImagePath() {
        return backCardImagePath;
    }

    public void setVisible() {
        this.isVisible = true;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public Card getType() {
        return type;
    }

    /**
     * Rotates the component 90 degrees clockwise.
     */
    public void rotate() {
        ConnectorType temp = frontConnector;
        frontConnector = leftConnector;
        leftConnector = rearConnector;
        rearConnector = rightConnector;
        rightConnector = temp;
        switch (orientation){
            case UP: orientation = Direction.RIGHT; break;
            case RIGHT: orientation = Direction.DOWN; break;
            case DOWN: orientation = Direction.LEFT; break;
            case LEFT: orientation = Direction.UP; break;
        }
    }

    /**
     * Rotates to match a specific target orientation (minimal steps).
     */
    public void setOrientation(Direction orientation) {
        while (this.orientation != orientation) {
            rotate();
        }
    }

    /**
     * Rotates the current orientation 90 degrees clockwise.
     *
     * @param dir the current direction
     * @return the new direction after rotation
     */
    private Direction rotateClockwise(Direction dir) {
        return switch (dir) {
            case UP -> Direction.RIGHT;
            case RIGHT -> Direction.DOWN;
            case DOWN -> Direction.LEFT;
            case LEFT -> Direction.UP;
        };
    }

    /**
     * Returns the current orientation of the component.
     */
    public Direction getOrientation() {
        return orientation;
    }

    /**
     * Returns the connector type at a given side considering current orientation.
     */
    public ConnectorType getConnectorAt(Side side) {
        return switch (side) {
            case FRONT -> frontConnector;
            case REAR -> rearConnector;
            case LEFT -> leftConnector;
            case RIGHT -> rightConnector;
        };
    }



    public ShipBoard getShipBoard() {
        return shipBoard;
    }

    public void setShipBoard(ShipBoard ship) {
        this.shipBoard = ship;
    }

    /**
     * Hooks for lifecycle events: override in subclasses if needed.
     */
    public abstract void added();
    public abstract void removed();

    public String getImagePath() {
        return imagePath;
    }

    protected void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }



    public String[] renderSmall(){
        return null;
    }

    public String[] renderBig(){
        return null;
    }




//    public void reserved() {
//        // to be implemented if needed
//    }

    @Override
    public abstract SpaceshipComponent clone();
}
