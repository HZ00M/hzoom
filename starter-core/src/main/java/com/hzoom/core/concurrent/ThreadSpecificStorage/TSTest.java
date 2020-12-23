package com.hzoom.core.concurrent.ThreadSpecificStorage;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.DecimalFormat;

public class TSTest {
    public static final TSTest INSTANCCE = new TSTest();

    private TSTest() {
    }

    ;
    private static final ThreadLocal<SecureRandom> RANDOM = new ThreadLocal<SecureRandom>() {
        @Override
        protected SecureRandom initialValue() {
            SecureRandom random;
            try {
                random = SecureRandom.getInstance("SHAIPRNG");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                random = new SecureRandom();
            }
            return random;
        }
    };

    public static void main(String[] args) {
        SecureRandom random = RANDOM.get();
        int code = random.nextInt(9999);
        System.out.println("code" + code);
        DecimalFormat decimalFormat = new DecimalFormat("000000");
        String newCode = decimalFormat.format(code);
        System.out.println("newCode " + newCode);
    }
}
