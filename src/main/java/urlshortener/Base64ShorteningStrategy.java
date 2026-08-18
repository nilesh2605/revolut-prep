package urlshortener;

import java.util.Base64;

public class Base64ShorteningStrategy implements UrlShorteningStrategy{

    @Override
    public String shortenUrl(String longUrl, int attempt) {
        String input = attempt == 0 ? longUrl : longUrl + "#" + attempt;
        String encoded = Base64.getUrlEncoder().encodeToString(input.getBytes());
        return encoded.substring(0, Math.min(8, encoded.length()));
    }
}
