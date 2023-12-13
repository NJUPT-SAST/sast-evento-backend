package sast.evento.utils;

import sast.evento.common.enums.ErrorEnum;
import sast.evento.exception.LocalRunTimeException;

import java.security.MessageDigest;
import java.util.concurrent.ThreadLocalRandom;

import static java.nio.charset.StandardCharsets.UTF_8;

public class MD5Util {
    private static final String HEX_DIGITS[] = {"0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    public static String md5Encode(String origin, String salt) {

        String resultString = null;
        try {
            resultString = salt + origin;
            MessageDigest md = MessageDigest.getInstance("MD5");
            resultString = byteArrayToHexString(md.digest(resultString
                    .getBytes(UTF_8)));
        } catch (Exception e) {
            throw new LocalRunTimeException(ErrorEnum.LOGIN_ERROR);
        }
        return resultString;
    }

    public static String getSalt(Integer size) {
        char[] chars = ("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz" +
                "1234567890!@#$%^&*()_+").toCharArray();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            //Random().nextInt()返回值为[0,n)
            char aChar = chars[ThreadLocalRandom.current().nextInt(chars.length)];
            sb.append(aChar);
        }
        return sb.toString();
    }

    private static String byteArrayToHexString(byte b[]) {
        StringBuffer resultSb = new StringBuffer();
        for (int i = 0; i < b.length; i++)
            resultSb.append(byteToHexString(b[i]));

        return resultSb.toString();
    }

    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0)
            n += 256;
        int d1 = n / 16;
        int d2 = n % 16;
        return HEX_DIGITS[d1] + HEX_DIGITS[d2];
    }
}
