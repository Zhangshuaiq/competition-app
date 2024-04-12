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
    
    private String styles;
}
