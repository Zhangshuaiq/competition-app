package competition.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_terminal")
public class TerminalInfo {

    @TableId(type = IdType.AUTO)
    private Integer id;

    // 显示器信道
    private Integer connectPort;

    // 抢答器信道
    private Integer buzzPort;

    private String screenList;

    private String buzzList;
}
