package competition.service;

import competition.entity.Questions;
import competition.entity.Stages;
import competition.entity.Teams;
import competition.mapper.TeamsMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/** 自选题按环节、题目、队伍结算，同一轮返回旧题也不能重复落分。 */
@lombok.extern.slf4j.Slf4j
@Service
public class StageSelectScoreService {
    @Resource
    private TeamsMapper teamsMapper;
    @Resource
    private ScoreService scoreService;
    @Resource
    private org.springframework.transaction.PlatformTransactionManager transactionManager;

    private final Set<String> settled = new HashSet<>();

    public synchronized void apply(Stages stage, Questions question, Teams team, int delta, String answer) {
        if (!Objects.equals(1, stage.getCountTotal())) {
            return;
        }
        Objects.requireNonNull(stage.getId(), "自选环节缺少ID");
        Objects.requireNonNull(question.getId(), "自选题目缺少ID");
        Objects.requireNonNull(team.getId(), "自选队伍缺少ID");
        String key = stage.getId() + ":" + question.getId() + ":" + team.getId();
        if (settled.contains(key)) {
            log.info("自选题跳过重复结算: key={}, team={}, total={}", key, team.getFullName(), team.getTeamScore());
            return;
        }
        int total = Math.addExact(team.getTeamScore(), delta);
        new TransactionTemplate(transactionManager).executeWithoutResult(status -> {
            // 使用独立对象，提交成功之前不改变界面缓存；总分为0也必须写入。
            Teams update = new Teams();
            update.setId(team.getId());
            update.setTeamScore(total);
            if (teamsMapper.updateById(update) != 1) {
                throw new IllegalStateException("队伍分数保存失败: " + team.getFullName());
            }
            scoreService.recordStageSelectScore(team, stage.getName(), question.getQuestion(), delta,
                    String.valueOf(total), answer);
        });
        team.setTeamScore(total);
        team.setNewScore(0);
        settled.add(key);
        log.info("自选题结算成功: key={}, team={}, delta={}, total={}", key, team.getFullName(), delta, total);
        scoreService.updateStageSelectHardware(team, String.valueOf(total));
    }

    /** 明确重新开始当前题目，允许本题各队在新一轮再次结算。 */
    public synchronized void beginQuestionRound(Stages stage, Questions question) {
        Objects.requireNonNull(stage.getId(), "自选环节缺少ID");
        Objects.requireNonNull(question.getId(), "自选题目缺少ID");
        String prefix = stage.getId() + ":" + question.getId() + ":";
        settled.removeIf(key -> key.startsWith(prefix));
    }

    /** 复位全部选择时清除所有题目的结算记录。 */
    public synchronized void reset() {
        settled.clear();
    }
}
