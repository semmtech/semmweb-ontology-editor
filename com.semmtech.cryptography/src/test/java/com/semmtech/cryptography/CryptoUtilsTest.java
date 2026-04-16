package com.semmtech.cryptography;


import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class CryptoUtilsTest {

    @Test
    public void test() {
        // with shorter key
        String message = "Semmtech BV";
        String key = "asap";

        String crypted = CryptoUtils.simpleEncrypt(message, key);
        System.out.println("encrypted " + crypted);

        String decrypted = CryptoUtils.simpleDecrypt(crypted, key);
        System.out.println("decrypted " + decrypted);

        assertEquals(message, decrypted);

        // with longer key
        message = "Semmtech BV";
        key = "������pasa�4tya123mikeStolk35223ugaoi";

        crypted = CryptoUtils.simpleEncrypt(message, key);
        System.out.println("encrypted " + crypted);

        decrypted = CryptoUtils.simpleDecrypt(crypted, key);
        System.out.println("decrypted " + decrypted);

        assertEquals(message, decrypted);
    }
}
