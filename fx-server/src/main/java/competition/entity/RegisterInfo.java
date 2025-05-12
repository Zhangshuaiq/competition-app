package competition.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_register")
public class RegisterInfo {
    
    @TableId
    private Integer id;

    @TableField("active_code")
    private String activeCode;
    
    private int countVal;

    private String lastCheckTime;

    private String deviceCode;
}
