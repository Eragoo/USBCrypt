package com.Erag0.USBCrypt.crypto;

import com.Erag0.USBCrypt.crypto.algorithm.Shacal2;

import java.io.File;

public class CryptoHelper {
    public static void run(CryptoDataDto cryptoDataDto) {
        FileCoder fileCoder = new FileCoder(new Shacal2());
        for (File file : cryptoDataDto.getFiles()) {
            fileCoder.processFile(file, cryptoDataDto.getPassword(),
                    cryptoDataDto.isEncryption(), cryptoDataDto.isBackupNeeded());
        }
    }
}
