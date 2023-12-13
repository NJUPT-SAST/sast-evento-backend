package sast.evento.utils;

import java.util.concurrent.ThreadLocalRandom;

public class TicketUtil {


    public static String generateTicket(String key, String randomStr) {
        return key + "::" + System.currentTimeMillis() + "::" + randomStr;
    }

    public static String generateTicket() {
        return generateKey() + "::" + System.currentTimeMillis() + "::" + generateRandomStr();
    }

    public static String generateTicket(String key) {
        return key + "::" + System.currentTimeMillis() + "::" + generateRandomStr();
    }


    public static String[] getInfoFromTicket(String ticket) {
        int p1 = ticket.indexOf(':');
        int p2 = ticket.lastIndexOf(':');
        return new String[]{ticket.substring(0, p1), ticket.substring(p1 + 2, p2 - 1), ticket.substring(p1 + 1)};
    }

    public static String generateRandomStr() {
        char[] chars = ("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz" +
                "1234567890!@#$%^&*()_+").toCharArray();
        StringBuilder sb = new StringBuilder();
        int size = 50;
        for (int i = 0; i < size; i++) {
            //Random().nextInt()返回值为[0,n)
            char aChar = chars[ThreadLocalRandom.current().nextInt(chars.length)];
            sb.append(aChar);
        }
        return sb.toString();
    }

    public static String generateKey() {
        return String.valueOf(Math.abs(ThreadLocalRandom.current().nextLong()));
    }


}
