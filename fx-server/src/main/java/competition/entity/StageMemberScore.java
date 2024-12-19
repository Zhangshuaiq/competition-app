package competition.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_stage_member_score")
// 每个环节每个成员的得分详情
public class StageMemberScore {

    @ExcelIgnore
    @TableId(type = IdType.AUTO)
    private Integer id;

    @ExcelProperty(index = 0, value="队伍")
    // 队伍
    private String team;

    @ExcelProperty(index = 1, value="环节")
    // 环节
    private String stage;

    @ExcelProperty(index = 2, value="队员")
    // 队员
    private String member;

    @ExcelProperty(index = 3, value="题号")
    // 题号
    private String question;

    @ExcelProperty(index = 4, value="用时")
    // 用时
    private String time;

    @ExcelProperty(index = 5, value="加分")
    // 加分
    private String addScore;

    @ExcelProperty(index = 6, value="减分")
    // 减分
    private String desScore;

    @ExcelProperty(index = 7, value="环节小计")
    // 环节小计
    private String score;

    @ExcelProperty(index = 8, value="成绩合计")
    // 成绩合计
    private String totalScore;

    @ExcelProperty(index = 9, value="回答的内容")
    // 回答的内容
    private String answerText;
}
