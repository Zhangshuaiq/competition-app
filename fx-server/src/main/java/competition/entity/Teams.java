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
    
    private Integer teamScore;
    
    private Integer memberScore;
    
    @TableField(exist = false)
    private Integer currentScore;

    @TableField(exist = false)
    private List<Integer> memberScores;
    
}
