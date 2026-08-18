package urlshortener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5ShorteningStrategy implements UrlShorteningStrategy{
    @Override
    public String shortenUrl(String longUrl, int attempt) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            String input = attempt == 0 ? longUrl : longUrl + "#" + attempt;
            byte[] digest = md.digest(input.getBytes());
            StringBuilder hex = new StringBuilder();
            for (byte b : digest) {
                hex.append(String.format("%02x", b));
            }
            return hex.substring(0, 8);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
