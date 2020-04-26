package com.Erag0.USBCrypt.crypto;

import com.Erag0.USBCrypt.exception.InputDataLengthException;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Shacal2 {
    private final static int[] K = {
            0x428a2f98, 0x71374491, 0xb5c0fbcf, 0xe9b5dba5, 0x3956c25b, 0x59f111f1, 0x923f82a4, 0xab1c5ed5,
            0xd807aa98, 0x12835b01, 0x243185be, 0x550c7dc3, 0x72be5d74, 0x80deb1fe, 0x9bdc06a7, 0xc19bf174,
            0xe49b69c1, 0xefbe4786, 0x0fc19dc6, 0x240ca1cc, 0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc, 0x76f988da,
            0x983e5152,	0xa831c66d, 0xb00327c8, 0xbf597fc7, 0xc6e00bf3, 0xd5a79147, 0x06ca6351, 0x14292967,
            0x27b70a85, 0x2e1b2138, 0x4d2c6dfc,	0x53380d13, 0x650a7354, 0x766a0abb, 0x81c2c92e, 0x92722c85,
            0xa2bfe8a1, 0xa81a664b, 0xc24b8b70, 0xc76c51a3, 0xd192e819, 0xd6990624, 0xf40e3585, 0x106aa070,
            0x19a4c116, 0x1e376c08, 0x2748774c, 0x34b0bcb5, 0x391c0cb3, 0x4ed8aa4a, 0x5b9cca4f, 0x682e6ff3,
            0x748f82ee, 0x78a5636f, 0x84c87814, 0x8cc70208,	0x90befffa, 0xa4506ceb, 0xbef9a3f7, 0xc67178f2
    };
    private static final int BLOCK_SIZE = 32;
    private boolean forEncryption = false;
    private static final int ROUNDS = 64;
    private int[] workingKey = null;

    public void init(boolean _forEncryption, byte[] key) {
        this.forEncryption = _forEncryption;
        workingKey = new int[64];
        setKey(key);
    }

    public void setKey(byte[] kb) {
        if (kb.length > 64 || kb.length < 16 || kb.length % 8 != 0) {
            throw new IllegalArgumentException("Shacal2-key must be 16 - 64 bytes and multiple of 8");
        }

        bytesToInts(kb, workingKey, 0);

        for ( int i = 16; i < 64; i++) {
            workingKey[i] =
                    ( (workingKey[i-2] >>> 17 | workingKey[i-2] << -17)
                            ^ (workingKey[i-2] >>> 19 | workingKey[i-2] << -19)
                            ^ (workingKey[i-2] >>> 10) )
                            + workingKey[i-7]
                            + ( (workingKey[i-15] >>> 7 | workingKey[i-15] << -7)
                            ^ (workingKey[i-15] >>> 18 | workingKey[i-15] << -18)
                            ^ (workingKey[i-15] >>> 3) )
                            + workingKey[i-16];
        }
    }

    private void encryptBlock(byte[] in, int inOffset, byte[] out, int outOffset) {
        int[] block = new int[BLOCK_SIZE / 4];
        byteBlockToInts(in, block, inOffset, 0);

        for (int i = 0; i < ROUNDS; i++) {
            int tmp =
                    (((block[4] >>> 6) | (block[4] << -6))
                            ^ ((block[4] >>> 11) | (block[4] << -11))
                            ^ ((block[4] >>> 25) | (block[4] << -25)))
                            + ((block[4] & block[5]) ^ ((~block[4]) & block[6]))
                            + block[7] + K[i] + workingKey[i];
            block[7] = block[6];
            block[6] = block[5];
            block[5] = block[4];
            block[4] = block[3] + tmp;
            block[3] = block[2];
            block[2] = block[1];
            block[1] = block[0];
            block[0] = tmp
                    + (((block[0] >>> 2) | (block[0] << -2))
                    ^ ((block[0] >>> 13) | (block[0] << -13))
                    ^ ((block[0] >>> 22) | (block[0] << -22)))
                    + ((block[0] & block[2]) ^ (block[0] & block[3]) ^ (block[2] & block[3]));
        }
        intsToBytes(block, out, outOffset);
    }

    private void decryptBlock(byte[] in, int inOffset, byte[] out, int outOffset) {
        int[] block = new int[BLOCK_SIZE / 4];
        byteBlockToInts(in, block, inOffset, 0);
        for (int i = ROUNDS - 1; i >-1; i--) {
            int tmp = block[0] - (((block[1] >>> 2) | (block[1] << -2))
                    ^ ((block[1] >>> 13) | (block[1] << -13))
                    ^ ((block[1] >>> 22) | (block[1] << -22)))
                    - ((block[1] & block[2]) ^ (block[1] & block[3]) ^ (block[2] & block[3]));
            block[0] = block[1];
            block[1] = block[2];
            block[2] = block[3];
            block[3] = block[4] - tmp;
            block[4] = block[5];
            block[5] = block[6];
            block[6] = block[7];
            block[7] = tmp - K[i] - workingKey[i]
                    - (((block[4] >>> 6) | (block[4] << -6))
                    ^ ((block[4] >>> 11) | (block[4] << -11))
                    ^ ((block[4] >>> 25) | (block[4] << -25)))
                    - ((block[4] & block[5]) ^ ((~block[4]) & block[6])); // T1
        }
        intsToBytes(block, out, outOffset);
    }

    public int processBlock(byte[] in, int inOffset, byte[] out, int outOffset)
            throws InputDataLengthException, IllegalStateException {
        if (workingKey == null) {
            throw new IllegalStateException("Key not initialised");
        }

        if ((inOffset + BLOCK_SIZE) > in.length) {
            throw new InputDataLengthException("input byte array too short");
        }

        if ((outOffset + BLOCK_SIZE) > out.length) {
            throw new InputDataLengthException("output byte array too short");
        }

        if (forEncryption) {
            encryptBlock(in, inOffset, out, outOffset);
        }
        else {
            decryptBlock(in, inOffset, out, outOffset);
        }

        return BLOCK_SIZE;
    }

    private void bytesToInts(byte[] bytes, int[] block, int bytesPosition) {
        for (int i = 0; i < bytes.length / 4; i++) {
            block[i] = ((bytes[bytesPosition++] & 0xFF) << 24)
                    | ((bytes[bytesPosition++] & 0xFF) << 16)
                    | ((bytes[bytesPosition++] & 0xFF) << 8)
                    | (bytes[bytesPosition++] & 0xFF);
        }
    }

    private void intsToBytes(int[] block, byte[] out, int pos) {
        for (int value : block) {
            out[pos++] = (byte) (value >>> 24);
            out[pos++] = (byte) (value >>> 16);
            out[pos++] = (byte) (value >>> 8);
            out[pos++] = (byte) value;
        }
    }

    private void byteBlockToInts(byte[] bytes, int[] block, int bytesPos, int blockPos) {
        for (int i = blockPos; i < BLOCK_SIZE / 4; i++) {
            block[i] = ((bytes[bytesPos++] & 0xFF) << 24)
                    | ((bytes[bytesPos++] & 0xFF) << 16)
                    | ((bytes[bytesPos++] & 0xFF) << 8)
                    | (bytes[bytesPos++] & 0xFF);
        }
    }
}
