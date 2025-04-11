package com.urlino.urlino.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.UUID;

public class ShortUrlGenerator {
    private static final String SECRET_KEY = "your_secret_key";
    private static final String HMAC_ALGO = "HmacSHA256";
    private static final char[] BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

    /**
     * 生成 8 位随机短链
     */
    public static String generateShortUrl() {
        String uuid = UUID.randomUUID().toString();
        byte[] hmac = computeHmacSHA256(uuid, SECRET_KEY);
        String base62 = base62Encode(hmac);
        return base62.length() >= 8 ? base62.substring(0, 8) : base62;
    }

    private static byte[] computeHmacSHA256(String data, String key) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGO);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), HMAC_ALGO);
            mac.init(secretKeySpec);
            return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException("HMAC calculation failed", e);
        }
    }

    public static String base62Encode(byte[] input) {
        BigInteger bi = new BigInteger(1, input);
        StringBuilder sb = new StringBuilder();
        while (bi.compareTo(BigInteger.ZERO) > 0) {
            BigInteger[] divmod = bi.divideAndRemainder(BigInteger.valueOf(62));
            sb.append(BASE62[divmod[1].intValue()]);
            bi = divmod[0];
        }
        return sb.reverse().toString();
    }

    /**
     * 计算 longUrl 的 MD5 hash，取前8位，用于反向索引
     */
    public static String hashLongUrl(String longUrl) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(longUrl.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString().substring(0, 8);
        } catch (Exception e) {
            throw new RuntimeException("MD5 calculation failed", e);
        }
    }
}
