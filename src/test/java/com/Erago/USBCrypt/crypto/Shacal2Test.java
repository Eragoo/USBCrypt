package com.Erago.USBCrypt.crypto;

import com.Erag0.USBCrypt.crypto.algorithm.Shacal2;
import org.junit.Assert;
import org.junit.Test;
import java.util.Arrays;

public class Shacal2Test extends Assert {

    @Test(expected = IllegalArgumentException.class)
    public void SmallKeyTest() {
        Shacal2 shacal2 = new Shacal2();
        shacal2.init(true, "test-secret-key".getBytes());
        byte[] inputData = "LoremIpsum".getBytes();
        byte[] outputData = new byte[inputData.length];
        shacal2.processBlock(inputData, 0, outputData, 0);
    }

    @Test
    public void BlockEncDecTest() {
        byte[] dataBlock = Arrays.copyOf("This text contains 32 byte block of data".getBytes(),32);
        byte[] outputBlock = new byte[dataBlock.length];
        byte[] tmpBlock = new byte[outputBlock.length];
        byte[] key = Arrays.copyOf("super-secret-key-for-super-application".getBytes(), 16);//16 byte - min length of key

        Shacal2 shacal2 = new Shacal2();
        //encode
        shacal2.init(true, key);
        shacal2.processBlock(dataBlock, 0, outputBlock, 0);
        //decode
        shacal2.init(false, key);
        shacal2.processBlock(outputBlock, 0, tmpBlock, 0);

        assertArrayEquals(dataBlock, tmpBlock);
    }
}
