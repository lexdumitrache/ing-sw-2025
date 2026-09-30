package Model.Ship.Components;

import Model.Enums.Card;
import Model.Enums.ConnectorType;
import Model.Enums.Good;
import Model.Enums.Side;
import Model.Ship.ShipBoard;
import com.google.gson.JsonObject;

/**
 * Represents a CargoHold component used to store goods in the ship.
 * Special cargo holds can contain red (dangerous) goods, while standard holds cannot.
 */
public class CargoHold extends SpaceshipComponent {
    private final int capacity;        // Number of slots available in the hold
    private final Good[] goods;              // Goods currently stored in the cargo hold
    private final boolean isSpecial;         // True if this cargo hold can carry red goods

    /**
     * Constructor using explicit parameters.
     */
    public CargoHold(Card Type, ConnectorType front, ConnectorType rear, ConnectorType left, ConnectorType right, int capacity, boolean isSpecial) {
        super(Type, front, rear, left, right);
        this.capacity = capacity;
        this.goods = new Good[capacity];
        this.isSpecial = isSpecial;
    }

    /**
     * Constructor that initializes a CargoHold from a JSON object.
     * Used by the ComponentFactory to dynamically load components from configuration.
     */
    public CargoHold(JsonObject json) {
        super(
                Card.valueOf(json.get("type").getAsString().toUpperCase()),
                ConnectorType.valueOf(json.getAsJsonObject("connectors").get("front").getAsString()),
                ConnectorType.valueOf(json.getAsJsonObject("connectors").get("rear").getAsString()),
                ConnectorType.valueOf(json.getAsJsonObject("connectors").get("left").getAsString()),
                ConnectorType.valueOf(json.getAsJsonObject("connectors").get("right").getAsString()),
                json.get("imagePath").getAsString()
        );

        if (json.has("cargoCapacity")) {
            this.capacity = json.get("cargoCapacity").getAsInt();
            this.goods = new Good[this.capacity];
        }else{
            throw new RuntimeException("Missing cargoCapacity in CargoHold JSON configuration" +
                    " at " + json.get("imagePath").getAsString());
        }

        if (json.has("isSpecial")) {
            this.isSpecial = json.get("isSpecial").getAsBoolean();
        } else {
            throw new RuntimeException("Missing isSpecial in CargoHold JSON configuration" +
                    " at " + json.get("imagePath").getAsString());
        }

    }

    public boolean isSpecial(){
        return this.isSpecial;
    }

    public boolean isEmpty(){
        for(int i=0; i<capacity; i++){
            if(goods[i] != null){return false;}
        }
        return true;
    }

    @Override
    public void visualize() {
        super.visualize();
        System.out.println("Cargo Hold Capacity: " + capacity);
        System.out.println("Cargo Hold Special: " + isSpecial);
        System.out.println("Goods in Cargo Hold:");
        System.out.println("==========================");
        System.out.printf("\n\n\n\n");
    }

    public int getCapacity() {
        return capacity;
    }

    public Good[] getGoods() {
        return goods;
    }

    /**
     * Adds a good to the first available slot in the cargo hold.
     * @param good the good to add
     * @return true if successfully added, false otherwise (e.g., no space or red good in normal hold)
     */
    public boolean addGood(Good good) {
        if (good == Good.RED && !isSpecial) {
            return false;
        }
        for (int i = 0; i < goods.length; i++) {
            if (goods[i] == null) {
                goods[i] = good;
                return true;
            }
        }
        return false;
    }

    /**
     * Adds a good to a specific index in the cargo hold.
     * @param good the good to add
     * @param index the index at which to add the good
     * @return true if added successfully, false if the index is invalid, occupied, or not allowed
     */
    public boolean addGoodAt(Good good, int index) {
        if (good == Good.RED && !isSpecial) {
            return false;
        }
        if (index < 0 || index >= goods.length || goods[index] != null) {
            return false;
        }
        goods[index] = good;
        return true;
    }

    /**
     * Removes the good at the specified index.
     * @param index the index of the good to remove
     */
    public void removeGood(int index) {
        if (index >= 0 && index < goods.length) {
            goods[index] = null;
        }
    }

    @Override
    public void added(){
        if(getShipBoard().getCondensedShip().getCargoHolds().contains(this)){
            throw new RuntimeException("Cargo Hold already added to the ship.");
        } else {
            getShipBoard().getCondensedShip().addCargoHold(this);
        }
    }

    @Override
    public void removed() {
        if(!getShipBoard().getCondensedShip().getCargoHolds().contains(this)){
            throw new RuntimeException("Cargo Hold not found in the ship.");
        } else {
            getShipBoard().getCondensedShip().removeCargoHold(this);
        }

    }

    public String[] renderSmall() {
        String[] righe = new String[3];
        righe[0] = String.format("╔═ %s ═╗",
                this.getConnectorAt(Side.FRONT).getNumero() > 0 ?
                        String.valueOf(this.getConnectorAt(Side.FRONT).getNumero()) : "═");
        String sx = (this.getConnectorAt(Side.LEFT).getNumero() > 0 ? String.valueOf(this.getConnectorAt(Side.LEFT).getNumero()) : "║");
        String dx = (this.getConnectorAt(Side.RIGHT).getNumero() > 0 ? String.valueOf(this.getConnectorAt(Side.RIGHT).getNumero()) : "║");
        if (isSpecial) {
            righe[1] = String.format("%s C-S %s", sx, dx);
        }else{
            righe[1] = String.format("%s C-B %s", sx, dx);
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





        if(this.isSpecial){
            righe[1] = "║ SPECIAL ║";
        }else{
            righe[1] = "║ NORMAL  ║";
        }
        righe[2] = "║  CARGO  ║";


        righe[3] = String.format("%s%s%s",
                (this.getConnectorAt(Side.LEFT).getNumero() > 0 ?
                        String.valueOf(this.getConnectorAt(Side.LEFT).getNumero()) : "║"),
                "    " + this.getOrientation().getFreccia(),
                "    " + (this.getConnectorAt(Side.RIGHT).getNumero() > 0 ?
                        String.valueOf(this.getConnectorAt(Side.RIGHT).getNumero()) : "║")
        );

        //righe[4] = "║         ║";

        StringBuilder goodsLine = new StringBuilder("║  ");
        switch(this.getCapacity()) {
            case 1:
                goodsLine.append("  ");
                for (Good good : this.getGoods()) {
                    if (good == null) {
                        goodsLine.append("☐ ");
                    } else {
                        // Prendo la prima lettera del nome dell'enum
                        goodsLine.append(good.name().charAt(0)).append(" ");
                    }
                }
                goodsLine.append("  ");
                break;
            case 2:
                goodsLine.append(" ");
                for (Good good : this.getGoods()) {
                    if (good == null) {
                        goodsLine.append("☐ ");
                    } else {
                        // Prendo la prima lettera del nome dell'enum
                        goodsLine.append(good.name().charAt(0)).append(" ");
                    }
                }
                goodsLine.append(" ");
                break;
            case 3:
                //goodsLine.append(" ");
                for (Good good : this.getGoods()) {
                    if (good == null) {
                        goodsLine.append("☐ ");
                    } else {
                        // Prendo la prima lettera del nome dell'enum
                        goodsLine.append(good.name().charAt(0)).append(" ");
                    }
                }
                break;
            default:
                goodsLine = new StringBuilder(String.format("║ %d slots ║", this.getCapacity()));
        }
        goodsLine.append(" ║");
        righe[4] = goodsLine.toString();

        // Riga inferiore
        righe[5] = String.format("╚══  %s  ══╝",
                this.getConnectorAt(Side.REAR).getNumero() > 0 ?
                        String.valueOf(this.getConnectorAt(Side.REAR).getNumero()) : "═");

        return righe;
    }

    @Override
    public CargoHold clone(){
        CargoHold clone = new CargoHold(this.getType(), this.getConnectorAt(Side.FRONT), this.getConnectorAt(Side.REAR), this.getConnectorAt(Side.LEFT), this.getConnectorAt(Side.RIGHT), this.capacity, this.isSpecial);

        System.arraycopy(this.goods, 0, clone.goods, 0, this.capacity);
        clone.orientation = this.getOrientation();

        return clone;
    }
}
