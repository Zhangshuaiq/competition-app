package competition.service;

import competition.entity.*;
import competition.mapper.TeamsMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.SimpleTransactionStatus;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class StageSelectScoreServiceTest {
    StageSelectScoreService service;
    TeamsMapper mapper;
    ScoreService scores;
    PlatformTransactionManager transactions;
    Stages stage;
    Questions question;
    Teams team;

    @BeforeEach
    void setup() {
        service = new StageSelectScoreService();
        mapper = mock(TeamsMapper.class);
        scores = mock(ScoreService.class);
        transactions = mock(PlatformTransactionManager.class);
        when(transactions.getTransaction(any())).thenReturn(new SimpleTransactionStatus());
        when(mapper.updateById(any())).thenReturn(1);
        ReflectionTestUtils.setField(service, "teamsMapper", mapper);
        ReflectionTestUtils.setField(service, "scoreService", scores);
        ReflectionTestUtils.setField(service, "transactionManager", transactions);
        stage = new Stages(); stage.setId(1); stage.setName("子环节"); stage.setCountTotal(1);
        question = new Questions(); question.setId(10); question.setQuestion("题目");
        team = new Teams(); team.setId(1); team.setFullName("队伍"); team.setTeamScore(100);
    }

    @Test
    void settlesEachNewQuestionAndStageButNeverRepeatsOldQuestion() {
        service.apply(stage, question, team, 10, "A");
        service.apply(stage, question, team, 10, "A");
        question.setId(11);
        service.apply(stage, question, team, 10, "A");
        question.setId(10);
        service.apply(stage, question, team, 10, "A");
        assertEquals(120, team.getTeamScore());
        stage.setId(2);
        service.apply(stage, question, team, 10, "A");
        assertEquals(130, team.getTeamScore());
        verify(mapper, times(3)).updateById(any());
    }

    @Test
    void persistsZeroTotalAndDeduplicatesEachTeamIndependently() {
        team.setTeamScore(-10);
        service.apply(stage, question, team, 10, "A");
        assertEquals(0, team.getTeamScore());
        verify(mapper).updateById(argThat(t -> t.getTeamScore() == 0));
        team.setId(2);
        service.apply(stage, question, team, 10, "A");
        assertEquals(10, team.getTeamScore());
    }

    @Test
    void failedRecordRollsBackAndDoesNotConsumeSettlement() {
        doThrow(new IllegalStateException("write failed")).doNothing().when(scores)
                .recordStageSelectScore(any(), any(), any(), anyInt(), any(), any());
        assertThrows(IllegalStateException.class, () -> service.apply(stage, question, team, 10, "A"));
        assertEquals(100, team.getTeamScore());
        verify(transactions).rollback(any());
        service.apply(stage, question, team, 10, "A");
        assertEquals(110, team.getTeamScore());
    }

    @Test
    void failedTotalWriteOrCommitCanBeRetriedWithoutChangingCachedTotal() {
        when(mapper.updateById(any())).thenReturn(0, 1, 1);
        assertThrows(IllegalStateException.class, () -> service.apply(stage, question, team, 10, "A"));
        assertEquals(100, team.getTeamScore());
        verifyNoInteractions(scores);
        doThrow(new IllegalStateException("commit failed")).doNothing().when(transactions).commit(any());
        assertThrows(IllegalStateException.class, () -> service.apply(stage, question, team, 10, "A"));
        assertEquals(100, team.getTeamScore());
        service.apply(stage, question, team, 10, "A");
        assertEquals(110, team.getTeamScore());
    }

    @Test
    void explicitRestartAllowsSameQuestionToScoreAgainButDoesNotResetOtherQuestions() {
        service.apply(stage, question, team, 10, "A");
        question.setId(11);
        service.apply(stage, question, team, 10, "A");
        question.setId(10);
        service.beginQuestionRound(stage, question);
        service.apply(stage, question, team, 10, "A");
        service.apply(stage, question, team, 10, "A");
        assertEquals(130, team.getTeamScore());
        question.setId(11);
        service.apply(stage, question, team, 10, "A");
        assertEquals(130, team.getTeamScore());
        verify(mapper, times(3)).updateById(any());
    }

    @Test
    void resetStartsNewRoundAndDisabledScoringDoesNotConsumeSettlement() {
        stage.setCountTotal(0);
        service.apply(stage, question, team, 10, "A");
        assertEquals(100, team.getTeamScore());
        stage.setCountTotal(1);
        service.apply(stage, question, team, 10, "A");
        service.reset();
        service.apply(stage, question, team, -5, "B");
        assertEquals(105, team.getTeamScore());
    }
}
