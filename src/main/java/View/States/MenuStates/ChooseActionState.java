package View.States.MenuStates;

import View.Client.Actions.ActionConstructor;
import View.Client.Client;

import java.util.ArrayList;
import java.util.List;

public class ChooseActionState implements MenuState {
    @Override
    public void paint() {
        List<String> actions = new ArrayList<>();

        for(String action : Client.client.getState().getAvailableCommands()) {
            String message = action;

            final List<String> arguments = ActionConstructor.getActionConstructor(action).getArguments();

            if(arguments != null && !arguments.isEmpty()){
                for(String argument : arguments) {
                    message += " <" + argument + ">";
                }
            }

            actions.add(message);
        }

        Client.view.showOptions("Choose action", actions);
    }

    @Override
    public void callback(String line) {
        final String action = line.split(" ", 2)[0];

        String[] args;
        try{
            args = line.split(" ", 2)[1].split(" ");
        } catch (ArrayIndexOutOfBoundsException e){
            args = new String[0];
        }

        for(String option : Client.client.getState().getAvailableCommands()){
            if(action.equalsIgnoreCase(option)){
                Client.client.createAction(option, args);
                return;
            }
        }

        Client.view.repaint();
    }
}
