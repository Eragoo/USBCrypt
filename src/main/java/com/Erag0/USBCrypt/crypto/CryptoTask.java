package com.Erag0.USBCrypt.crypto;

import com.Erag0.USBCrypt.crypto.algorithm.Shacal2;
import javafx.concurrent.Task;
import lombok.Setter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Setter
public class CryptoTask extends Task<List<File>> {
    private CryptoDataDto cryptoDataDto;

    @Override
    protected List<File> call() throws Exception {
        FileCoder fileCoder = new FileCoder(new Shacal2());
        List<File> fileList = new ArrayList<>();
        int i = 0;
        for (File file : cryptoDataDto.getFiles()) {
            this.updateMessage("Processing: " + file.getName());
            fileCoder.processFile(file, cryptoDataDto.getPassword(),
                    cryptoDataDto.isEncryption(), cryptoDataDto.isBackupNeeded());
            fileList.add(file);
            i++;
            this.updateProgress(i, cryptoDataDto.getFiles().size());
        }
        return fileList;
    }
}
