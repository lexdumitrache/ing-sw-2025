package Model.Ship;

import java.io.Serializable;

/**
 * AlienCounter keeps track of alien crew presence on the ship.
 * It can be used to count the total number of alien crew members,
 * and also track whether at least one brown or purple alien is present.
 */
public class AlienCounter implements Serializable, Cloneable {
    private boolean brownAlien;
    private boolean purpleAlien;

    public AlienCounter() {
        this.brownAlien = false;
        this.purpleAlien = false;
    }

    public boolean hasBrownAlien() {
        return brownAlien;
    }

    public void setBrownAlien(boolean brownAlien) {
        this.brownAlien = brownAlien;
    }

    public boolean hasPurpleAlien() {
        return purpleAlien;
    }

    public void setPurpleAlien(boolean purpleAlien) {
        this.purpleAlien = purpleAlien;
    }

    @Override
    public AlienCounter clone() {
        AlienCounter clone = new AlienCounter();

        clone.brownAlien = this.brownAlien;
        clone.purpleAlien = this.purpleAlien;

        return clone;
    }
}
