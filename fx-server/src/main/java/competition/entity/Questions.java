package competition.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_questions")
public class Questions {
    
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    @ExcelProperty(index = 0, value="环节编号")
    private String stageNumber;

    @ExcelProperty(index = 1, value="环节名称")
    private String stageName;

    // 判断题 简答题 填空题 单选题 多选题 不定项选择
    @ExcelProperty(index = 2, value="问题类型")
    private String questionType;

    // 文字  图片 音频  视频
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

//    @ExcelProperty(index = 13, value="答案类型")
//    private String answerType;

    @TableField("`group`")
    @ExcelIgnore
    private String group;
    
    @ExcelIgnore
    private Integer questionNumber;

    public Integer getQuestionNumber() {
        return questionNumber;
    }

    @ExcelIgnore
    private Integer sort;

    @TableField(exist = false)
    @ExcelIgnore
    private Integer order;

    @ExcelIgnore
    private Integer viewSort;
    
    @TableField(exist = false)
    @ExcelIgnore
    private Integer score;
    
    @ExcelIgnore
    private boolean hasAnswer = false;

    // 答错扣分
    @TableField(exist = false)
    @ExcelIgnore
    private Integer desValue;

    //  题型标注
    @TableField(exist = false)
    @ExcelIgnore
    private String txbz;
    
}
