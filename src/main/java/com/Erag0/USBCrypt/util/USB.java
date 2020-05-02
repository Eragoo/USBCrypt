package com.Erag0.USBCrypt.util;

import java.io.File;

public class USB {
    public static File[] getRoots() {
        if (OSValidator.isMac()) {
            return new File("/Volumes").listFiles((file)-> file.canRead() && file.canWrite());
        } else if (OSValidator.isWindows()) {
            return File.listRoots();
        }
        return File.listRoots();
    }
}
