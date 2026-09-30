package Model.Ship;

import java.io.Serializable;

/**
 * DoubleCannonsCounter keeps track of the number of front and other cannons on a ship.
 * It can be used to count the total number of cannons and differentiate between front and other cannons.
 */
public class DoubleCannonsCounter implements Serializable {
    int  frontCannons;
    int  otherCannons;

    public DoubleCannonsCounter() {
        this.frontCannons = 0;
        this.otherCannons = 0;
    }

    public DoubleCannonsCounter(int frontCannons, int otherCannons) {
        this.frontCannons = frontCannons;
        this.otherCannons = otherCannons;
    }

    public int getFrontCannons() {
        return frontCannons;
    }

    public int getOtherCannons() {
        return otherCannons;
    }



}
