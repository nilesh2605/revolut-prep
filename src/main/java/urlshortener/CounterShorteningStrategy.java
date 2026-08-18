
package urlshortener;

import urlshortener.UrlShorteningStrategy;

import java.util.concurrent.atomic.AtomicInteger;

public class CounterShorteningStrategy implements UrlShorteningStrategy {

    private final AtomicInteger counter = new AtomicInteger(1);

    @Override
    public String shortenUrl(String longUrl, int attempt) {
        return encodeBase62(counter.getAndIncrement());
    }

    private String encodeBase62(long num) {
        String alphabet = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder sb = new StringBuilder();
        if (num == 0) return String.valueOf(alphabet.charAt(0));
        while (num > 0) {
            sb.append(alphabet.charAt((int) (num % 62)));
            num /= 62;
        }
        return sb.reverse().toString();
    }
}