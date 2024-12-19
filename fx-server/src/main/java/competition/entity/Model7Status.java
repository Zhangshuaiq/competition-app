package competition.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_model7_status")
public class Model7Status {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer teamId;

    private String questionText;

    private Integer currentNo;

    private Integer score;

    private String userSelects;
}
