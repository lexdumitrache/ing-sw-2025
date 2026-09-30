package Model.Ship;

import java.io.Serializable;

/**
 * EnginesCounter keeps track of the number of single and double engines on a ship.
 * It provides methods to increment and decrement the counts, ensuring they do not go below zero.
 */
public class EnginesCounter implements Serializable, Cloneable {
    private int singleEngines;
    private int doubleEngines;

    public EnginesCounter() {
        this.singleEngines = 0;
        this.doubleEngines = 0;
    }

    public int getSingleEngines() {
        return singleEngines;
    }
    public void setSingleEngines(int singleEngines) {
        this.singleEngines = singleEngines;
    }
    public void incrementSingleEngines() {this.singleEngines++;}
    public void decrementSingleEngines() {
        if(this.singleEngines > 0){
            this.singleEngines--;
        }else{
            throw new IllegalArgumentException("Cannot decrement single engines below zero.");
        }
    }

    public int getDoubleEngines() {
        return doubleEngines;
    }
    public void setDoubleEngines(int doubleEngines) {
        this.doubleEngines = doubleEngines;
    }
    public void incrementDoubleEngines() {this.doubleEngines++;}
    public void decrementDoubleEngines() {
        if(this.doubleEngines > 0){
            this.doubleEngines--;
        }else{
            throw new IllegalArgumentException("Cannot decrement double engines below zero.");
        }
    }

    @Override
    public EnginesCounter clone() {
        EnginesCounter clone = new EnginesCounter();

        clone.singleEngines = this.singleEngines;
        clone.doubleEngines = this.doubleEngines;

        return clone;
    }
}
