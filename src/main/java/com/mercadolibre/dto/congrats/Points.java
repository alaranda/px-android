package com.mercadolibre.dto.congrats;

import com.mercadolibre.dto.user_agent.OperatingSystem;
import com.mercadolibre.dto.user_agent.UserAgent;

import static com.mercadolibre.dto.user_agent.Version.LOYALTY_LINK_INVALID_VERSION_LESS;

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

        public Builder action(final Action action, final String platform, final UserAgent userAgent) {
            //Fix para no mostrar el link en versiones iOS < 4.24 - no anda el link que mandan.
            if (platform.equalsIgnoreCase("MP") && OperatingSystem.isIOS(userAgent.getOperatingSystem()) && LOYALTY_LINK_INVALID_VERSION_LESS.compareTo(userAgent.getVersion()) > 0) {
                this.action = new Action(action.getLabel(),"");
            } else if(platform.equalsIgnoreCase("MP")) {
                this.action = new Action(action.getLabel(), action.getMpTarget());
            } else if (platform.equalsIgnoreCase("ML")) {
                this.action = new Action(action.getLabel(), action.getMlTarget());
            }

            return this;
        }

        public Points build() { return new Points(this ); }
    }

}