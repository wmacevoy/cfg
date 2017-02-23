package cfg.functions;

import org.junit.*;
import static org.junit.Assert.*;

import java.util.*;
import java.lang.reflect.*;
import cfg.functions.*;

public class CipherTest
{
    void testHex() {
        assert Cipher.hex(null) == null;
        assert Cipher.hex(new byte[] { }).equals("");
        assert Cipher.hex(new byte[] { (byte) 0x00 }).equals("00");
        assert Cipher.hex(new byte[] { (byte) 0x09 }).equals("09");
        assert Cipher.hex(new byte[] { (byte) 0x0a }).equals("0a");
        assert Cipher.hex(new byte[] { (byte) 0x0f }).equals("0f");
        assert Cipher.hex(new byte[] { (byte) 0x90 }).equals("90");
        assert Cipher.hex(new byte[] { (byte) 0x99 }).equals("99");
        assert Cipher.hex(new byte[] { (byte) 0x9a }).equals("9a");
        assert Cipher.hex(new byte[] { (byte) 0x9f }).equals("9f");
        assert Cipher.hex(new byte[] { (byte) 0xa0 }).equals("a0");
        assert Cipher.hex(new byte[] { (byte) 0xa9 }).equals("a9");
        assert Cipher.hex(new byte[] { (byte) 0xaa }).equals("aa");
        assert Cipher.hex(new byte[] { (byte) 0xaf }).equals("af");
        assert Cipher.hex(new byte[] { (byte) 0xf0 }).equals("f0");
        assert Cipher.hex(new byte[] { (byte) 0xf9 }).equals("f9");
        assert Cipher.hex(new byte[] { (byte) 0xfa }).equals("fa");
        assert Cipher.hex(new byte[] { (byte) 0xff }).equals("ff");

        assert Cipher.hex(new byte[] { (byte) 0xde,
                                       (byte) 0xad,
                                       (byte) 0xbe,
                                       (byte) 0xef }).equals("deadbeef");

    }


    @Test public void testEncrypt() {
        String[] keys = new String[] { "", "a", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                                       "b", "ab", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab" };

        String[] plains = new String[] { "", "x", "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx",
                                         "y", "xy", "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxy" };

        // never encrypts twice to same cipher
        for (String key : keys) {
            for (String plain : plains) {
                assertNotEquals(Cipher.encrypt(key,plain),Cipher.encrypt(key,plain));
            }
        }

        // right key works
        for (String key : keys) {
            for (String plain : plains) {
                assertEquals(Cipher.decrypt(key,Cipher.encrypt(key,plain)),plain);
            }
        }

        // wrong key fails
        for (String key1 : keys) {
            for (String key2 : keys) {
                if (!key1.equals(key2)) {
                    for (String plain : plains) {
                        assertEquals(Cipher.decrypt(key1,Cipher.encrypt(key2,plain)),null);
                    }
                }
            }
        }

    }
}
