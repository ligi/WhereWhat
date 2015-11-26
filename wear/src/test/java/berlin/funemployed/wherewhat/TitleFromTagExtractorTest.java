package berlin.funemployed.wherewhat;

import org.junit.Test;

import java.util.HashMap;
import static org.assertj.core.api.Assertions.assertThat;


public class TitleFromTagExtractorTest {

    private final String FALLBACK="MY AWESOME FALLBACK";
    private final String CONTENT="YAY CONTENT";
    private final String OTHER="OTHER";

    @Test
    public void testThatEmptyMapGivesFallback() {
        final String titleFromTagMap = TitleFromTagExtractor.getTitleFromTagMap(new HashMap<String, String>(), FALLBACK);

        assertThat(titleFromTagMap).isEqualTo(FALLBACK);
    }

    @Test
    public void testThatMapWithUnknownTagsGivesFallback() {
        final HashMap<String, String> probe = new HashMap<String, String>() {{
            put("foo", "bar");
        }};
        final String titleFromTagMap = TitleFromTagExtractor.getTitleFromTagMap(probe, FALLBACK);

        assertThat(titleFromTagMap).isEqualTo(FALLBACK);
    }

    @Test
    public void testIfWeHaveOpeningTimesItShouldBeReturned() {
        final HashMap<String, String> probe = new HashMap<String, String>() {{
            put("collection_times", CONTENT);
        }};
        final String titleFromTagMap = TitleFromTagExtractor.getTitleFromTagMap(probe, FALLBACK);

        assertThat(titleFromTagMap).isEqualTo(CONTENT);
    }

    @Test
    public void testIfCollectionTimeWinsOverRef() {
        final HashMap<String, String> probe = new HashMap<String, String>() {{
            put("collection_times", CONTENT);
            put("ref", OTHER);
        }};
        final String titleFromTagMap = TitleFromTagExtractor.getTitleFromTagMap(probe, FALLBACK);

        assertThat(titleFromTagMap).isEqualTo(CONTENT);
    }

}