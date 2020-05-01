package com.Erag0.USBCrypt.crypto.algorithm;

public interface BaseAlgorithm {
    void processBlock(byte[] in, int inOffset, byte[] out, int outOffset);
    void processBlock(byte[] in, byte[] out);
    void init(boolean _forEncryption, byte[] key);
    int getBlockSize();
}
