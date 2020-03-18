package com.mercadolibre.dto.remedies;

public class FieldSetting {

    private final String name;
    private final int length;
    private final String type;
    private final String title;
    private final String hintMessage;
    private final String validationMessage;
    private final String mask;

    public FieldSetting(final Builder builder) {
        this.name = builder.name;
        this.length = builder.length;
        this.type = builder.type;
        this.title = builder.title;
        this.hintMessage = builder.hintMessage;
        this.validationMessage = builder.validationMessage;
        this.mask = builder.mask;
    }

    public static final class Builder {

        private String name;
        private int length;
        private String type;
        private String title;
        private String hintMessage;
        private String validationMessage;
        private String mask;

        public Builder(final String name, final String title, final String validationMessage, final String hintMessage) {
            this.name = name;
            this.title = title;
            this.validationMessage = validationMessage;
            this.hintMessage = hintMessage;
        }

        public Builder withType(final String type) {
            this.type = type;
            return this;
        }

        public Builder withLength(final int length) {
            this.length = length;
            return this;
        }

        public Builder withMask(final String mask) {
            this.mask = mask;
            return this;
        }

        public FieldSetting build() {
            return new FieldSetting(this);
        }
    }

    public String getName() {
        return name;
    }

    public int getLength() {
        return length;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getHintMessage() {
        return hintMessage;
    }

    public String getValidationMessage() {
        return validationMessage;
    }

    public String getMask() {
        return mask;
    }
}
