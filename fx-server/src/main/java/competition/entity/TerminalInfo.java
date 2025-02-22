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

    private Integer connectPort;

    private String screenList;

    private String buzzList;
}
