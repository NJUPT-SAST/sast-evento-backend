package sast.evento.utils;

import java.util.concurrent.ThreadLocalRandom;

public class TicketUtil {

    public static String generateTicket(){
       char[] chars = ("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz" +
               "1234567890!@#$%^&*()_+").toCharArray();
       StringBuilder sb = new StringBuilder();
       int size = 50;
       for(int i = 0; i < size; i++){
           //Random().nextInt()返回值为[0,n)
           char aChar = chars[ThreadLocalRandom.current().nextInt(chars.length)];
           sb.append(aChar);
       }
       return sb.toString();
   }



}
