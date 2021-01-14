package org.cybele.sodium;

public class SodiumJNI {
    public static native int sodium_init();
    public static native int randombytes_random();
}