package View.States.MenuStates;

public interface MenuState {
    default boolean isDone(){
        return false;
    }

    void paint();

    void callback(String line);
}
