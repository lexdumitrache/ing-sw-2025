package Model.Ship;

import java.io.Serializable;

/**
 * ShieldCounter keeps track of the number of shields on each side of a ship.
 * It provides methods to increment and decrement the counts, ensuring they do not go below zero.
 */
public class ShieldCounter implements Serializable, Cloneable {
    private int northShields;
    private int eastShields;
    private int southShields;
    private int westShields;

    public ShieldCounter() {
        this.northShields = 0;
        this.eastShields = 0;
        this.southShields = 0;
        this.westShields = 0;
    }

    public int getNorthShields() {
        return northShields;
    }
    public void setNorthShields(int northShields) {
        this.northShields = northShields;
    }
    public void incrementNorthShields() {this.northShields++;}
    public void decrementNorthShields() {
        if(this.northShields > 0){
            this.northShields--;
        }else{
            throw new IllegalArgumentException("Cannot decrement north shields below zero.");
        }
    }

    public int getEastShields() {
        return eastShields;
    }
    public void setEastShields(int eastShields) {
        this.eastShields = eastShields;
    }
    public void incrementEastShields() {this.eastShields++;}
    public void decrementEastShields() {
        if(this.eastShields > 0){
            this.eastShields--;
        }else{
            throw new IllegalArgumentException("Cannot decrement double engines below zero.");
        }
    }

    public int getSouthShields() {
        return southShields;
    }
    public void setSouthShields(int southShields) {
        this.southShields = southShields;
    }
    public void incrementSouthShields() {this.southShields++;}
    public void decrementSouthShields() {
        if(this.southShields > 0){
            this.southShields--;
        }else{
            throw new IllegalArgumentException("Cannot decrement south shields below zero.");
        }
    }

    public int getWestShields() {
        return westShields;
    }
    public void setWestShields(int westShields) {
        this.westShields = westShields;
    }
    public void incrementWestShields() {this.westShields++;}
    public void decrementWestShields() {
        if(this.westShields > 0){
            this.westShields--;
        }else{
            throw new IllegalArgumentException("Cannot decrement west shields below zero.");
        }
    }

    @Override
    public ShieldCounter clone() {
        ShieldCounter clone = new ShieldCounter();

        clone.northShields = this.northShields;
        clone.eastShields = this.eastShields;
        clone.southShields = this.southShields;
        clone.westShields = this.westShields;

        return clone;
    }
}
