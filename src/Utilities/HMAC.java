package Utilities;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class HMAC {
    public static byte[] hmac2561(String secretKey, String message) {
        try {
            return hmac256(secretKey.getBytes("UTF-8"), message.getBytes("UTF-8"));
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate HMACSHA256 encrypt", e);
        }
    }

    public static byte[] hmac256(byte[] secretKey, byte[] message) {
        byte[] hmac256 = null;
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec sks = new SecretKeySpec(secretKey, "HmacSHA256");
            mac.init(sks);
            hmac256 = mac.doFinal(message);
            return hmac256;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate HMACSHA256 encrypt ");
        }
    }
}

