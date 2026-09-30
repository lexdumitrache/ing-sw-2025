package View.States.MenuStates;

import View.Client.Actions.Action;
import View.Client.Actions.ActionConstructor;
import View.Client.Client;

import java.util.*;

public class ActionCreationState implements MenuState {
    private final ActionConstructor actionConstructor;
    private final Map<String, String> providedArguments;

    public ActionCreationState(ActionConstructor actionConstructor, Map<String, String> providedArguments) {
        this.actionConstructor = actionConstructor;

        this.providedArguments = providedArguments;
    }

    @Override
    public void paint(){
        Client.view.showArguments(this.actionConstructor.getArguments(), this.providedArguments);
    }

    public void addArgument(String argName, String argValue){
        this.providedArguments.put(argName, argValue);
    }

    @Override
    public void callback(String line) {
        final String argName = line.split(" ")[0];

        if(argName.equalsIgnoreCase("send")){
            this.send();
            return;
        }

        for(String arg : this.actionConstructor.getArguments()){
            if(argName.equalsIgnoreCase(arg)){
                try {
                    final String argValue = line.split(" ")[1];
                    this.addArgument(arg, argValue);
                }catch (ArrayIndexOutOfBoundsException e){
                    Client.view.log("Warning: No argument was provided");
                }
                break;
            }
        }

        Client.view.repaint();
    }

    public void send() {
        Action action;

        try{
            action = this.actionConstructor.create(this.providedArguments);
        } catch (IllegalArgumentException e) {
            action = state -> state;
        }

        Client.client.execute(action);
    }
}
