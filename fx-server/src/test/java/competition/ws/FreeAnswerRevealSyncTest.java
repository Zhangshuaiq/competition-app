package competition.ws;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import competition.controller.ControlPane;
import competition.entity.Questions;
import competition.entity.Stages;
import competition.entity.Teams;
import competition.util.TeamRuntime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class FreeAnswerRevealSyncTest {
    @AfterEach
    void reset() {
        Model61RuntimeData.reset();
        TeamRuntime.setTeams(Collections.emptyList());
    }

    @Test
    void pageRefreshCarriesRevealedStatusesAndScoresWithoutChangingUnselectedTeams() {
        Teams correct = team("正确组", 101);
        Teams wrong = team("错误组", 99);
        Teams unanswered = team("未答组", 99);
        Teams excluded = team("未指定组", 100);
        TeamRuntime.setTeams(Arrays.asList(correct, wrong, unanswered, excluded));
        Model61RuntimeData.setTeams(Arrays.asList(correct, wrong, unanswered), 1);
        Questions question = new Questions();
        question.setAnswer("A");
        Model61RuntimeData.setRuntimeData(question);
        Model61RuntimeData.getRuntimeData().get(0).setUserSelect("A");
        Model61RuntimeData.getRuntimeData().get(1).setUserSelect("B");
        correct.getStatusList().set(1, 1);
        wrong.getStatusList().set(1, 1);
        Model61RuntimeData.updateParticipantStatuses();

        Stages stage = new Stages();
        stage.setQuestionType(Stages.FREE_ANSWER_TYPE);
        stage.setAnswerSingle(0);
        ControlPane control = new ControlPane();
        ReflectionTestUtils.setField(control, "currentStage", stage);
        SimpMessagingTemplate transport = mock(SimpMessagingTemplate.class);
        WsMessageHandler handler = new WsMessageHandler();
        ReflectionTestUtils.setField(handler, "cachedControlPane", control);
        ReflectionTestUtils.setField(handler, "messagingTemplate", transport);

        handler.notifyPage("", null);

        ArgumentCaptor<String> payload = ArgumentCaptor.forClass(String.class);
        verify(transport).convertAndSend(eq(WsMessageHandler.PAGE_TOPIC), payload.capture());
        JSONObject message = JSON.parseObject(payload.getValue());
        assertEquals("", message.getString("route")); // 刷新当前网页，不跳转。
        JSONArray teams = message.getJSONArray("teams");
        assertTeam(teams.getJSONObject(0), 101, 2);
        assertTeam(teams.getJSONObject(1), 99, 3);
        assertTeam(teams.getJSONObject(2), 99, 3);
        assertTeam(teams.getJSONObject(3), 100, 0);
        assertEquals(Arrays.asList(0, 0, 0), excluded.getStatusList());
    }

    private void assertTeam(JSONObject team, int score, int status) {
        assertEquals(score, team.getIntValue("teamScore"));
        assertEquals(status, team.getJSONArray("statusList").getIntValue(1));
    }

    private Teams team(String name, int score) {
        Teams team = new Teams();
        team.setFullName(name);
        team.setMembers("队员1,队员2,队员3");
        team.setTeamScore(score);
        team.setStatusList(Arrays.asList(0, 0, 0));
        return team;
    }
}
