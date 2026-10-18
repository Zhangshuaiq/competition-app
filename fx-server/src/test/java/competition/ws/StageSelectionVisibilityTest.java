package competition.ws;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StageSelectionVisibilityTest {
    @AfterEach void reset() { StageSelectRuntime.clear(); }

    @Test void selectionIsPrivateUntilRevealedAndCanBeHiddenAgain() {
        StageSelectRuntime.clear();
        assertEquals("未选择",StageSelectRuntime.displaySelection("队伍"));
        StageSelectRuntime.StageSelectData data = new StageSelectRuntime.StageSelectData();
        data.setTeam("队伍"); data.setStage("子环节1");
        StageSelectRuntime.save(data);
        assertEquals("已选择",StageSelectRuntime.displaySelection("队伍"));
        assertEquals(1,StageSelectRuntime.selectionStatus("队伍"));
        StageSelectRuntime.markRevealed("队伍");
        assertEquals("子环节1",StageSelectRuntime.displaySelection("队伍"));
        StageSelectRuntime.hide("队伍");
        assertEquals("已选择",StageSelectRuntime.displaySelection("队伍"));
        StageSelectRuntime.markRevealed("队伍");
        data.setStage("子环节2"); StageSelectRuntime.save(data);
        assertEquals("已选择",StageSelectRuntime.displaySelection("队伍"));
    }
}
