package competition.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

//环节层级的样式
@Data
@TableName("t_stages_style")
public class StagesStyle {
    
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer parentId;
    
    private String style;
}
