package competition.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class Questions {
    
    @ExcelProperty(index = 0, value="阶段编号")
    private String stageNumber;

    @ExcelProperty(index = 1, value="阶段名称")
    private String stageName;

    @ExcelProperty(index = 2, value="问题类型")
    private String questionType;

    @ExcelProperty(index = 3, value="资源类型")
    private String resourceType;

    @ExcelProperty(index = 4, value="资源")
    private String resource;

    @ExcelProperty(index = 5, value="题目")
    private String question;

    @ExcelProperty(index = 6, value="选项A")
    private String choiceA;

    @ExcelProperty(index = 7, value="选项B")
    private String choiceB;

    @ExcelProperty(index = 8, value="选项C")
    private String choiceC;

    @ExcelProperty(index = 9, value="选项D")
    private String choiceD;

    @ExcelProperty(index = 10, value="选项E")
    private String choiceE;

    @ExcelProperty(index = 11, value="选项F")
    private String choiceF;

    @ExcelProperty(index = 12, value="答案")
    private String answer;

    @ExcelProperty(index = 13, value="备注")
    private String remark;

    @ExcelProperty(index = 14, value="所属题组")
    private String group;
    
    @ExcelIgnore
    private Integer questionNumber;
    
    @ExcelIgnore
    private Integer sort;
    
    @ExcelIgnore
    private Integer score;
    
    @ExcelIgnore
    private boolean hasAnswer = false;
    
}
