package urlshortener;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import urlshortener.exception.AliasConflictException;
import urlshortener.exception.InvalidUrlException;

import static org.junit.jupiter.api.Assertions.*;

public class UrlShortenerTest {

    UrlShortenerService urlShortenerService;
    @BeforeEach
    void setup() {
        urlShortenerService = new UrlShortenerService(new Md5ShorteningStrategy());
    }
    @Test
    void shouldProvideNonNullLongUrl() {
        assertThrows(InvalidUrlException.class,() ->urlShortenerService.shortenUrl(null,null, null));
    }
    @Test
    void shouldProvideShortUrlForLongUrl() {
        String longUrl = "http://www.google.com";
        String shortUrl = urlShortenerService.shortenUrl(longUrl,null, null);
        assertFalse(shortUrl.isEmpty());
        assertTrue(longUrl.length() > shortUrl.length());
    }
    @Test
    void shouldProvideNonNullShortUrl() {
        assertThrows(InvalidUrlException.class,() ->urlShortenerService.unShortenUrl(null));
    }
    @Test
    void shouldProvideShortUrlForShortUrl() {
        String longUrl = "http://www.google.com";
        String shortUrl = urlShortenerService.shortenUrl(longUrl,null, null);
        assertEquals(longUrl,urlShortenerService.unShortenUrl(shortUrl));
    }
    @Test
    void shouldThrowForInvalidShortUrl() {
        String shortUrl = "http://www.abcd";
        assertThrows(InvalidUrlException.class,() ->urlShortenerService.unShortenUrl(shortUrl));
    }
    @Test
    void shouldReturnSameShortUrlForSameLongUrlShortenedTwice() {
        String longUrl = "http://www.google.com";
        String shortUrl = urlShortenerService.shortenUrl(longUrl,null , null);
        assertEquals(shortUrl, urlShortenerService.shortenUrl(longUrl,null, null));
    }

    @Test
    void shouldShortenWithCustomAlias() {
        String longUrl = "http://www.google.com";
        String shortUrl = urlShortenerService.shortenUrl(longUrl, "mylink" , null);
        assertEquals("mylink", shortUrl);
        assertEquals(longUrl, urlShortenerService.unShortenUrl("mylink"));
    }
    @Test
    void shouldThrowWhenAliasAlreadyTaken() {
        urlShortenerService.shortenUrl("http://www.google.com", "mylink", null);
        assertThrows(AliasConflictException.class,
                () -> urlShortenerService.shortenUrl("http://www.bing.com", "mylink" , null));
    }
    @Test
    void shouldNotThrowWhenSameUrlReShortenedWithSameAlias() {
        String longUrl = "http://www.google.com";
        String first = urlShortenerService.shortenUrl(longUrl, "mylink", null);
        String second = urlShortenerService.shortenUrl(longUrl, "mylink", null);
        assertEquals(first, second);
    }

    @Test
    void shouldThrowForMalformedLongUrl() {
        assertThrows(InvalidUrlException.class,
                () -> urlShortenerService.shortenUrl("not a valid url", null, null));
    }


    @Test
    void shouldReturnNullOrThrowForExpiredShortUrl() throws InterruptedException {
        String longUrl = "http://www.google.com";
        String shortUrl = urlShortenerService.shortenUrl(longUrl, null, 50L); // 50ms TTL
        Thread.sleep(100);
        assertThrows(InvalidUrlException.class,
                () -> urlShortenerService.unShortenUrl(shortUrl));
    }
    @Test
    void shouldResolveWithinTtlWindow() {
        String longUrl = "http://www.google.com";
        String shortUrl = urlShortenerService.shortenUrl(longUrl, null, 60_000L); // 1 min TTL
        assertEquals(longUrl, urlShortenerService.unShortenUrl(shortUrl));
    }
}
