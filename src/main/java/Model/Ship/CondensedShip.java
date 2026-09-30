package Model.Ship;

import Model.Enums.Crewmates;
import Model.Enums.Direction;
import Model.Enums.Good;
import Model.Ship.Components.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class CondensedShip implements Serializable, Cloneable {
    private final List<Cabin> cabins;
    private final List<BatteryCompartment> batteryCompartments;
    private final List<CargoHold> cargoHolds;
    private final List<Cannon> cannons;
    private final List<Engine> enginesList;
    private final List<AlienLifeSupport> alienSupports;
    private final EnginesCounter engines;
    private final AlienCounter aliens;
    private final ShieldCounter shields;

    public CondensedShip() {
        this.cabins = new ArrayList<>();
        this.batteryCompartments = new ArrayList<>();
        this.cargoHolds = new ArrayList<>();
        this.cannons = new ArrayList<>();
        this.alienSupports = new ArrayList<>();
        this.enginesList = new ArrayList<>();
        this.engines = new EnginesCounter();
        this.aliens = new AlienCounter();
        this.shields = new ShieldCounter();
    }

    public int getTotalBatteries() {
        int totalBatteries = 0;

        for (BatteryCompartment batteryCompartment : batteryCompartments) {
            totalBatteries += batteryCompartment.getBatteries();
        }

        return totalBatteries;
    }
    public int getTotalCrew() {
        int totalCrew = 0;

        for (Cabin cabin : cabins) {
            Crewmates crewmates = cabin.getOccupants();
            if(crewmates == Crewmates.EMPTY) {
                totalCrew += 0;
            } else if (crewmates == Crewmates.DOUBLE_HUMAN) {
                totalCrew += 2;
            } else {
                totalCrew += 1;
            }
        }

        return totalCrew;
    }

    public int getTotalHumans() {
        int totalHumans = 0;

        for (Cabin cabin : cabins) {
            Crewmates crewmates = cabin.getOccupants();
            if(crewmates == Crewmates.SINGLE_HUMAN) {
                totalHumans += 1;
            } else if (crewmates == Crewmates.DOUBLE_HUMAN) {
                totalHumans += 2;
            }
        }

        return totalHumans;
    }

    public DoubleCannonsCounter getTotalDoubleCannons() {
        int frontCannons = 0;
        int otherCannons = 0;
        for (Cannon cannon : cannons) {

            if (cannon.isDouble()) {
                if(cannon.getOrientation()== Direction.UP){
                    frontCannons++;
                } else {
                    otherCannons++;
                }
            }
        }
        return new DoubleCannonsCounter(frontCannons, otherCannons);
    }

    /**
     * Returns a GoodCounter containing the specified number of goods to discard.
     * The method prioritizes RED goods, then YELLOW, GREEN, and BLUE.
     * If there are not enough goods of the specified type, it will return as many as possible.
     *
     * @param num the number of goods to discard
     * @return a GoodCounter with the specified number of goods
     */
    public GoodCounter goodToDiscard(int num){
        List<Good> allGoods = cargoHolds.stream()
                .flatMap(cargo -> Arrays.stream(cargo.getGoods()))
                .collect(Collectors.toList());
        GoodCounter counter = new GoodCounter();
        for(Good g : allGoods){
            if(num== 0)
                return counter;
            if(g == Good.RED){
                counter.addGood(g);
                num--;
            }
        }
        if(num > 0){
            for(Good g : allGoods){
                if(num== 0)
                    return counter;
                if(g == Good.YELLOW){
                    counter.addGood(g);
                    num--;
                }
            }
        }
        if(num > 0){
            for(Good g : allGoods){
                if(num== 0)
                    return counter;
                if(g == Good.GREEN){
                    counter.addGood(g);
                    num--;
                }
            }
        }
        if(num > 0){
            for(Good g : allGoods){
                if(num== 0)
                    return counter;
                if(g == Good.BLUE){
                    counter.addGood(g);
                    num--;
                }
            }
        }
        return counter;
    }


    public List<Cabin> getCabins() {
        return cabins;
    }
    public void addCabin(Cabin cabin) {
        cabins.add(cabin);
    }
    public void removeCabin(Cabin cabin) {
        cabins.remove(cabin);
    }

    public List<BatteryCompartment> getBatteryCompartments() {
        return batteryCompartments;
    }
    public void addBatteryCompartment(BatteryCompartment batteryCompartment) { batteryCompartments.add(batteryCompartment); }
    public void removeBatteryCompartment(BatteryCompartment batteryCompartment) { batteryCompartments.remove(batteryCompartment); }

    public List<CargoHold> getCargoHolds() {
        return cargoHolds;
    }
    public void addCargoHold(CargoHold cargoHold) {
        cargoHolds.add(cargoHold);
    }
    public void removeCargoHold(CargoHold cargoHold) {
        cargoHolds.remove(cargoHold);
    }

    public List<Cannon> getCannons() {
        return cannons;
    }
    public void addCannon(Cannon cannon) {
        cannons.add(cannon);
    }
    public void removeCannon(Cannon cannon) {
        cannons.remove(cannon);
    }

    public List<AlienLifeSupport> getAlienSupports() { return alienSupports; }
    public void addAlienSupport(AlienLifeSupport alienSupport) { alienSupports.add(alienSupport); }
    public void removeAlienSupport(AlienLifeSupport alienSupport) { alienSupports.remove(alienSupport); }

    public List<Engine> getEnginesList() {
        return enginesList;
    }
    public void addEngine(Engine engine) {
        enginesList.add(engine);
    }
    public void removeEngine(Engine engine) {
        enginesList.remove(engine);
    }

    public EnginesCounter getEngines() {
        return engines;
    }

    public ShieldCounter getShields() {
        return shields;
    }

    public AlienCounter getAliens() {return aliens;}


    public boolean canContainBrown(){
        Coordinates centralCabinCoordinates=new Coordinates(7,7);

        for (Cabin cabin : cabins) {
            if (cabin.getCanContainBrown()) {
                Coordinates myCoordinates=cabin.getShipBoard().getIndex(cabin);
                if ((myCoordinates.getI()!=centralCabinCoordinates.getI())||(myCoordinates.getJ()!=centralCabinCoordinates.getJ())) {
                    System.out.println("Cabin at coordinates (i:"+myCoordinates.getI()+"; j:"+myCoordinates.getJ()+")"+" can contain Brown Alien");
                    return true;
                }else{
                    System.out.println("Central Cabin at coordinates (i:"+myCoordinates.getI()+"; j:"+myCoordinates.getJ()+")"+" can't contain Aliens");
                }
            }
        }
        return false;
    }

    public boolean canContainPurple(){
        Coordinates centralCabinCoordinates=new Coordinates(7,7);

        for (Cabin cabin : cabins) {
            if (cabin.getCanContainPurple()) {
                Coordinates myCoordinates=cabin.getShipBoard().getIndex(cabin);
                if ((myCoordinates.getI()!=centralCabinCoordinates.getI())||(myCoordinates.getJ()!=centralCabinCoordinates.getJ())) {
                    System.out.println("Cabin at coordinates (i:"+myCoordinates.getI()+"; j:"+myCoordinates.getJ()+")"+" can contain Purple Alien");
                    return true;
                }else{
                    System.out.println("Central Cabin at coordinates (i:"+myCoordinates.getI()+"; j:"+myCoordinates.getJ()+")"+" can't contain Aliens");
                }
            }
        }
        return false;
    }

    /**
     * Returns the base power of the ship, without considering double cannons.
     * */
    public double getBasePower(){
        double power = 0;
        for(Cannon cannon : this.getCannons()){
            if(!cannon.isDouble()){
                if(cannon.getOrientation()== Direction.UP){
                    power++;
                } else {
                    power+=0.5;
                }
            }
        }
        if(power>0){
            if(this.getAliens().hasPurpleAlien()){
                power+=2;
            }
        }
        return power;
    }

    /**
     * Returns the maximum power of the ship, considering double cannons.
     * The power is calculated as follows:
     * - Each single cannon contributes 1 power (0.5 for other cannons).
     * - Each double cannon contributes 2 power (1 for other cannons).
     * - If there is a purple alien, an additional 2 power is added.
     */
    public double getMaxPower(){
        double power = getBasePower();
        DoubleCannonsCounter doubleCannons = this.getTotalDoubleCannons();
        power += doubleCannons.getFrontCannons() * 2;
        power += doubleCannons.getOtherCannons();

        return power;
    }

    /**
     * Returns the base thrust of the ship, without considering double engines.
     * The thrust is calculated as follows:
     * - Each single engine contributes 1 thrust.
     * - If there is a brown alien, an additional 2 thrust is added.
     */
    public double getBaseThrust(){
        double thrust = 0;
        thrust += this.getEngines().getSingleEngines();
        if(thrust>0){
            if(this.getAliens().hasBrownAlien()){
                thrust += 2;
            }
        }
        return thrust;
    }

    /**
     * Returns the maximum thrust of the ship, considering double engines.
     * The thrust is calculated as follows:
     * - Each single engine contributes 1 thrust.
     * - Each double engine contributes 2 thrust.
     * - If there is a brown alien, an additional 2 thrust is added.
     */
    public double getMaxThrust(){
        double thrust = getBaseThrust();
        thrust += this.getEngines().getDoubleEngines() * 2;
        return thrust;
    }

    @Override
    public CondensedShip clone(){
        return new CondensedShip(this);
    }

    private CondensedShip(CondensedShip old) {
        this.cabins = new ArrayList<>();

        for(Cabin component : old.cabins){
            this.cabins.add(component.clone());
        }

        this.batteryCompartments = new ArrayList<>();

        for(BatteryCompartment component : old.batteryCompartments){
            this.batteryCompartments.add(component.clone());
        }

        this.cargoHolds = new ArrayList<>();

        for(CargoHold component : old.cargoHolds){
            this.cargoHolds.add(component.clone());
        }

        this.cannons = new ArrayList<>();

        for(Cannon component : old.cannons){
            this.cannons.add(component.clone());
        }

        this.enginesList = new ArrayList<>();

        for(Engine component : old.enginesList){
            this.enginesList.add(component.clone());
        }

        this.alienSupports = new ArrayList<>();

        for(AlienLifeSupport component : old.alienSupports){
            this.alienSupports.add(component.clone());
        }

        this.engines = old.engines.clone();
        this.aliens = old.aliens.clone();
        this.shields = old.shields.clone();
    }
}
