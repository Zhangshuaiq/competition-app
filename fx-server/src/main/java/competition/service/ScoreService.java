package competition.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import competition.entity.StageMemberScore;
import competition.entity.Teams;
import competition.mapper.StageMemberScoreMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
@Service
@Slf4j
public class ScoreService {

    @Resource
    private StageMemberScoreMapper scoreMapper;


    @Resource
    private HardwareService hardwareService;

    /**
     * 异步添加分数 - 使用专用的写执行器
     * 关键：指定sqliteWriteExecutor确保写操作串行执行
     */
    @Async("scoreTaskExecutor")  // 使用单线程写执行器
    public void addScore(String member, Teams team, String stage,
                         String question, String time, int score,
                         String totalScore, String answerText) {

        try {
            // 数据库写操作 - 在单线程中执行，避免并发冲突
            performDatabaseWrite(member, team, stage, question, time, score, totalScore, answerText);

        } catch (Exception e) {
            log.error("数据库写入失败: {} - {}", team.getFullName(), e.getMessage());
            // 可以考虑重试机制或记录到失败队列
        }

        // 硬件显示操作异步执行（不影响数据库操作）
        hardwareService.updateHardwareDisplayAsync(team, totalScore);
    }

    public void updateStageSelectHardware(Teams team, String totalScore) {
        try {
            hardwareService.updateHardwareDisplayAsync(team, totalScore);
        } catch (Exception e) {
            log.error("自选题已计分，硬件显示刷新失败: {}", team.getFullName(), e);
        }
    }

    // 自选题与团队总分在调用方的同一个事务中保存，失败直接上抛以便重试。
    public void recordStageSelectScore(Teams team, String stage, String question, int score,
                                       String totalScore, String answerText) {
        handleTeamScore(team, stage, question, null, score, totalScore, answerText);
    }

    /**
     * 执行数据库写入操作
     */
    private void performDatabaseWrite(String member, Teams team, String stage,
                                      String question, String time, int score,
                                      String totalScore, String answerText) {

        if (member == null) {
            // 团队分数记录
            handleTeamScore(team, stage, question, time, score, totalScore, answerText);
        } else {
            // 个人分数记录
            handleMemberScore(member, team, stage, question, time, score, totalScore, answerText);
        }
    }

    /**
     * 处理团队分数
     */
    private void handleTeamScore(Teams team, String stage, String question, String time,
                                 int score, String totalScore, String answerText) {

        List<StageMemberScore> scores = scoreMapper.selectList(new LambdaQueryWrapper<StageMemberScore>()
            .eq(StageMemberScore::getTeam, team.getFullName())
            .eq(StageMemberScore::getStage, stage)
        );

        StageMemberScore memberScore = new StageMemberScore();
        memberScore.setTeam(team.getFullName());
        memberScore.setStage(stage);
        memberScore.setQuestion(question);
        memberScore.setTime(time);
        memberScore.setTotalScore(totalScore);
        memberScore.setAnswerText(answerText);

        if (score > 0) {
            memberScore.setAddScore(score + "");
        } else {
            memberScore.setDesScore((-score) + "");
        }

        if (scores.isEmpty()) {
            memberScore.setScore(score + "");
        } else {
            int stageAdd = scores.stream()
                .filter(x -> !StringUtils.isEmpty(x.getAddScore()))
                .mapToInt(x -> Integer.parseInt(x.getAddScore()))
                .sum();

            int stageDes = scores.stream()
                .filter(x -> !StringUtils.isEmpty(x.getDesScore()))
                .mapToInt(x -> Integer.parseInt(x.getDesScore()))
                .sum();

            memberScore.setScore((score + stageAdd - stageDes) + "");
        }

        scoreMapper.insert(memberScore);
    }

    /**
     * 处理个人分数
     */
    private void handleMemberScore(String member, Teams team, String stage, String question,
                                   String time, int score, String totalScore, String answerText) {

        List<StageMemberScore> scores = scoreMapper.selectList(new LambdaQueryWrapper<StageMemberScore>()
            .eq(StageMemberScore::getTeam, team.getFullName())
            .eq(StageMemberScore::getStage, stage)
        );

        StageMemberScore memberScore = new StageMemberScore();
        memberScore.setTeam(team.getFullName());
        memberScore.setMember(member);
        memberScore.setStage(stage);
        memberScore.setQuestion(question);
        memberScore.setTime(time);
        memberScore.setAnswerText(answerText);
        memberScore.setTotalScore(totalScore);

        if (score > 0) {
            memberScore.setAddScore(score + "");
        } else {
            memberScore.setDesScore((-score) + "");
        }

        if (scores.isEmpty()) {
            memberScore.setScore(score + "");
        } else {
            int stageAdd = scores.stream()
                .filter(x -> !StringUtils.isEmpty(x.getAddScore()))
                .mapToInt(x -> Integer.parseInt(x.getAddScore()))
                .sum();

            int stageDes = scores.stream()
                .filter(x -> !StringUtils.isEmpty(x.getDesScore()))
                .mapToInt(x -> Integer.parseInt(x.getDesScore()))
                .sum();

            memberScore.setScore((score + stageAdd - stageDes) + "");
        }

        scoreMapper.insert(memberScore);
    }



    /**
     * 同步读取操作 - 可以并发执行
     */
    public List<StageMemberScore> getScoresByTeamAndStage(String teamName, String stage) {
        return scoreMapper.selectList(new LambdaQueryWrapper<StageMemberScore>()
            .eq(StageMemberScore::getTeam, teamName)
            .eq(StageMemberScore::getStage, stage)
        );
    }
}