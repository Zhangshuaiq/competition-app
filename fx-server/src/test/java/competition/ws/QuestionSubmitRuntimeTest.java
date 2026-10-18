package competition.ws;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class QuestionSubmitRuntimeTest {
    @AfterEach
    void cleanup() { QuestionSubmitRuntime.clear(); }

    @Test
    void revealFreezesAnswersAndRejectsLateSubmissionsUntilNextQuestion() {
        QuestionSubmitRuntime.clear();
        assertTrue(QuestionSubmitRuntime.save("队伍", "A"));
        Map<String, String> snapshot = QuestionSubmitRuntime.snapshotAndClose();
        assertFalse(QuestionSubmitRuntime.save("队伍", "B"));
        assertEquals("A", snapshot.get("队伍"));
        assertEquals("A", QuestionSubmitRuntime.get("队伍"));
        snapshot.clear();
        assertEquals("A", QuestionSubmitRuntime.get("队伍"));
        QuestionSubmitRuntime.clear();
        assertTrue(QuestionSubmitRuntime.save("队伍", "B"));
    }
}
