package competition.controller;

import com.alibaba.fastjson2.JSON;
import competition.entity.Questions;
import competition.entity.Stages;
import competition.entity.Teams;
import competition.modal.BoardAnswerSelectInfo;
import competition.util.RuntimeUtil;
import competition.util.TeamRuntime;
import competition.ws.Model61RuntimeData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class FreeAnswerModeTest {
    @AfterEach
    void reset() {
        Model61RuntimeData.reset();
        TeamRuntime.setTeams(Collections.emptyList());
        RuntimeUtil.boardAnswerMemberInfo = new BoardAnswerSelectInfo();
    }

    @Test
    void independentModeDoesNotRequirePersonalOrTabletFlags() {
        Stages stage = stage(Stages.FREE_ANSWER_TYPE, 0, 0);
        assertTrue(stage.isFreeAnswerMode());
        assertTrue(stage.isAnswerMode());
        assertTrue(stage.isMemberAnswerMode());

        // 内部判断不能变成新的页面消息字段。
        assertFalse(JSON.parseObject(JSON.toJSONString(stage)).containsKey("freeAnswerMode"));
    }

    @Test
    void existingModesAndOldFlagsDoNotEnableFreeAnswer() {
        Stages team = stage(6, 0, 0);
        assertTrue(team.isAnswerMode());
        assertFalse(team.isMemberAnswerMode());
        assertFalse(team.isFreeAnswerMode());

        Stages personal = stage(6, 1, 0);
        assertTrue(personal.isMemberAnswerMode());
        assertFalse(personal.isFreeAnswerMode());

        Stages tablet = stage(8, 0, 1);
        assertFalse(tablet.isAnswerMode());
        assertFalse(tablet.isFreeAnswerMode());

        Stages oldFlags = stage(6, 1, 1);
        assertFalse(oldFlags.isFreeAnswerMode());
        assertTrue(oldFlags.isMemberAnswerMode());
    }

    @Test
    void onlySelectedTeamsAndMemberArePreparedAndMarkedAfterReveal() {
        Teams first = team("一组");
        Teams second = team("二组");
        TeamRuntime.setTeams(Arrays.asList(first, second));
        BoardAnswerSelectInfo selection = new BoardAnswerSelectInfo();
        selection.setSelectTeams(Collections.singletonList(first));
        selection.setSelectMemberIndex(1);
        selection.setAccumulateMemberTime(true);
        RuntimeUtil.boardAnswerMemberInfo = selection;

        Stages stage = stage(Stages.FREE_ANSWER_TYPE, 0, 0);
        new ControlPane().prepareMemberAnswerTeams(stage);
        Questions question = new Questions();
        question.setAnswer("A");
        Model61RuntimeData.setRuntimeData(question);

        assertEquals(Collections.singletonList("一组"), stage.getFreeAnswerTeams());
        assertEquals(1, stage.getFreeAnswerMemberIndex());
        assertTrue(stage.getFreeAnswerTimeRank());
        assertEquals(1, Model61RuntimeData.getRuntimeData().size());
        assertTrue(Model61RuntimeData.accepts("一组", "队员2"));
        assertFalse(Model61RuntimeData.accepts("一组", "队员1"));
        assertFalse(Model61RuntimeData.accepts("二组", "队员2"));

        Model61RuntimeData.updateParticipantStatuses();
        assertEquals(Arrays.asList(0, 3, 0), first.getStatusList());
        assertEquals(Arrays.asList(0, 0, 0), second.getStatusList());
    }

    @Test
    void changingSelectionAffectsNextQuestionAndNotCurrentSnapshot() {
        Teams first = team("一组");
        Teams second = team("二组");
        TeamRuntime.setTeams(Arrays.asList(first, second));
        BoardAnswerSelectInfo selection = new BoardAnswerSelectInfo();
        selection.setSelectTeams(Collections.singletonList(first));
        selection.setSelectMemberIndex(0);
        RuntimeUtil.boardAnswerMemberInfo = selection;
        Stages stage = stage(Stages.FREE_ANSWER_TYPE, 0, 0);
        ControlPane control = new ControlPane();
        control.prepareMemberAnswerTeams(stage);

        selection.setSelectTeams(Collections.singletonList(second));
        selection.setSelectMemberIndex(2);
        assertEquals(Collections.singletonList("一组"), stage.getFreeAnswerTeams());
        assertEquals(0, stage.getFreeAnswerMemberIndex());
        assertTrue(Model61RuntimeData.accepts("一组", "队员1"));

        control.prepareMemberAnswerTeams(stage);
        assertEquals(Collections.singletonList("二组"), stage.getFreeAnswerTeams());
        assertEquals(2, stage.getFreeAnswerMemberIndex());
        assertTrue(Model61RuntimeData.accepts("二组", "队员3"));
        assertFalse(Model61RuntimeData.accepts("一组", "队员1"));
    }

    @Test
    void ordinaryPersonalModeStillPreparesEveryMember() {
        Teams first = team("一组");
        Teams second = team("二组");
        TeamRuntime.setTeams(Arrays.asList(first, second));
        new ControlPane().prepareMemberAnswerTeams(stage(6, 1, 0));
        assertEquals(6, Model61RuntimeData.getRuntimeData().size());
    }

    private Stages stage(int type, int personal, int tablet) {
        Stages stage = new Stages();
        stage.setQuestionType(type);
        stage.setM6(personal);
        stage.setBoardAnswer(tablet);
        return stage;
    }

    private Teams team(String name) {
        Teams team = new Teams();
        team.setFullName(name);
        team.setMembers("[\"队员1\",\"队员2\",\"队员3\"]");
        team.setStatusList(Arrays.asList(0, 0, 0));
        team.setQScores(Arrays.asList(0, 0, 0));
        return team;
    }
}
