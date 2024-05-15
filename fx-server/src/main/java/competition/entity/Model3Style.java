package competition.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_model3")
public class Model3Style {
    
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private String titleStyle;
    
    private String qStyle;
    
    // 风险题目大标题样式
    private String headStyle;
}
