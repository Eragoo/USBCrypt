package com.Erag0.USBCrypt.util;

import java.io.File;

public class USB {
    public static File getRoots() {
        if (OSValidator.isMac()) {
            return new File("/Volumes");
        } else if (OSValidator.isWindows()) {
            return File.listRoots()[0].getParentFile();
        }
        return File.listRoots()[0].getParentFile();
    }
}
