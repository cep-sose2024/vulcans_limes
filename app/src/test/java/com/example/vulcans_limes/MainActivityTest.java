package com.example.vulcans_limes;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.util.Arrays;
import java.util.Base64;

import org.junit.Before;

@RunWith(RobolectricTestRunner.class)
public class MainActivityTest {

    //TODO move this test class to a better directory than "example"

    private MainActivity ma;

    @Before
    public void setUp() {
        ma = new MainActivity();
    }

    /**
     * This method tests keyTestAES() to check, if the returned String of the generated AES Key is correct.
     *
     */
    @Test
    public void testKeyTestAES() {
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

    /**
     * This method tests genKey() to check, if a key is generated correctly.
     * @TODO: "AndroidKeyStore not found" and check how a key is written to a string and how it should look like
     */
    @Test
    public void testGenKey() {
        //TODO fix "AndroidKeyStore not found" so Test can run

        String keyInfo = ma.genKey();

        assertNotNull(keyInfo);
        assertNotEquals("ERROR", keyInfo);

        //TODO check how a key is written to a string and how it should look like
        //assertTrue(keyInfo.contains("Key"));
    }

    /**
     * This method tests initKeyGen() to check, if the key generator was initialized correctly.
     * @TODO: "AndroidKeyStore not found"
     */
    @Test
    public void testInitKeyGen() {
        assertTrue(ma.initKeyGen());
    }

    /**
     * This method tests encryptData(null) and expects a NullPointerException.
     * @throws NullPointerException
     */
    @Test(expected = NullPointerException.class)
    public void testEncryptDataWithNullInput() throws Exception {
        ma.encryptData(null);
    }

    /**
     * This method tests encryptData() and decryptData() with given test data. It expects the test data
     * to be the same as the decrypted data after encryption.
     * @throws Exception
     */
    @Test
    public void testEncryptAndDecryptData() throws Exception {
        MainActivity ma = Robolectric.buildActivity(MainActivity.class).get();
        byte[] testData = "Hello, World!".getBytes();
        ma.initKeyGen();
        byte[] encryptedData = ma.encryptData(testData);
        assertNotNull(encryptedData);

        byte[] decryptedData = ma.decryptData(encryptedData);
        assertNotNull(decryptedData);
        assertArrayEquals(testData, decryptedData);
    }
}
