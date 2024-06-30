package competition.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_time")
public class TimeEntity {
    
    @TableId
    private Integer id;
    
    private Integer time;
}
