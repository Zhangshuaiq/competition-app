package competition.controller;

import competition.controller.match.Model7;
import competition.controller.preview.Model7Preview;
import competition.entity.Questions;
import competition.entity.Stages;
import competition.entity.Teams;
import competition.mapper.QuestionsMapper;
import competition.modal.Model7RankData;
import competition.ws.Model7RuntimeData;
import competition.ws.WsMessageHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class Model7RoundLifecycleTest {
    Teams team;
    @BeforeEach void prepare() {
        Model7RuntimeData.setUIUpdateCallback(null);
        team = new Teams(); team.setId(1); team.setFullName("测试队"); team.setTeamScore(130);
        Model7RuntimeData.setTeams(Collections.singletonList(team));
    }
    @AfterEach void clean() {
        Model7RuntimeData.setUIUpdateCallback(null);
        Model7RuntimeData.setTeams(Collections.emptyList());
        Model7RuntimeData.executeStart = false;
    }
    Stages stage(int id) {
        Stages s=new Stages();s.setId(id);s.setName("竞技答题");s.setSortVal(String.valueOf(id));s.setQuestionType(7);return s;
    }
    void firstRoundResults() {
        Model7RuntimeData.ModelData data=new Model7RuntimeData.ModelData();
        data.setQuestionNumber(1); data.setAnswer("A"); data.setUserSelect("A");data.setScore(10);
        Model7RuntimeData.getRuntimeData().get(0).add(data);
        Model7RuntimeData.getTimes().set(0,"01:23");
        Model7RuntimeData.getFts().set(0,"01:23");
        Model7RuntimeData.getSeconds().set(0,83);
        Model7RuntimeData.getFinished().set(0,true);
        Model7RuntimeData.persistComputed(Collections.singletonMap(team.getFullName(),10),
                Collections.singletonMap(team.getFullName(),1),Collections.singletonMap(team.getFullName(),1));
    }
    void assertCleanRound() {
        assertTrue(Model7RuntimeData.getRuntimeData().get(0).isEmpty());
        assertEquals(Collections.singletonList("00:00"),Model7RuntimeData.getTimes());
        assertEquals(Collections.singletonList("00:00"),Model7RuntimeData.getFts());
        assertEquals(Collections.singletonList(0),Model7RuntimeData.getSeconds());
        assertEquals(Collections.singletonList(false),Model7RuntimeData.getFinished());
        assertTrue(Model7RuntimeData.getCachedScoreMap().isEmpty());
        assertTrue(Model7RuntimeData.getCachedAnswerMap().isEmpty());
        assertTrue(Model7RuntimeData.getCachedCorrectMap().isEmpty());
        assertEquals(130,team.getTeamScore());
    }
    @Test void startingAgainClearsRoundResultsAndKeepsAccumulatedTotal() {
        firstRoundResults();long previous=Model7RuntimeData.getRoundVersion();
        Model7RuntimeData.setUIUpdateCallback((name,index)->assertCleanRound());
        Model7RuntimeData.reset();
        assertCleanRound();assertTrue(Model7RuntimeData.getRoundVersion()>previous);
    }
    @Test void startingRoundDeletesRecoveryDataBeforeRefreshingThePreview() {
        firstRoundResults();
        ControlPane control=new ControlPane();
        competition.mapper.Model7StatusMapper statuses=mock(competition.mapper.Model7StatusMapper.class);
        Model7Preview preview=mock(Model7Preview.class);
        ReflectionTestUtils.setField(control,"model7StatusMapper",statuses);
        ReflectionTestUtils.setField(control,"previewPane",preview);
        doAnswer(invocation -> {assertCleanRound();return null;}).when(preview).reload();
        ReflectionTestUtils.invokeMethod(control,"prepareModel7Round");
        org.mockito.InOrder order=inOrder(statuses,preview);
        order.verify(statuses).delete(any());order.verify(preview).reload();
        assertTrue(Model7RuntimeData.executeStart);
    }
    @Test void enteringSecondStageClearsPreviousComputedResults() {
        firstRoundResults();
        Model7RuntimeData.setTeams(Collections.singletonList(team));assertCleanRound();
    }
    @Test void previewSwitchUsesStageIdentityEvenWhenBothNamesMatch() {
        Model7Preview preview=new Model7Preview();preview.setData(stage(1));
        ReflectionTestUtils.setField(preview,"timeValue","01:23");
        ReflectionTestUtils.setField(preview,"cachedRankData",Collections.singletonList(new Model7RankData()));
        preview.getQuestionCache().put("questions",Collections.singletonList(new Questions()));
        preview.setData(stage(2));
        assertEquals("00:00",ReflectionTestUtils.getField(preview,"timeValue"));
        assertTrue(((List<?>)ReflectionTestUtils.getField(preview,"cachedRankData")).isEmpty());
        assertTrue(preview.getQuestionCache().isEmpty());
    }
    @Test void restartingSameStageResetsReusedPreviewTimerAndRank() {
        Model7Preview preview=new Model7Preview();Stages first=stage(1);preview.setData(first);
        ReflectionTestUtils.setField(preview,"timeValue","01:23");
        ReflectionTestUtils.setField(preview,"cachedRankData",Collections.singletonList(new Model7RankData()));
        firstRoundResults();Model7RuntimeData.reset();preview.setData(first);
        assertEquals("00:00",ReflectionTestUtils.getField(preview,"timeValue"));
        assertTrue(((List<?>)ReflectionTestUtils.getField(preview,"cachedRankData")).isEmpty());
    }
    @Test void secondStageReloadsItsOwnQuestionsWithSameStageName() {
        Model7Preview preview=new Model7Preview();QuestionsMapper mapper=mock(QuestionsMapper.class);
        Questions first=new Questions();first.setQuestion("第一环节题目");
        Questions second=new Questions();second.setQuestion("第二环节题目");
        when(mapper.selectList(any())).thenReturn(Collections.singletonList(first),Collections.singletonList(second));
        ReflectionTestUtils.setField(preview,"questionsMapper",mapper);
        preview.setData(stage(1));assertSame(first,preview.getQuestionsWithCache().get(0));
        preview.setData(stage(2));assertSame(second,preview.getQuestionsWithCache().get(0));
        verify(mapper,times(2)).selectList(any());
    }
    @Test void externalDisplayResetsTimerOnStageSwitchAndSameStageRestart() {
        Model7 external=new Model7();Stages first=stage(1);external.setData(first);
        ReflectionTestUtils.setField(external,"timeValue","01:23");external.setData(stage(2));
        assertEquals("00:00",ReflectionTestUtils.getField(external,"timeValue"));
        ReflectionTestUtils.setField(external,"timeValue","01:23");Model7RuntimeData.reset();external.setData(stage(2));
        assertEquals("00:00",ReflectionTestUtils.getField(external,"timeValue"));
    }
    @Test void previousRoundsLateRankResultCannotOverwriteNewRound() {
        Model7Preview preview=new Model7Preview();WsMessageHandler messages=mock(WsMessageHandler.class);
        ReflectionTestUtils.setField(preview,"messageHandler",messages);preview.setData(stage(1));
        long revision=((AtomicLong)ReflectionTestUtils.getField(preview,"rankRevision")).get();
        long round=Model7RuntimeData.getRoundVersion();
        Model7RuntimeData.reset();
        ReflectionTestUtils.invokeMethod(preview,"publishRankData",revision,round,Collections.singletonList(new Model7RankData()));
        verifyNoInteractions(messages);
        assertTrue(((List<?>)ReflectionTestUtils.getField(preview,"cachedRankData")).isEmpty());
    }
    @Test void latestRankResultIsPublishedButOlderRequestIsDiscarded() {
        Model7Preview preview=new Model7Preview();WsMessageHandler messages=mock(WsMessageHandler.class);
        ReflectionTestUtils.setField(preview,"messageHandler",messages);preview.setData(stage(1));
        AtomicLong revision=(AtomicLong)ReflectionTestUtils.getField(preview,"rankRevision");
        long old=revision.getAndIncrement();long round=Model7RuntimeData.getRoundVersion();
        List<Model7RankData> result=Collections.singletonList(new Model7RankData());
        ReflectionTestUtils.invokeMethod(preview,"publishRankData",old,round,result);verifyNoInteractions(messages);
        ReflectionTestUtils.invokeMethod(preview,"publishRankData",revision.get(),round,result);
        assertSame(result,ReflectionTestUtils.getField(preview,"cachedRankData"));
        verify(messages).notifyPage(eq("hmodel7"),any());
    }
}
