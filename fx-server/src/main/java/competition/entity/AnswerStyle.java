package competition.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_answer_style")
public class AnswerStyle {
    
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private String style1;

    private String style2;

    private String style3;

    private String style4;

    private String style5;

    private String style6;
    
}
