package competition.util;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DelimitedConfigCodecTest {

    @Test
    void preservesLegacyFieldParsing() {
        assertArrayEquals(new String[]{"750", "150", "", "默认.ttf"},
                DelimitedConfigCodec.splitFields("750%%150%%%%默认.ttf"));
    }

    @Test
    void preservesJavaSplitTrailingEmptyBehavior() {
        assertArrayEquals(new String[]{"标题", "内容"},
                DelimitedConfigCodec.splitFields("标题%%内容%%%%"));
    }

    @Test
    void handlesNestedSectionsSeparately() {
        assertArrayEquals(new String[]{"标题%%40", "正文%%30"},
                DelimitedConfigCodec.splitSections("标题%%40###正文%%30"));
    }

    @Test
    void legacyFormatRoundTrips() {
        assertEquals("10%%20%%%%默认.ttf",
                DelimitedConfigCodec.joinFields(Arrays.asList("10", "20", "", "默认.ttf")));
        assertEquals("%%20", DelimitedConfigCodec.joinFields(Arrays.asList("", "20")));
        assertEquals("标题%%40###正文%%30",
                DelimitedConfigCodec.joinSections(Arrays.asList("标题%%40", "正文%%30")));
    }
}
