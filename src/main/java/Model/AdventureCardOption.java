package Model;

import java.util.List;

public class AdventureCardOption {
    private String label;
    private List<String> conditions;
    private List<String> rewards;
    private List<String> penalties;

    public String getLabel() {
        return label;
    }

    public List<String> getConditions() {
        return conditions;
    }

    public List<String> getRewards() {
        return rewards;
    }

    public List<String> getPenalties() {
        return penalties;
    }
}
