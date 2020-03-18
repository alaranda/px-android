package com.mercadolibre.dto.congrats;

public class Action {

    private String label;
    private String target;
    private String mlTarget;
    private String mpTarget;

    public Action(final String label, final String target) {
        this.label = label;
        this.target = target;
    }

    public String getMlTarget() {
        return mlTarget;
    }

    public String getMpTarget() {
        return mpTarget;
    }

    public String getLabel() {
        return label;
    }

    public String getTarget() {
        return target;
    }

    public String toString() {
        return String.format("Action{label=[%s], target=[%s], mlTarget=[%s], mpTarget=[%s]}",
                label, target, mlTarget, mpTarget);
    }
}
