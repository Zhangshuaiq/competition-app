package competition.entity;


import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
// 问题样式，style1 是题干和选项的样式； style2 是答案的样式
@Data
public class QuestionStyle {

    @ExcelProperty(index = 0, value="阶段编号")
    private String stageNo;

    @ExcelProperty(index = 1, value="题目样式")
    
    private String style1;
    @ExcelProperty(index = 2, value="答案样式")
    private String style2;
}
