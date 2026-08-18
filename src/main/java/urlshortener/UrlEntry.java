package urlshortener;

public class UrlEntry {
    private final String longUrl;
    private final long expiryEpochMillis;

    public UrlEntry(String longUrl, long expiryEpochMillis) {
        this.longUrl = longUrl;
        this.expiryEpochMillis = expiryEpochMillis;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expiryEpochMillis;
    }

    public String getLongUrl() {
        return longUrl;
    }
}
