package View.States.MenuStates;

public class StopState implements MenuState {
    @Override
    public boolean isDone(){
        return true;
    }

    @Override
    public void paint() {
    }

    @Override
    public void callback(String line) {
        System.out.println("Cannot callback on stop state");
    }
}
