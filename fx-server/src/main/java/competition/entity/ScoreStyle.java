package competition.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_score_style")
public class ScoreStyle {
    
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    // 样式
    private String styles;
}
