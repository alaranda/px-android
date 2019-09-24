package com.mercadolibre.dto.congrats;

public class CrossSelling {

    private String title;
    private String icon;
    private String label;
    private String target;
    private String contentId;
    private Action action;

    public CrossSelling(final Builder builder) {
        this.title = builder.title;
        this.icon = builder.icon;
        this.contentId = builder.contentId;
        this.action = builder.action;
    }

    public static class Builder {

        private String title;
        private String icon;
        private String contentId;
        private Action action;

        public Builder (final com.mercadolibre.dto.congrats.merch.Content content, final String iconUrl) {

            if (null == content) return;

            this.title = content.getTitle();
            this.icon = iconUrl;
            this.contentId = content.getContentId();

            if (null == content.getActions() || null == content.getActions().getContentLink()) return;

            this.action = new Action(content.getSubtitle(), content.getActions().getContentLink().getLink());
        }

        public CrossSelling build() {
            return(new CrossSelling(this));
        }
    }
}
