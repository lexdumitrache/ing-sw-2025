package Model.Ship;

import Controller.Enums.MatchLevel;
import Model.Enums.*;
import Model.Exceptions.InvalidMethodParameters;
import Model.Ship.Components.Cannon;
import Model.Ship.Components.Engine;
import Model.Ship.Components.SpaceshipComponent;


import java.io.Serializable;
import java.util.*;


/**
 * Full-featured ShipBoard for Galaxy Trucker.
 * Manages placement, connections, rotations, integrity, firepower, thrust, shields, damage, and exposed connectors.
 */
public class ShipBoard implements Serializable, Cloneable {
    private static final int ROWS = 5;
    private static final int COLS = 7;

    private final SpaceshipComponent[][] components;
    private SpaceshipComponent activeComponent;
    private final List<SpaceshipComponent> reservedComponents;
    private final CondensedShip condensedShip;
    private boolean isValid = false;

    public ShipBoard() {
        this.components = new SpaceshipComponent[ROWS][COLS];
        this.condensedShip = new CondensedShip();



        this.activeComponent = null;
        this.reservedComponents = new ArrayList<>();
    }

    public CondensedShip getCondensedShip() {
        return condensedShip;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    /**
     * Places a spaceship component on the board at the specified coordinates.
     * Ensures the placement is within board bounds, the position is unoccupied,
     * and the component is properly connected to existing components via compatible connectors.
     *
     * @param component The spaceship component to add.
     * @param coordinates The position on the board where the component should be placed.
     * @throws InvalidMethodParameters if the position is invalid, already occupied, or not connected properly.
     */
    public void addComponent(SpaceshipComponent component, Coordinates coordinates) throws InvalidMethodParameters {
        int i = coordinates.getI()-5;
        int j = coordinates.getJ()-4;

        if (i < 0 || i >= ROWS || j < 0 || j >= COLS) throw new InvalidMethodParameters("Invalid coordinates out of bounds");
        if (components[i][j] != null) throw new InvalidMethodParameters("Position already occupied");

        components[i][j] = component;

    }

    /**
     *  Checks if a position is adjacent to an existing component on the board.
     *
     * @param coordinates The position on the board to check.
     * @return true if the component can be placed, false otherwise.
     */
    public boolean isAdjacentToExistingComponent(Coordinates coordinates) {
        int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

        int row= coordinates.getI() - 5;
        int col = coordinates.getJ() - 4;

        for (int[] d : dirs) {
            int newRow = row + d[0];
            int newCol = col + d[1];
            if (isValidPosition(newRow, newCol) && components[newRow][newCol] != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Removes a spaceship component from the board at the given coordinates.
     * Translates logical coordinates into grid indices and clears the position if it contains a component.
     * Throws an exception if coordinates are out of bounds.
     *
     * @param coordinates the location from which to remove the component
     * @throws InvalidMethodParameters if the coordinates are invalid
     */
    public void removeComponent(Coordinates coordinates) throws InvalidMethodParameters {
        int i = coordinates.getI()-5;
        int j = coordinates.getJ()-4;

        if (i < 0 || i >= ROWS || j < 0 || j >= COLS) throw new InvalidMethodParameters("Invalid coordinates out of bounds");
        if (components[i][j] != null) {
            components[i][j].removed();
            components[i][j].setShipBoard(null);
            components[i][j] = null;
        }
    }

    /**
     * Checks whether the given row and column indices are within the bounds of the ship grid.
     *
     * @param row the row index to check
     * @param col the column index to check
     * @return true if the indices are within bounds, false otherwise
     */
    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < ROWS && col >= 0 && col < COLS;
    }



    /**
     * DOES NOT Checks whether the ship board is entirely empty.
     * Does check if there are no components but in central cabin position
     *
     * @return true if all cells in the board are null, false otherwise
     */
    public boolean isEmpty() {
//        for (SpaceshipComponent[] row : components) {
//            for (SpaceshipComponent c : row) {
//                if (c != null) return false;
//            }
//        }

        for(int i=0; i<ROWS; i++){
            for(int j=0; j<COLS; j++){
                if (components[i][j] != null) {
                    if ((i!=2)||(j!=3)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }


    /**
     * Checks if the given component can be connected to any existing component on the board
     * by verifying adjacent connectors for compatibility.
     *
     * @param component the component to place
     * @param row the target row in the board grid
     * @param col the target column in the board grid
     * @return true if there is at least one compatible adjacent connector, false otherwise
     */
    public boolean isConnectedToExistingComponents(SpaceshipComponent component, int row, int col) {
        for (Side side : Side.values()) {
            int[] offset = getOffset(side);
            int adjRow = row + offset[0];
            int adjCol = col + offset[1];
            if (isValidPosition(adjRow, adjCol) && components[adjRow][adjCol] != null) {
                SpaceshipComponent neighbor = components[adjRow][adjCol];
                if (connectorsAreConnected(
                        component.getConnectorAt(side),
                        neighbor.getConnectorAt(getOppositeSide(side))
                )) {
                    return true;
                }
            }
        }
        return false;



    }



    /**
     * Returns the relative coordinate offset for a given side of a component.
     * Used to find neighboring positions in the grid.
     *
     * @param side the side of the component (FRONT, REAR, LEFT, RIGHT)
     * @return an array containing the offset as [rowDelta, colDelta]
     */
    private int[] getOffset(Side side) {
        return switch (side) {
            case FRONT -> new int[]{-1, 0};
            case REAR -> new int[]{1, 0};
            case LEFT -> new int[]{0, -1};
            case RIGHT -> new int[]{0, 1};
        };
    }

    /**
     * Returns the opposite side of the given side.
     * Used to determine the side of a neighboring component for connection checks.
     *
     * @param side the original side
     * @return the side opposite to the input
     */
    private Side getOppositeSide(Side side) {
        return switch (side) {
            case FRONT -> Side.REAR;
            case REAR -> Side.FRONT;
            case LEFT -> Side.RIGHT;
            case RIGHT -> Side.LEFT;
        };
    }

    /**
     * Checks if two connector types are compatible.
     * NONE is not compatible with anything, UNIVERSAL is compatible with everything except NONE,
     * SINGLE is compatible with itself and UNIVERSAL, DOUBLE is compatible with itself and UNIVERSAL.
     *
     * @param a the first connector type
     * @param b the second connector type
     * @return true if the connectors are compatible, false otherwise
     */
    private boolean connectosAreCompatible(ConnectorType a, ConnectorType b) {
        //NONE is compatible with NONE
        // UNIVERSAL is compatible with anything that is not NONE
        // SINGLE is compatible with itself and UNIVERSAL
        // DOUBLE is compatible with itself and UNIVERSAL

        if(a==b) return true; // same type is always compatible
        if(a == ConnectorType.NONE || b == ConnectorType.NONE) return false; // NONE is not compatible with anything
        if(a == ConnectorType.UNIVERSAL || b == ConnectorType.UNIVERSAL) return true; // UNIVERSAL is compatible with anything that is not NONE
        return false;

    }

    /**
     * Determines if two connectors are connected with each other. (this means that they are compatible)
     * NONE is not connected with anything
     * Universal connectors are compatible with any connector,
     *
     * @param a the first connector
     * @param b the second connector
     * @return true if connectors are compatible, false otherwise
     */
    private boolean connectorsAreConnected(ConnectorType a, ConnectorType b) {
        if (a == ConnectorType.NONE || b == ConnectorType.NONE) return false;
        if (a == ConnectorType.UNIVERSAL || b == ConnectorType.UNIVERSAL) return true;
        return a == b;
    }

    /**
     * Sets the active component currently being manipulated or selected.
     *
     * @param component the component to mark as active
     */
    public void setActiveComponent(SpaceshipComponent component) {
        this.activeComponent = component;
    }
    /**
     * Retrieves the currently active component.
     *
     * @return the active component instance, or null if none is set
     */
    public SpaceshipComponent getActiveComponent() {
        return activeComponent;
    }

    /**
     * Checks whether the ship is still structurally connected after potential damage.
     * Uses depth-first search to ensure all components are connected starting from the first one found.
     *
     * @return true if all components are reachable and the ship is whole, false otherwise
     */
    public boolean checkIntegrity() {
        boolean[][] visited = new boolean[ROWS][COLS];
        int totalComponents = 0;
        int startRow = -1, startCol = -1;

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (components[i][j] != null) {
                    totalComponents++;
                    if (startRow == -1) {
                        startRow = i;
                        startCol = j;
                    }
                }
            }
        }

        if (totalComponents == 0) return true;

        int visitedComponents = dfs(startRow, startCol, visited);
        return visitedComponents == totalComponents;
    }

    public boolean areComponentsConnected(SpaceshipComponent a, SpaceshipComponent b) {
        if (a == null || b == null) return false;

        Coordinates aCoords = getIndex(a);
        Coordinates bCoords = getIndex(b);

        if (aCoords.getI() == -1 || bCoords.getI() == -1) return false; // one of the components is not on the board

        if(aCoords.manhattanDistance(bCoords) != 1) return false; // components are not adjacent

        // Check if the connectors are compatible

        int deltaI = bCoords.getI() - aCoords.getI();
        int deltaJ = bCoords.getJ() - aCoords.getJ();

        ConnectorType aConnector;
        ConnectorType bConnector;

        if(deltaJ==1){
            //b is at the right of a
            aConnector = a.getConnectorAt(Side.RIGHT);
            bConnector = b.getConnectorAt(Side.LEFT);
        } else if (deltaJ==-1) {
            //b is at the left of a
            aConnector = a.getConnectorAt(Side.LEFT);
            bConnector = b.getConnectorAt(Side.RIGHT);
        } else {
           if (deltaI==-1) {
                //b is below a
                aConnector = a.getConnectorAt(Side.FRONT);
                bConnector = b.getConnectorAt(Side.REAR);
           } else if (deltaI==1) {
               //b is above a
               aConnector = a.getConnectorAt(Side.REAR);
               bConnector = b.getConnectorAt(Side.FRONT);
           }else{
               throw new RuntimeException("Error in areComponentsConnected: components are not adjacent, even though they checked by manhattan distance");
           }
        }


        return connectorsAreConnected(aConnector, bConnector);

    }

    /**
     * Performs a depth-first search to count connected components starting from a given cell.
     *
     * @param row the row to start from
     * @param col the column to start from
     * @param visited a matrix to mark visited cells
     * @return the total number of connected components found
     */
    private int dfs(int row, int col, boolean[][] visited) {
        //if (!isValidPosition(row, col)) return 0;
        if (visited[row][col] || components[row][col] == null) {return 0;}

        visited[row][col] = true;
        int count = 1;
        SpaceshipComponent currentComponent = components[row][col];
        // Explore each of the four directions, only if the tile in that direction is not null and it's connected with the current component


        SpaceshipComponent nextComponent = null;
        if (isValidPosition(row+1, col)) {
            nextComponent = components[row+1][col];
            if(nextComponent!=null) {
                ConnectorType currentComponentConnector = currentComponent.getConnectorAt(Side.REAR);
                ConnectorType nextComponentConnector = nextComponent.getConnectorAt(Side.FRONT);

                if(!connectosAreCompatible(currentComponentConnector, nextComponentConnector)) {
                    System.out.println("Uncompatible connectors between element at " + (row+5) + ", " + (col+4)+"and element at " + (row+6) + ", " + (col+4));
                    return -1;
                    //-1 makes checkIntegrity return false,
                    // this is not mandatory for ship integrity, but it's a simple check that is useful for validation
                }

                if (connectorsAreConnected(currentComponentConnector, nextComponentConnector)) {
                    count += dfs(row + 1, col, visited);
                }
            }
        }

        if (isValidPosition(row-1, col)) {
            nextComponent = components[row-1][col];
            if(nextComponent!=null) {
                ConnectorType currentComponentConnector = currentComponent.getConnectorAt(Side.FRONT);
                ConnectorType nextComponentConnector = nextComponent.getConnectorAt(Side.REAR);
                if(!connectosAreCompatible(currentComponentConnector, nextComponentConnector)) {
                    System.out.println("Uncompatible connectors between element at " + (row+5) + ", " + (col+4)+"and element at " + (row+4) + ", " + (col+4));
                    return -1;
                    //-1 makes checkIntegrity return false,
                    // this is not mandatory for ship integrity, but it's a simple check that is useful for validation
                }
                if (connectorsAreConnected(currentComponentConnector, nextComponentConnector)) {
                    count += dfs(row - 1, col, visited);
                }
            }
        }

        if (isValidPosition(row, col+1)) {
            nextComponent = components[row][col + 1];
            if(nextComponent!=null) {

                ConnectorType currentComponentConnector = currentComponent.getConnectorAt(Side.RIGHT);
                ConnectorType nextComponentConnector = nextComponent.getConnectorAt(Side.LEFT);
                if(!connectosAreCompatible(currentComponentConnector, nextComponentConnector)) {
                    System.out.println("Uncompatible connectors between element at " + (row+5) + ", " + (col+4)+"and element at " + (row+5) + ", " + (col+5));
                    return -1;
                    //-1 makes checkIntegrity return false,
                    // this is not mandatory for ship integrity, but it's a simple check that is useful for validation
                }

                if(connectorsAreConnected(currentComponentConnector, nextComponentConnector)) {
                    count += dfs(row, col + 1, visited);
                }
            }
        }


        if (isValidPosition(row, col-1)) {
            nextComponent = components[row][col - 1];
            if(nextComponent!=null) {
                ConnectorType currentComponentConnector = currentComponent.getConnectorAt(Side.LEFT);
                ConnectorType nextComponentConnector = nextComponent.getConnectorAt(Side.RIGHT);
                if(!connectosAreCompatible(currentComponentConnector, nextComponentConnector)) {
                    System.out.println("Uncompatible connectors between element at " + (row+5) + ", " + (col+4)+"and element at " + (row+5) + ", " + (col+3));
                    return -1;
                    //-1 makes checkIntegrity return false,
                    // this is not mandatory for ship integrity, but it's a simple check that is useful for validation
                }

                if(connectorsAreConnected(currentComponentConnector, nextComponentConnector)) {
                    count += dfs(row, col - 1, visited);
                }
            }
        }


        return count;
    }

    /**
     * Keeps only the components connected to the given starting position.
     * All other components will be removed from the board. This can be used after damage
     * to preserve a specific part of the ship and remove all detached segments.
     *
     * @param origin the starting position of the ship part to preserve
     */
//    public void keepOnlyConnectedFrom(Position origin) {
//        Set<Position> connected = new HashSet<>();
//        Queue<Position> toVisit = new LinkedList<>();
//        toVisit.add(origin);
//
//        while (!toVisit.isEmpty()) {
//            Position current = toVisit.poll();
//            if (connected.contains(current)) continue;
//            connected.add(current);
//
//            SpaceshipComponent currentComp = components[current.getX()][current.getY()];
//            if (currentComp == null) continue;
//
//            for (Side side : Side.values()) {
//                int[] offset = getOffset(side);
//                int adjRow = current.getX() + offset[0];
//                int adjCol = current.getY() + offset[1];
//
//                if (!isValidPosition(adjRow, adjCol)) continue;
//
//                SpaceshipComponent neighborComp = components[adjRow][adjCol];
//                if (neighborComp == null) continue;
//
//                ConnectorType thisConnector = currentComp.getConnectorAt(side);
//                ConnectorType neighborConnector = neighborComp.getConnectorAt(getOppositeSide(side));
//
//                if (connectorsAreConnected(thisConnector, neighborConnector)) {
//                    toVisit.add(new Position(adjRow, adjCol));
//                }
//            }
//        }
//
//        for (int row = 0; row < components.length; row++) {
//            for (int col = 0; col < components[0].length; col++) {
//                Position pos = new Position(row, col);
//                if (!connected.contains(pos)) {
//                    components[row][col] = null;
//                }
//            }
//        }
//    }



    /**
     * Calculates the rectangular boundary of the ship on the grid.
     * Iterates through all cells to find the minimum and maximum rows and columns
     * where components are placed. Returns null if the ship is empty.
     *
     * @return an array of four integers: [minRow, maxRow, minCol, maxCol], or null if empty
     */
    public int[] getShipBoundaries() {
        int minRow = ROWS, maxRow = -1, minCol = COLS, maxCol = -1;
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (components[i][j] != null) {
                    if (i < minRow) minRow = i;
                    if (i > maxRow) maxRow = i;
                    if (j < minCol) minCol = j;
                    if (j > maxCol) maxCol = j;
                }
            }
        }
        if (maxRow == -1) return null;
        return new int[]{minRow, maxRow, minCol, maxCol};
    }

    /**
     * Returns a list of all components currently placed on the ship board.
     * Iterates through the grid and collects all non-null components.
     *
     * @return a list of all spaceship components on the board
     */
    public List<SpaceshipComponent> getAllComponents() {
        List<SpaceshipComponent> list = new ArrayList<>();
        for (SpaceshipComponent[] row : components) {
            for (SpaceshipComponent c : row) {
                if (c != null) list.add(c);
            }
        }
        return list;
    }

    /**
     * Retrieves the component located at the given coordinates.
     * Converts logical coordinates to grid indices and returns the component if within bounds.
     *
     * @param coordinates the logical board coordinates
     * @return the component at the specified location, or null if out of bounds or empty
     */
    public SpaceshipComponent getComponent(Coordinates coordinates) {
        int i = coordinates.getI()-5;
        int j = coordinates.getJ()-4;
        if (i < 0 || i >= ROWS || j < 0 || j >= COLS) return null;
        return components[i][j];
    }

    /**
     * Searches for the specified component in the grid and returns its coordinates.
     * If not found, returns (-1, -1).
     *
     * @param goalComponent the component to locate on the board
     * @return the coordinates of the component or (-1, -1) if not found
     */
    public Coordinates getIndex(SpaceshipComponent goalComponent) {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (components[i][j] == goalComponent) {
                    return new Coordinates(i+5, j+4);
                }
            }
        }
        return new Coordinates(-1, -1);
    }

    /**
     * Prints a visual matrix of the ship grid to the console.
     * Displays '[X]' for occupied positions and '[ ]' for empty ones
     * within the active ship boundaries.
     */
    public void printOccupiedMatrix() {
        int[] bounds = getShipBoundaries();
        if (bounds == null) {
            System.out.println("Nave vuota.");
            return;
        }
        int minRow = bounds[0], maxRow = bounds[1], minCol = bounds[2], maxCol = bounds[3];
        for (int i = minRow; i <= maxRow; i++) {
            for (int j = minCol; j <= maxCol; j++) {
                System.out.print(components[i][j] != null ? "[X]" : "[ ]");
            }
            System.out.println();
        }
    }


//    /**
//     * Calculates the total firepower of the ship.
//     * Considers all cannon components and their orientation relative to the ship's forward direction.
//     *
//     *
//     * @return the sum of effective power from all cannons
//     */
//
//    public int calculateFirepower(Direction shipForward) {
//        int firepower = 0;
//        for (int row = 0; row < ROWS; row++) {
//            for (int col = 0; col < COLS; col++) {
//                SpaceshipComponent component = components[row][col];
//                if (component != null) {
//                    firepower += component.getFirepower(shipForward);
//                }
//            }
//        }
//        return firepower;
//    }
//
//
//    /**
//     * Calculates total thrust considering engine orientation.
//     */
//    /**
//     * Calculates the total thrust produced by all engines correctly oriented to the rear of the ship.
//     * Only engines that face the specified rear direction contribute their engine power.
//     *
//     * @param shipRear the direction that represents the rear of the ship
//     * @return the total thrust value from all correctly oriented engines
//     */
//    public int calculateThrust(Direction shipRear) {
//        int thrust = 0;
//        for (int row = 0; row < ROWS; row++) {
//            for (int col = 0; col < COLS; col++) {
//                SpaceshipComponent component = components[row][col];
//                if (component != null) {
//                    thrust += component.getThrust(shipRear);
//                }
//            }
//        }
//        return thrust;
//    }



    /**
     * Checks if a component at a given position is protected by an active shield facing a direction.
     * Accepts incoming side (FRONT, REAR, LEFT, RIGHT) and converts it to Direction internally.
     */
    /**
     * Determines if the component at the specified position is protected by a shield
     * from the direction corresponding to the incoming side.
     * Converts the side to a direction and checks adjacent shield generators facing that direction.
     *
     * @param pos the position of the component being checked
     * @param incomingSide the side from which the attack is coming
     * @return true if the component is protected by a shield, false otherwise
     */
//    public boolean isProtectedByShield(Position pos, Side incomingSide) {
//        int row = pos.getX();
//        int col = pos.getY();
//        if (!isValidPosition(row, col)) return false;
//
//        Direction incomingDirection = DirectionSideUtils.convertSideToDirection(incomingSide);
//
//        switch (incomingDirection){
//            case UP:
//                if(condensedShip.getShields().getNorthShields() > 0)
//                    return true;
//                break;
//            case RIGHT:
//                if(condensedShip.getShields().getEastShields() > 0)
//                    return true;
//                break;
//            case DOWN:
//                if(condensedShip.getShields().getSouthShields() > 0)
//                    return true;
//                break;
//            case LEFT:
//                if(condensedShip.getShields().getWestShields() > 0)
//                    return true;
//                break;
//        }
//        return false;
//    }

    /**
     * Applies damage to a given position if it is not protected by a shield.
     */
    /**
     * Applies damage to the component at the given position.
     * If the component is not protected by a shield in the incoming direction, it is removed (set to null).
     *
     * @param pos the position to apply damage to
     * @param incomingSide the side from which the damage is incoming
     */
//    public void applyDamage(Position pos, Side incomingSide) {
//        int row = pos.getX();
//        int col = pos.getY();
//        if (!isValidPosition(row, col)) return;
//
//        if (components[row][col] != null) {
//            if (!isProtectedByShield(pos, incomingSide)) {
//                components[row][col] = null;
//            }
//        }
//    }



    /**
     * Validates the structure and rules compliance of the current ship configuration.
     * Checks for connector compatibility, blocked engines or cannons, disconnected cabins,
     * and special component requirements like alien life support adjacency.
     *
     * @return true if the ship structure is valid according to game rules, false otherwise
     */
    /**
     * Validates the ship by checking:
     * - Connector compatibility
     * - Blocking of engines and cannons
     * - Proper orientation of engines and cannons
     * - Special rules for cabins, alien support, shields, and special cargo
     *
     * @return true if the ship is valid, false otherwise
     */
    public boolean validateShip() {

        if(!this.checkIntegrity()) {
            System.out.println("La nave non è strutturalmente integra.");
            return false;
        }

        //check that


        //controllare che tutti i motori siano orientati verso la parte posteriore della nave e che non abbiano alti pezzi nella posizione sotto di loro


        for(Engine engine : condensedShip.getEnginesList()) {
            if(!engine.getOrientation().equals(Direction.UP)) {
                System.out.println("Motore non è orientato verso la parte posteriore della nave.");
                return false;
            }

            Coordinates engineCoords = getIndex(engine);
            int row = engineCoords.getI() - 5;
            int col = engineCoords.getJ() - 4;

            // Check the position below the engine
            if (isValidPosition(row + 1, col) && components[row + 1][col] != null) {
                System.out.println("Motore bloccato da un altro pezzo sotto di esso.");
                return false;
            }
        }

        //controllare che tutti i cannoni  non abbiano alti pezzi nella direzione in cui sparano


        for (Cannon cannon : condensedShip.getCannons()) {
            Coordinates cannonCoords = getIndex(cannon);
            int row = cannonCoords.getI() - 5;
            int col = cannonCoords.getJ() - 4;

            // Check the direction the cannon is facing
            Direction cannonDirection = cannon.getOrientation();

            //crea un array di offset per la direzione del cannone, inizalizzandolo a 0, 0
            int[] offset = new int[2];


            switch (cannonDirection){
                case UP:
                    offset[0] = -1; // row offset for UP
                    offset[1] = 0;  // col offset for UP
                    break;
                case DOWN:
                    offset[0] = 1;  // row offset for DOWN
                    offset[1] = 0;  // col offset for DOWN
                    break;
                case LEFT:
                    offset[0] = 0;  // row offset for LEFT
                    offset[1] = -1; // col offset for LEFT
                    break;
                case RIGHT:
                    offset[0] = 0;  // row offset for RIGHT
                    offset[1] = 1;  // col offset for RIGHT
                    break;
            }


            int targetRow = row + offset[0];
            int targetCol = col + offset[1];

            if (isValidPosition(targetRow, targetCol) && components[targetRow][targetCol] != null) {
                System.out.println("Cannon blocked by another piece in its firing direction.");
                return false;
            }

        }

        System.out.println("Validazione nave completata con successo.");
        return true;
    }


    /**
     * Identifies all spaceship components on the board with exposed connectors.
     * A connector is considered exposed if it is adjacent to an empty cell, out-of-bounds space,
     * or incompatible connector. The method uses flood-fill to distinguish interior voids
     * from actual exposure to external space.
     *
     * @return a map linking each exposed component position to a list of its exposed sides
     */
    public Map<Coordinates, List<Side>> getExposedConnectors() {
        Map<Coordinates, List<Side>> exposedConnectors = new HashMap<>();

        // Check each component for exposed connectors
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                SpaceshipComponent component = components[row][col];
                if (component == null) continue;

                List<Side> exposedSides = new ArrayList<>();

                // Check each side of the component
                for (Side side : Side.values()) {
                    ConnectorType connector = component.getConnectorAt(side);

                    // Skip sides without connectors
                    if (connector == ConnectorType.NONE) continue;

                    // Get adjacent position
                    int[] offset = getOffset(side);
                    int adjRow = row + offset[0];
                    int adjCol = col + offset[1];

                    boolean isExposed = false;

                    // Check if connector is exposed
                    if (!isValidPosition(adjRow, adjCol)) {
                        // Adjacent position is out of bounds - connector is exposed
                        isExposed = true;
                    } else if (components[adjRow][adjCol] == null) {
                        // Adjacent position is empty - connector is exposed
                        isExposed = true;
                    } else {
                        // Adjacent position has a component - check connector compatibility
                        SpaceshipComponent neighborComponent = components[adjRow][adjCol];
                        ConnectorType neighborConnector = neighborComponent.getConnectorAt(getOppositeSide(side));
                        isExposed = !connectorsAreConnected(connector, neighborConnector);
                        System.out.println("\n\n\n\nThere is a Non valid Ship Flying!\nGetExposedConnectors found a single connector connected to a double connector\n\n\n");
                    }

                    if (isExposed) {
                        exposedSides.add(side);
                    }
                }

                // Add component to result if it has exposed connectors
                if (!exposedSides.isEmpty()) {
                    exposedConnectors.put(new Coordinates(row, col), exposedSides);
                }
            }
        }

        return exposedConnectors;
    }


    /**
     * Utility method to count the total number of exposed connectors across all components.
     * This method should be used when counting individual exposed connectors rather than
     * just the number of components with exposed connectors.
     *
     * @return the total count of individual exposed connectors
     */
    public int getTotalExposedConnectorCount() {
        int totalCount = 0;
        Map<Coordinates, List<Side>> exposedConnectors = getExposedConnectors();

        for (List<Side> exposedSides : exposedConnectors.values()) {
            totalCount += exposedSides.size();
        }

        return totalCount;
    }



    /**
     * Adds a component to the reserved components list.
     * Throws an exception if more than 2 are reserved.
     * @param component the component to reserve
     */
    public void reserveComponent(SpaceshipComponent component) {
        if (reservedComponents.size() >= 2) {
            throw new IllegalStateException("You can only reserve up to 2 components.");
        }
        reservedComponents.add(component);
    }

    /**
     * Returns a copy of the reserved components list.
     * @return list of reserved components
     */
    public List<SpaceshipComponent> getReservedComponents() {
        return new ArrayList<>(reservedComponents);
    }

    /**
     * Removes a reserved component by index.
     *
     * @param index the index to remove
     */
    public void removeReservedComponent(int index) {
        reservedComponents.remove(index);
    }

    /**
     * Removes all components that are no longer connected to the central part of the ship.
     * Performs a DFS from the center and removes anything not visited.
     */
    public void purgeDisconnectedComponents() {
        int centerRow = components.length / 2;
        int centerCol = components[0].length / 2;

        if (components[centerRow][centerCol] == null) return;

        boolean[][] visited = new boolean[components.length][components[0].length];
        dfs(centerRow, centerCol, visited);

        for (int row = 0; row < components.length; row++) {
            for (int col = 0; col < components[0].length; col++) {
                if (components[row][col] != null && !visited[row][col]) {
                    components[row][col] = null;
                }
            }
        }
    }

    public void render(MatchLevel level){
        // Prima stampiamo l'intestazione con i numeri delle colonne
        System.out.print("   "); // spazio per i numeri di riga
        for (int j = 0; j < COLS; j++) {
            System.out.printf("   %d   ", j + 4); // o qualsiasi offset tu voglia
        }
        System.out.println();

        String[] casellaA = null, casellaB = null, casellaC = null;

        // Active component slot (HAND)
        if (activeComponent != null) {
            casellaA = activeComponent.renderBig();
        } else {
            casellaA = renderEmptyBig();
        }

        // First reserved component slot
        if (!reservedComponents.isEmpty()) {
            casellaB = reservedComponents.get(0).renderBig();
        } else {
            casellaB = renderEmptyBig();
        }

        // Second reserved component slot
        if (reservedComponents.size() > 1) {
            casellaC = reservedComponents.get(1).renderBig();
        } else {
            casellaC = renderEmptyBig();
        }

        // Ora stampiamo riga per riga componendo i pezzi
        for (int i = 0; i < ROWS; i++) { // righe da 5 a 9
            // Prima otteniamo i disegni di tutti i componenti della riga i
            String[][] draw = new String[7][]; // 7 colonne (4-10)
            for (int j = 0; j < COLS; j++) {
                // Controlla se questa casella deve essere vuota secondo le tue regole
                boolean needsToBeEmpty = false;

                if (level == MatchLevel.TRIAL) {
                    // Prima e ultima colonna (j=0 e j=6)
                    if (j == 0 || j == 6) {
                        needsToBeEmpty = true;
                    }
                    // Prime due dall'alto della seconda e penultima colonna (j=1 e j=5, i=0 e i=1)
                    else if ((j == 1 || j == 5) && (i == 0 || i == 1)) {
                        needsToBeEmpty = true;
                    }
                    // Prima della terza e quinta colonna (j=2 e j=4, i=0)
                    else if ((j == 2 || j == 4) && i == 0) {
                        needsToBeEmpty = true;
                    }
                    // Ultima della quarta colonna (j=3, i=ROWS-1)
                    else if (j == 3 && i == ROWS - 1) {
                        needsToBeEmpty = true;
                    }
                } else {
                    if( (j == 0 || j == 1 || j == 3 || j == 5 || j == 6) && i == 0 ){
                        needsToBeEmpty = true;
                    } else if (i == 1 && (j == 0 || j == 6)){
                        needsToBeEmpty = true;
                    } else if (j == 3 && i == ROWS - 1) {
                        needsToBeEmpty = true;
                    }
                }

                // Scegli il rendering appropriato
                if (needsToBeEmpty) {
                    draw[j] = renderNull(); // Usa il tuo metodo empty()
                } else if (components[i][j] != null) {
                    draw[j] = components[i][j].renderSmall();
                } else {
                    draw[j] = renderEmpty();
                }
            }



            // Poi stampiamo riga per riga il disegno
            for (int riga = 0; riga < draw[0].length; riga++) {
                // Stampiamo il numero di riga solo sulla riga centrale del componente
                if (riga == draw[0].length / 2) {
                    System.out.printf("%2d ", i + 5);
                } else {
                    System.out.print("   ");
                }

                // Stampiamo la griglia principale
                for (int j = 0; j < 7; j++) {
                    System.out.print(draw[j][riga]);
                }

                System.out.print("     "); // spazio separatore

                // Print extra slots with labels, split across multiple rows
                if (i == 0 && casellaA != null) { // First 3 lines of active component slot
                    System.out.print(casellaA[riga]);
                    if (riga == draw[0].length / 2) {
                        System.out.print(" HAND");
                    }
                } else if (i == 1 && casellaA != null) { // Last 3 lines of active component slot
                    System.out.print(casellaA[riga + 3]);
                } else if (i == 2) { // First 3 lines of reserved component slots
                    if (casellaB != null) System.out.print(casellaB[riga]);
                    if (casellaC != null) System.out.print(casellaC[riga]);
                    if (riga == draw[0].length / 2) {
                        System.out.print(" RESERVED");
                    }
                } else if (i == 3) { // Last 3 lines of reserved component slots
                    if (casellaB != null) System.out.print(casellaB[riga + 3]);
                    if (casellaC != null) System.out.print(casellaC[riga + 3]);
                }

                System.out.println();
            }
        }

        // Informazioni dalla condensedShip
        System.out.println("\nSHIP STATUS:");
        System.out.println("Batteries: "+ condensedShip.getTotalBatteries() +"  Crew: " + condensedShip.getTotalCrew());
        System.out.println("Power: " + condensedShip.getBasePower() + "  Thrust: " + condensedShip.getBaseThrust());

        // Legenda su 3 colonne
        System.out.println("\nLEGENDA:");

        // Array con i tuoi contenuti personalizzati
        String[] legenda = {
                "BAT - Battery compartment",
                "C-S - Special Cargo hold",
                "C-B - Base Cargo hold",
                "CAB - Cabin",
                "STR - Structural Module",
                "E1  - Single Engine",
                "E2  - Double Engine",
                "C1  - Single Cannon",
                "C2  - Double Cannon",
                "PAL - Purple Alien",
                "BAL - Brown Alien",
                "SH  - Shield",
                "↑   - Orientation"
        };

        // Stampa su 3 colonne
        for (int i = 0; i < legenda.length; i += 3) {
            // Prima colonna
            System.out.printf("%-30s", legenda[i]);

            // Seconda colonna (se esiste)
            if (i + 1 < legenda.length) {
                System.out.printf("%-30s", legenda[i + 1]);
            } else {
                System.out.printf("%-30s", "");
            }

            // Terza colonna (se esiste)
            if (i + 2 < legenda.length) {
                System.out.printf("%-25s", legenda[i + 2]);
            }

            System.out.println();
        }
    }

    public String[] renderEmpty() {
        // Disegno vuoto con linee singole
        String[] righe = new String[3];
        righe[0] = "┌─────┐";
        righe[1] = "│     │";
        righe[2] = "└─────┘";
        return righe;
    }

    public String[] renderNull() {
        // Disegno vuoto con linee doppie
        String[] righe = new String[3];
        righe[0] = "       ";
        righe[1] = "       ";
        righe[2] = "       ";
        return righe;
    }

    @Override
    public ShipBoard clone() {
        return new ShipBoard(this);
    }

    private ShipBoard(ShipBoard old){
        this.components = new SpaceshipComponent[ROWS][COLS];
        for(int i = 0; i < ROWS; i++){
            for(int j = 0; j < COLS; j++){
                this.components[i][j] = (old.components[i][j] != null) ? old.components[i][j].clone() : null;
            }
        }

        this.activeComponent = (old.activeComponent != null) ? old.activeComponent.clone() : null;

        this.reservedComponents = new ArrayList<>();

        for(SpaceshipComponent component : old.reservedComponents){
            this.reservedComponents.add((component != null) ? component.clone() : null);
        }

        this.condensedShip = old.condensedShip.clone();
        this.isValid = old.isValid;
    }

    /**
     * Renders an empty slot with the same dimensions as renderBig().
     *
     * @return array of 5 strings representing an empty big slot
     */
    public String[] renderEmptyBig() {
        String[] righe = new String[6]; // Stesso numero di righe di renderBig()
        righe[0] = "┌─────────┐";
        righe[1] = "│         │";
        righe[2] = "│         │";
        righe[3] = "│         │";
        righe[4] = "│         │";
        righe[5] = "└─────────┘";
        return righe;
    }
}


