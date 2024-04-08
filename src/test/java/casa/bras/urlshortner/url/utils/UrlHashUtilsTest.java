package casa.bras.urlshortner.url.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.stream.Stream;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@Tag("unit")
class UrlHashUtilsTest {

  @ParameterizedTest
  @MethodSource(value = "url_hash_hashesToStringWithSize7")
  void url_hash_hashesToStringWithSize7(String expected, String url) throws MalformedURLException {
    assertEquals(expected, UrlHashUtils.hash(new URL(url)));
  }

  @Test
  void twoUrls_hash_outputsTheSameHash() throws MalformedURLException {
    var url = new URL("https://www.youtube.com/watch?v=jW0VUBpp-40");
    assertEquals(UrlHashUtils.hash(url), UrlHashUtils.hash(url));
  }

  private static Stream<Arguments> url_hash_hashesToStringWithSize7() {
    return Stream.of(
        Arguments.of("5g3AZZ2", "https://payroc.com"),
        Arguments.of("6Rl5T0S", "https://www.gitlab.bras.casa/public-repository"));
  }
}
