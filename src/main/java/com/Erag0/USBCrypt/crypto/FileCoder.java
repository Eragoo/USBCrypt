package com.Erag0.USBCrypt.crypto;

import com.Erag0.USBCrypt.crypto.algorithm.BaseAlgorithm;
import lombok.AllArgsConstructor;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.util.Arrays;

@AllArgsConstructor
public class FileCoder {
    private BaseAlgorithm algorithm;

    public void processFile(File file, byte[] key, boolean isEncryption) {
        String namePrefix = isEncryption ? "ENC" : "DEC";
        String encodedFileName = FilenameUtils.removeExtension(file.getPath())
                + "-" + namePrefix + "."
                + FilenameUtils.getExtension(file.getPath());

        try (FileInputStream bufferedInputStream = new FileInputStream(file);
             FileOutputStream bufferedOutputStream = new FileOutputStream(new File(encodedFileName))) {
            readFileAndWriteWithOffset(isEncryption, bufferedInputStream, bufferedOutputStream, key);
        } catch (IOException ex) {
            ex.printStackTrace();
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
