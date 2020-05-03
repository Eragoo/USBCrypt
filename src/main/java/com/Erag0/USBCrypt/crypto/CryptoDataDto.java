package com.Erag0.USBCrypt.crypto;

import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.List;

@Getter
@Setter
public class CryptoDataDto {
    private boolean isBackupNeeded;
    private byte[] password;
    private List<File> files;
    private boolean isEncryption;
}