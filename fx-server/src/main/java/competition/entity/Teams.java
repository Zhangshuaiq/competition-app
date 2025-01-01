package competition.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.List;

@Data
@TableName("t_teams")
public class Teams {
    
    @ExcelIgnore
    @TableId(type = IdType.AUTO)
    private Integer id;

    @ExcelProperty(index = 0, value="编号")
    private String code;

    @ExcelProperty(index = 1, value="团队简称")
    private String shortName;

    @ExcelProperty(index = 2, value="团队名称")
    private String fullName;

    @ExcelProperty(index = 3, value="队员")
    private String members;
    
    @ExcelIgnore
    // 团队分数
    private Integer teamScore;
    
    @ExcelIgnore
    // 成员分数
    private String memberScore;

    @ExcelProperty(index = 4, value="密码")
    // 团队密码
    private String password;

//    @ExcelIgnore
//    // 当前分数
//    @TableField(exist = false)
//    private Integer currentScore;
//
//    @ExcelIgnore
//    @TableField(exist = false)
//    private List<Integer> memberScores;

    @ExcelIgnore
    @TableField(exist = false)
    private List<Integer> qScores;

    @ExcelIgnore
    @TableField(exist = false)
    private Integer qScore = 0;

    @ExcelIgnore
    // 团队答题状态 0 未答  1 已答  2 答对  3 答错  -1 未开始
    @TableField(exist = false)
    private int status = 0;

    @ExcelIgnore
    // 个人答题状态
    @TableField(exist = false)
    private List<Integer> statusList;

    @ExcelIgnore
    // 团队模式下选择的答案
    @TableField(exist = false)
    private String teamSelect;

    @ExcelIgnore
    // 新的分数，用于在展示答案时更新最新的分数
    @TableField(exist = false)
    private Integer newScore;

    @ExcelIgnore
    // 抢答状态
    @TableField(exist = false)
    private boolean buzzStatus = false;

    @ExcelIgnore
    // 抢答时间
    @TableField(exist = false)
    private String buzzTime;

}
