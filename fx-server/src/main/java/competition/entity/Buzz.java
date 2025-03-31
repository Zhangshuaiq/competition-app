package competition.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_buzz")
public class Buzz {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String mode;

    private String speed;

    private Integer timeLimit;

    private Integer lastSuccess;

    private Integer light;

    private Integer playError;

    private Integer successMusic;

    private Integer errorMusic;

    private String background;

    private String font;

    private Integer fontSize;

    private String color;

    private Integer bold;

    private Integer itic;

    private Integer underline;

    private String x;

    private String y;
}
