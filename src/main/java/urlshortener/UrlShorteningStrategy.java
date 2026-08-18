package urlshortener;

public interface UrlShorteningStrategy {
    String shortenUrl(String longUrl, int attempt);
}
