package com.mercadolibre.dto.congrats;

public class Points {

    private PointsProgress progress;
    private String title;
    private Action action;

    public Action getAction() {
        return action;
    }

    public PointsProgress getProgress() {
        return progress;
    }

    public String getTitle() {
        return title;
    }

    private Points(final Builder builder){
        this.progress = builder.progress;
        this.title = builder.title;
        this.action = builder.action;
    }

    public static class Builder {

        private PointsProgress progress;
        private String title;
        private Action action;

        public Builder (final PointsProgress pointsProgress, final String title) {
            this.progress = pointsProgress;
            this.title = title;
        }

        public Builder action(final Action action, final String platform) {
            if (platform.equalsIgnoreCase("MP")) {
                this.action = new Action(action.getLabel(), action.getMpTarget());
            } else {
                this.action = new Action(action.getLabel(), action.getMlTarget());
            }

            return this;
        }

        public Points build() { return new Points(this ); }
    }

}
