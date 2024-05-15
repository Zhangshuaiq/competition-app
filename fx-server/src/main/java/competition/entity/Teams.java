package competition.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.List;

@Data
@TableName("t_teams")
public class Teams {
    
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private String shortName;
    
    private String fullName;
    
    private String members;
    
    private String code;

    // 出生分数
    private Integer teamScore;

    // 初试分数
    private Integer memberScore;
    
    // 团队密码
    private String password;

    // 当前分数
    @TableField(exist = false)
    private Integer currentScore;

    @TableField(exist = false)
    private List<Integer> memberScores;

    @TableField(exist = false)
    private List<Integer> qScores;
    
    @TableField(exist = false)
    private Integer qScore = 0;

    // 团队答题状态 0 未答  1 已答  2 答对  3 答错  -1 未开始
    @TableField(exist = false)
    private int status = 0;

    // 个人答题状态
    @TableField(exist = false)
    private List<Integer> statusList;
    
    // 团队模式下选择的答案
    @TableField(exist = false)
    private String teamSelect;

    // 新的分数，用于在展示答案时更新最新的分数
    @TableField(exist = false)
    private Integer newScore;
}
