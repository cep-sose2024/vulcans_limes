package com.example.vulcans_limes;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.util.Arrays;
import java.util.Base64;

@RunWith(RobolectricTestRunner.class)
public class MainActivityTest {

    /**
     * This method tests keyTestAES() to check, if the returned String of the generated AES Key is correct.
     *
     */
    @Test
    public void testKeyTestAES() {
        MainActivity ma = new MainActivity();
        String result = ma.keyTestAES();

        assertNotNull(result);

        int keyIndex = result.indexOf("AES Key: [") + "AES Key: [".length();
        int keyEndIndex = result.indexOf("]", keyIndex);
        String aesKeyString = result.substring(keyIndex, keyEndIndex);
        String[] aesKeyParts = aesKeyString.split(", ");
        byte[] aesKey = new byte[aesKeyParts.length];
        for (int i = 0; i < aesKeyParts.length; i++) {
            aesKey[i] = Byte.parseByte(aesKeyParts[i].trim());
        }

        assertTrue(result.contains("AES Key length: " + aesKey.length));
        assertTrue(result.contains("AES Key: " + Arrays.toString(aesKey)));
        assertTrue(result.contains("AES Key Base64: " + Base64.getEncoder().encodeToString(aesKey)));
    }

    @Test
    public void testGenKey() {
        MainActivity ma = Robolectric.buildActivity(MainActivity.class).get();
        String keyInfo = ma.genKey();
        assertNotNull(keyInfo);
        assertTrue(keyInfo.contains("Key"));
    }

    @Test
    public void testInitKeyGen() {
        MainActivity ma = Robolectric.buildActivity(MainActivity.class).get();
        assertTrue(ma.initKeyGen());
    }

    @Test(expected = NullPointerException.class)
    public void testEncryptDataWithNullInput() throws Exception {
        MainActivity ma = Robolectric.buildActivity(MainActivity.class).get();
        ma.encryptData(null);
    }

    @Test
    public void testEncryptAndDecryptData() throws Exception {
        MainActivity ma = Robolectric.buildActivity(MainActivity.class).get();
        byte[] testData = "Hello, World!".getBytes();
        byte[] encryptedData = ma.encryptData(testData);
        assertNotNull(encryptedData);

        byte[] decryptedData = ma.decryptData(encryptedData);
        assertNotNull(decryptedData);
        assertArrayEquals(testData, decryptedData);
    }
}
