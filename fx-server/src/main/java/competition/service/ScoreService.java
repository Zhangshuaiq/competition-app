package competition.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import competition.entity.StageMemberScore;
import competition.mapper.StageMemberScoreMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ScoreService {
    
    @Resource
    private StageMemberScoreMapper scoreMapper;
    
    public void addScore(String member, String team, String stage,
                         String question, String time, int score, String totalScore){
        
        if (member == null) {
            List<StageMemberScore> scores = scoreMapper.selectList(new LambdaQueryWrapper<StageMemberScore>()
                    .eq(StageMemberScore::getTeam, team)
                    .eq(StageMemberScore::getStage, stage)
            );
            StageMemberScore memberScore = new StageMemberScore();
            memberScore.setTeam(team);
            memberScore.setStage(stage);
            memberScore.setQuestion(question);
            memberScore.setTime(time);
            memberScore.setTotalScore(totalScore);
            if (score > 0) {
                memberScore.setAddScore(score + "");
            } else {
                memberScore.setDesScore(-score + "");
            }
            if (scores.isEmpty()) {
                memberScore.setScore(score + "");
            } else {
                int stageAdd = scores.stream().filter(x -> !StringUtils.isEmpty(x.getAddScore()))
                        .mapToInt(x -> Integer.parseInt(x.getAddScore())).sum();

                int stageDes = - scores.stream().filter(x -> !StringUtils.isEmpty(x.getDesScore()))
                        .mapToInt(x -> Integer.parseInt(x.getDesScore())).sum();
                
                memberScore.setScore((score + stageAdd + stageDes) + "");
            }
            scoreMapper.insert(memberScore);
        } else {
            scoreMapper.selectList(new LambdaQueryWrapper<StageMemberScore>()
                    .eq(StageMemberScore::getTeam, team)
                    .eq(StageMemberScore::getMember, member)
                    .eq(StageMemberScore::getStage, stage)
            );
            List<StageMemberScore> scores = scoreMapper.selectList(new LambdaQueryWrapper<StageMemberScore>()
                    .eq(StageMemberScore::getTeam, team)
                    .eq(StageMemberScore::getStage, stage)
            );
            StageMemberScore memberScore = new StageMemberScore();
            memberScore.setTeam(team);
            memberScore.setMember(member);
            memberScore.setStage(stage);
            memberScore.setQuestion(question);
            memberScore.setTime(time);
            memberScore.setTotalScore(totalScore);
            if (score > 0) {
                memberScore.setAddScore(score + "");
            } else {
                memberScore.setDesScore(-score + "");
            }
            if (scores.isEmpty()) {
                memberScore.setScore(score + "");
            } else {
                int stageAdd = scores.stream().filter(x -> !StringUtils.isEmpty(x.getAddScore()))
                        .mapToInt(x -> Integer.parseInt(x.getAddScore())).sum();

                int stageDes = - scores.stream().filter(x -> !StringUtils.isEmpty(x.getDesScore()))
                        .mapToInt(x -> Integer.parseInt(x.getDesScore())).sum();

                memberScore.setScore((score + stageAdd + stageDes) + "");
            }
            scoreMapper.insert(memberScore);
        }
        
    }
}
