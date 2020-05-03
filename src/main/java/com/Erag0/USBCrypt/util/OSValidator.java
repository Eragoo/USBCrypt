package com.Erag0.USBCrypt.util;

public class OSValidator {

    private static String OS = System.getProperty("os.name").toLowerCase();

    public static SupportOS getOS() {
        if (isWindows()) {
            return SupportOS.WIN;
        } else if (isMac()) {
            return SupportOS.MAC;
        } else {
            return SupportOS.UNSUPPORTED;
        }
    }

    public static boolean isWindows() {
        return (OS.contains("win"));
    }

    public static boolean isMac() {
        return (OS.contains("mac"));
    }
}
