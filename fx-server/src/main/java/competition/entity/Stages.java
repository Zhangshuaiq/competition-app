package competition.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_stages")
public class Stages {
    
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private Integer scorePerQ;
    
    private Integer descTime;
    
    private String name;
    
    private Integer randomSort;

    // 显示题号
    private Integer showNumber;
    
    private Integer selectGrey;
    
    private Integer timeCType;
    
    private Integer show;
    
    private Integer questionType;
    
    private String pAType;
    
    private Integer parentId;
    
    private String ruleType;
    
    private String ruleContent;
    
    private Integer rRandomSort;
    
    private Integer level;
    
    private String sortVal;
    
    private String style;
    
    private String questionStyle;
    
    private Integer orderVal;
    
    private Integer m6;

    // 是否开启抢答模式 0 不开启 1 开启
    private Integer openQd = 0;
    
    // 答错扣除分值
    private Integer desValue;
    
    // 当前环节得分是否计入总分
    private Integer countTotal;
    
    // 规则页标题的样式
    private String titleRuleStyle;
    
    // 答案单独展示
    private Integer answerSingle;

    private Integer qdDesc;

    // 抢答犯规的自动扣分数值
    private Integer qdDescValue;

    // 平板作答
    private Integer boardAnswer;

    // 团队答案单独展示
    private Integer teamAnswerSingle;

    public void setQdDescValue(Integer qdDescValue) {
        this.qdDescValue = qdDescValue;
    }

    public Integer getQdDescValue() {
        return qdDescValue;
    }

    public void setQdDesc(Integer qdDesc) {
        this.qdDesc = qdDesc;
    }

    public Integer getQdDesc() {
        return qdDesc;
    }

    public void setOpenQd(Integer openQd) {
        this.openQd = openQd;
    }

    public Integer getOpenQd() {
        return openQd;
    }

    public void setSortVal(String sortVal) {
        this.sortVal = sortVal;
    }

    public String getSortVal() {
        return sortVal;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getLevel() {
        return level;
    }

    public Integer getId() {
        return id;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }


    public Integer getParentId() {
        return parentId;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getScorePerQ() {
        return scorePerQ;
    }

    public void setScorePerQ(Integer scorePerQ) {
        this.scorePerQ = scorePerQ;
    }

    public Integer getDescTime() {
        return descTime;
    }

    public void setDescTime(Integer descTime) {
        this.descTime = descTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getRandomSort() {
        return randomSort;
    }

    public void setRandomSort(Integer randomSort) {
        this.randomSort = randomSort;
    }

    public Integer getShowNumber() {
        return showNumber;
    }

    public void setShowNumber(Integer showNumber) {
        this.showNumber = showNumber;
    }

    public Integer getSelectGrey() {
        return selectGrey;
    }

    public void setSelectGrey(Integer selectGrey) {
        this.selectGrey = selectGrey;
    }

    public Integer getTimeCType() {
        return timeCType;
    }

    public void setTimeCType(Integer timeCType) {
        this.timeCType = timeCType;
    }

    public Integer getShow() {
        return show;
    }

    public void setShow(Integer show) {
        this.show = show;
    }

    public Integer getQuestionType() {
        return questionType;
    }

    public void setQuestionType(Integer questionType) {
        this.questionType = questionType;
    }

    public String getpAType() {
        return pAType;
    }

    public void setpAType(String pAType) {
        this.pAType = pAType;
    }

    public String getRuleType() {
        return ruleType;
    }

    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    public String getRuleContent() {
        return ruleContent;
    }

    public void setRuleContent(String ruleContent) {
        this.ruleContent = ruleContent;
    }

    public Integer getrRandomSort() {
        return rRandomSort;
    }

    public void setrRandomSort(Integer rRandomSort) {
        this.rRandomSort = rRandomSort;
    }
}
