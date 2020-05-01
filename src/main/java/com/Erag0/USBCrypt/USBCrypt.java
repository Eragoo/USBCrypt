package com.Erag0.USBCrypt;

import com.Erag0.USBCrypt.crypto.FileCoder;
import com.Erag0.USBCrypt.crypto.algorithm.Shacal2;

import java.io.File;

public class USBCrypt {
    public static void main(String[] args){
        FileCoder fileCoder = new FileCoder(new Shacal2());
        fileCoder.processFile(new File("path"), "key",
                true);
    }
}
