package com.Erag0.USBCrypt.crypto;

import com.Erag0.USBCrypt.crypto.algorithm.BaseAlgorithm;
import lombok.AllArgsConstructor;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.util.Arrays;
import java.util.Objects;

import static java.util.Objects.isNull;

@AllArgsConstructor
public class FileCoder {
    private BaseAlgorithm algorithm;

    public void processFile(File file, byte[] key, boolean isEncryption, boolean isBackupNeeded) {
        if (isNull(file)){
            return;
        }
        String namePrefix = isEncryption ? "ENC" : "DEC";
        String encodedFileName = FilenameUtils.removeExtension(file.getPath())
                + "-" + namePrefix + "."
                + FilenameUtils.getExtension(file.getPath());
        File processedFile = new File(encodedFileName);

        if (file.isDirectory()) {
            for (File f : Objects.requireNonNull(file.listFiles(file1 -> file1.canWrite() && file1.canRead()))) {
                processFile(f, key, isEncryption, isBackupNeeded);
            }
        } else {
            try (FileInputStream bufferedInputStream = new FileInputStream(file);
                 FileOutputStream bufferedOutputStream = new FileOutputStream(processedFile)) {
                readFileAndWriteWithOffset(isEncryption, bufferedInputStream, bufferedOutputStream, key);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        if (!isBackupNeeded && !file.isDirectory()) {
            file.delete();
            processedFile.renameTo(file);

        }
    }

    private void readFileAndWriteWithOffset(boolean isEncryption, FileInputStream sourceInputStream, FileOutputStream processedOutputStream, byte[] key)
            throws IOException {
        byte[] source = new byte[algorithm.getBlockSize()];
        byte[] processed = new byte[algorithm.getBlockSize()];
        int r = sourceInputStream.read(source);
        algorithm.init(isEncryption, normalizeKey(key));
        while (r != -1) {
            algorithm.processBlock(source, processed);
            processedOutputStream.write(processed);
            processedOutputStream.flush();
            processed = new byte[algorithm.getBlockSize()];
            source = new byte[algorithm.getBlockSize()];
            r = sourceInputStream.read(source);
        }
    }

    private byte[] normalizeKey(byte[] key) {
        return Arrays.copyOf(key, 64);
    }
}
