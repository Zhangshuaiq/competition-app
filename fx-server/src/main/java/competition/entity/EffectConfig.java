package competition.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_score_effect")
public class EffectConfig {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer screenEffect = 0;

    private Integer scoreEffect = 0;

    private Integer buzzEffect = 0;
}
