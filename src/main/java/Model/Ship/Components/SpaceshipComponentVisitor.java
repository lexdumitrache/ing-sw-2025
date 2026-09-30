package Model.Ship.Components;

/**
 * Visitor interface for spaceship components.
 * Used to implement external operations on different component types.
 */
public interface SpaceshipComponentVisitor {
    void visit(Cabin cabin);
    void visit(Cannon cannon);
    void visit(Engine engine);
    void visit(BatteryCompartment battery);
    void visit(CargoHold cargo);
    void visit(ShieldGenerator shield);
    void visit(StructuralModule structural);
    void visit(AlienLifeSupport support);
}