package urlshortener;

import urlshortener.exception.AliasConflictException;
import urlshortener.exception.InvalidUrlException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class UrlShortenerService {

    private final Map<String,UrlEntry> shortToEntryMap;
    private final Map<String,String> longToShortMap;
    private final UrlShorteningStrategy urlShorteningStrategy;

    public UrlShortenerService(UrlShorteningStrategy urlShorteningStrategy) {
        this.shortToEntryMap = new ConcurrentHashMap<>();
        this.longToShortMap = new ConcurrentHashMap<>();
        this.urlShorteningStrategy = urlShorteningStrategy;
    }

    private static final Pattern URL_PATTERN = Pattern.compile(
            "^(https?://)?([\\da-z.-]+)\\.([a-z.]{2,6})([/\\w .-]*)*/?$"
    );


    public String shortenUrl(String longUrl,String alias, Long ttlMillis){
        if (longUrl == null || longUrl.isEmpty() || !URL_PATTERN.matcher(longUrl).matches()) {
            throw new InvalidUrlException("Invalid URL format: " + longUrl);
        }
        long expiry = (ttlMillis == null) ? Long.MAX_VALUE : System.currentTimeMillis() + ttlMillis;
        return longToShortMap.computeIfAbsent(longUrl, url -> {
            String shortUrl;
            UrlEntry entry = new UrlEntry(url,expiry);
            if (alias != null) {
                if (shortToEntryMap.putIfAbsent(alias, entry) != null) {
                    throw new AliasConflictException("Alias already in use: " + alias);
                }
                shortUrl = alias;
            } else {
                shortUrl = generateUniqueShortUrl(url, entry);
            }
            return shortUrl;
        });
    }


    public String unShortenUrl(String shortUrl){
        if(shortUrl == null || shortUrl.isEmpty()) {
            throw new InvalidUrlException("URL can not be null");
        }
        UrlEntry entry = shortToEntryMap.get(shortUrl);
        if (entry == null ) {
            throw new InvalidUrlException("Invalid short url provided");
        }
        if (entry.isExpired()) {
            shortToEntryMap.remove(shortUrl, entry);       // remove-if-still-this-value
            longToShortMap.remove(entry.getLongUrl(), shortUrl);
            throw new InvalidUrlException("Short url has expired");
        }
        return entry.getLongUrl();

    }

    private String generateUniqueShortUrl(String longUrl, UrlEntry entry) {
        int attempt = 0;
        String candidate;
        do {
            candidate = urlShorteningStrategy.shortenUrl(longUrl, attempt);
            attempt++;
        } while (shortToEntryMap.putIfAbsent(candidate, entry) != null
                && !shortToEntryMap.get(candidate).getLongUrl().equals(longUrl));
        return candidate;
    }
}
