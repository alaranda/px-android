package com.mercadolibre.dto.user_agent;

/**
 * Supported mobile operating systems. NO_OS is the default system when no one is defined in
 * the received user agent.
 */
public enum OperatingSystem {
    ANDROID("Android"),
    IOS("iOS"),
    NO_OS("NoOS");

    private final String operatingSystem;

    /* default */ OperatingSystem(final String operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    public String getName() {
        return operatingSystem;
    }

    public static boolean isAndroid(final OperatingSystem platform) {
        return OperatingSystem.ANDROID.equals(platform);
    }

    public static boolean isIOS(final OperatingSystem platform) {
        return OperatingSystem.IOS.equals(platform);
    }
}
