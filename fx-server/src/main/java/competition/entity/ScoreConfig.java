package competition.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("t_score_config")
@Data
public class ScoreConfig {
    
    @TableId
    private Integer id;
    
    private Integer size = 24;
    
    private Integer x = 0;
    
    private Integer y = 0;
    
    private Integer show = 1;
    
    private String color = "#ffffff";
}
