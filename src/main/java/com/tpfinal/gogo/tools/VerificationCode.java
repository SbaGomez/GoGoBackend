package com.tpfinal.gogo.tools;

import java.util.Random;

public class VerificationCode {
    public static String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return Integer.toString(code);
    }
}
