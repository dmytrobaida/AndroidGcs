package com.diploma.dima.androidgcs.utils;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

public class Numbers {
    public static String random(int length) {
        SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(32);
    }
}
