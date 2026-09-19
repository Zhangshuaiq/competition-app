package competition.ws;

import competition.entity.Teams;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
}
