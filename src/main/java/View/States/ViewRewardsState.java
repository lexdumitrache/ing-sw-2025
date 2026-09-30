package View.States;

import View.Client.Client;

public class ViewRewardsState implements ViewState {
    @Override
    public void paint() {
        Client.client.getState().viewRewards();
    }
}
