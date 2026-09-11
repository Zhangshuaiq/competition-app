package competition.util;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TeamMemberCodecTest {

    @Test
    void readsLegacyCommaSeparatedMembersWithoutChangingValues() {
        assertEquals(Arrays.asList("张三", "李四", "王五"), TeamMemberCodec.decode("张三,李四,王五"));
    }

    @Test
    void preservesLegacyWhitespaceBehavior() {
        assertEquals(Arrays.asList("张三", " 李四"), TeamMemberCodec.decode("张三, 李四"));
    }

    @Test
    void readsJsonMembersContainingComma() {
        assertEquals(Arrays.asList("张三,甲", "李四"), TeamMemberCodec.decode("[\"张三,甲\",\"李四\"]"));
    }

    @Test
    void fallsBackToLegacyFormatWhenBracketedTextIsNotJson() {
        assertEquals(Arrays.asList("[一队", "二队]"), TeamMemberCodec.decode("[一队,二队]"));
    }

    @Test
    void handlesEmptyValues() {
        assertEquals(Collections.emptyList(), TeamMemberCodec.decode(null));
        assertEquals(Collections.emptyList(), TeamMemberCodec.decode(""));
    }

    @Test
    void legacyAndJsonFormatsRoundTrip() {
        assertEquals(Arrays.asList("张三", "李四"),
                TeamMemberCodec.decode(TeamMemberCodec.encodeLegacy(Arrays.asList("张三", "李四"))));
        assertEquals(Arrays.asList("张三,甲", "李四"),
                TeamMemberCodec.decode(TeamMemberCodec.encodeJson(Arrays.asList("张三,甲", "李四"))));
    }
}
