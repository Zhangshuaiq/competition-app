package competition.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_stage_member_score")
// 每个环节每个成员的得分详情
public class StageMemberScore {

    @TableId(type = IdType.AUTO)
    private Integer id;
    // 队伍
    private String team;
    // 环节
    private String stage;
    // 队员
    private String member;
    // 题号
    private String question;
    // 用时
    private String time;
    // 加分
    private String addScore;
    // 减分
    private String desScore;
    // 环节小计
    private String score;
    // 成绩合计
    private String totalScore;
}
