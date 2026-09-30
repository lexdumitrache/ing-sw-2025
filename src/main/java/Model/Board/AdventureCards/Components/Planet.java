package Model.Board.AdventureCards.Components;

import Model.Board.AdventureCards.Rewards.Goods;
import Model.Enums.Good;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a planet in the game, which can be landed on by players.
 * Each planet has a name, a landing reward in the form of goods, and a status indicating whether it is occupied.
 */
public class Planet implements Serializable, Cloneable {
    private final String name;
    private final Goods landingReward;
    private boolean occupied;

    public Planet(String name, List<Good> goods) {
        this.name = name;
        this.landingReward = new Goods(goods);
        this.occupied = false;
    }

    public final String getName() {
        return name;
    }

    public final boolean isOccupied() {
        return occupied;
    }

    public final boolean setOccupied() {
        if(occupied)
            return false;

        occupied = true;
        return true;
    }

    public final Goods getLandingReward() {
        return landingReward;
    }

    @Override
    public Planet clone() {
        final List<Good> goods = new ArrayList<>();
        for(Good good : this.landingReward){
            goods.add(good);
        }

        Planet clone = new Planet(this.name, goods);

        clone.occupied = this.occupied;

        return clone;
    }
}
