package competition.rest;

import competition.entity.Questions;
import competition.entity.Stages;
import competition.entity.Teams;
import competition.resp.Model6RestData;
import competition.util.TeamRuntime;
import competition.ws.Model60RuntimeData;
import competition.ws.Model61RuntimeData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FreeAnswerBoardTest {
    @AfterEach
    void reset() {
        Model60RuntimeData.reset();
        Model61RuntimeData.reset();
        TeamRuntime.setTeams(Collections.emptyList());
    }

    @Test
    void independentModeBoardUsesSelectedMemberDataAndLeavesOtherTeamsIdle() throws Exception {
        Teams first = team(1, "一组");
        Teams second = team(2, "二组");
        TeamRuntime.setTeams(Arrays.asList(first, second));
        Model61RuntimeData.setTeams(Collections.singletonList(first), 1);
        Questions question = new Questions();
        question.setAnswer("A");
        Model61RuntimeData.setRuntimeData(question);
        Model61RuntimeData.getRuntimeData().get(0).setUserSelect("A");

        Stages stage = new Stages();
        stage.setQuestionType(Stages.FREE_ANSWER_TYPE);
        stage.setM6(0);
        stage.setBoardAnswer(0);
        RestApi api = new RestApi();

        List<Model6RestData> answering = api.buildModel6BoardData(stage, 3);
        assertEquals(3, answering.get(0).getPhase());
        assertEquals(1, answering.get(0).getBuzzStatus());
        assertNull(answering.get(0).getAnswer());
        assertEquals(0, answering.get(1).getBuzzStatus());

        List<Model6RestData> revealed = api.buildModel6BoardData(stage, 4);
        assertEquals(4, revealed.get(0).getPhase());
        assertEquals(2, revealed.get(0).getBuzzStatus());
        assertEquals("A", revealed.get(0).getAnswer());
        assertEquals(0, revealed.get(1).getBuzzStatus());
        assertEquals(100, revealed.get(1).getTeamScore());
    }

    private Teams team(int id, String name) {
        Teams team = new Teams();
        team.setId(id);
        team.setFullName(name);
        team.setMembers("队员1,队员2,队员3");
        team.setTeamScore(100);
        return team;
    }
}
