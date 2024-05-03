package com.example.vulcans_limes;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class ExampleUnitTest {

    //Initialize MainActivity Object to have access to methods
    //MainActivity ma = Robolectric.buildActivity(MainActivity.class).get();

    @Test(expected = NullPointerException.class)
    public void encrypt_false_input() throws Exception {
        System.out.println("Er springt in die encrypt_false_input()!");
        MainActivity ma = Robolectric.buildActivity(MainActivity.class).get();
        ma.encryptData(null);
    }

    /**
     * This method tests the initKeyGen(). It should not throw an Exception
     * if the AndroidKeyStore is not found, as it is handled with a try-catch.
     */
    @Test
    public void initKeyGenTest() {
        System.out.println("Er springt in die init!");
        MainActivity ma = Robolectric.buildActivity(MainActivity.class).get();
        ma.initKeyGen();
    }

    /*
    @Test
    public void dreier(){
        MainActivity ma = Robolectric.buildActivity(MainActivity.class).get();
        int drei = ma.drei();
        assertEquals(3,drei);
    }

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

     */
}
