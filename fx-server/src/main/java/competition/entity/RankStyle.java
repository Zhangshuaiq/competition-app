package competition.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_rank_style")
public class RankStyle {
    
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private String headConfig;
    
    private String rankConfig;
}
