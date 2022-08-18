package de.kbecker.utils;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * @author Kevin Becker (kevin.becker@stud.th-owl.de)
 */
public class Helper {

    private static Helper instance;

    private final String alphanum = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toLowerCase() + "0123456789";

    private Random random;

    private Helper(){
        random = new Random();
    }

    public static Helper getInstance(){
        if(instance == null){
            instance = new Helper();
        }
        return instance;
    }

    public String hashWithSha256(String value){
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            BigInteger bigInt = new BigInteger(1, hash);
            String hashtext = bigInt.toString(16);

            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }


    public String randomString(int length, boolean allowSymbols){
        String s = "";
        String parent = allowSymbols ? alphanum+"#+_-.,@" : alphanum;
        for (int i = 0; i < length; i++){
            s+=parent.charAt(random.nextInt(parent.length()));
        }
        return s;
    }

}
