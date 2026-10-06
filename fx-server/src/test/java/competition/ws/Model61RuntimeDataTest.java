package competition.ws;

import com.alibaba.fastjson2.JSON;
import competition.entity.Questions;
import competition.entity.Teams;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Model61RuntimeDataTest {

    @AfterEach
    void reset() {
        Model61RuntimeData.reset();
    }

    @Test
    void freeAnswerOnlyCreatesTheSelectedMemberForParticipatingTeams() {
        Teams first = team("一队", "[\"甲1\",\"甲2\",\"甲3\"]");
        Teams second = team("二队", "乙1,乙2,乙3");

        Model61RuntimeData.setTeams(Arrays.asList(first, second), 1);

        assertEquals(2, Model61RuntimeData.getRuntimeData().size());
        assertTrue(Model61RuntimeData.accepts("一队", "甲2"));
        assertTrue(Model61RuntimeData.accepts("二队", "乙2"));
        assertFalse(Model61RuntimeData.accepts("一队", "甲1"));
        assertFalse(Model61RuntimeData.accepts("未参与队", "丙2"));
    }

    private Teams team(String name, String members) {
        Teams team = new Teams();
        team.setFullName(name);
        team.setMembers(members);
        return team;
    }

    @Test
    void missingQuestionDeductionRemainsNullAfterWebSubmission() {
        Questions question = new Questions();
        question.setDescScore(null);

        // 网页复制收到的题目再提交；JSON 默认省略 null 字段。
        String payload = JSON.toJSONString(question);
        Model61RuntimeData.ModelData submitted = JSON.parseObject(payload, Model61RuntimeData.ModelData.class);

        assertFalse(JSON.parseObject(payload).containsKey("descScore"));
        assertNull(submitted.getDescScore());
    }

    @Test
    void nullAndEmptyQuestionDeductionsUseStageConfiguration() {
        assertNull(JSON.parseObject("{\"descScore\":null}", Model61RuntimeData.ModelData.class).getDescScore());
        assertNull(JSON.parseObject("{\"descScore\":\"\"}", Model61RuntimeData.ModelData.class).getDescScore());
    }

    @Test
    void explicitQuestionDeductionIncludingZeroIsPreserved() {
        assertEquals(Integer.valueOf(0), JSON.parseObject("{\"descScore\":\"0\"}",
                Model61RuntimeData.ModelData.class).getDescScore());
        assertEquals(Integer.valueOf(5), JSON.parseObject("{\"descScore\":\"5\"}",
                Model61RuntimeData.ModelData.class).getDescScore());
    }
}
