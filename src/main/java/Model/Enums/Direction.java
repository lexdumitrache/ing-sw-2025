package Model.Enums;

/**
 * Direzioni cardinali usate per orientare i componenti sulla nave.
 */
public enum Direction {
    UP("↑"),     // verso l'alto (fronte della nave)
    DOWN("↓"),   // verso il basso (retro della nave)
    LEFT("←"),   // lato sinistro
    RIGHT("→");  // lato destro

    private final String freccia;

    Direction(String freccia) {
        this.freccia = freccia;
    }

    public String getFreccia() {
        return freccia;
    }
}
