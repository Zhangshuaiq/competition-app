package competition.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_score_config1")
public class ScoreConfig1 {
    
    @TableId
    private Integer id;
    
    private Integer size = 24;
    
    private Integer rows = 3;

    private Integer cols = 4;
    
    private String color = "#ffffff";
    
    private Integer x = 20;
    
    private Integer y = 20;
}
