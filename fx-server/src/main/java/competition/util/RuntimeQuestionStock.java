package competition.util;

import competition.entity.QuestionStyle;
import competition.entity.Questions;

import java.util.List;

public class RuntimeQuestionStock {
    
    private static List<Questions> questions =  ExcelParser.parse(ExcelParser.Q_PATH, Questions.class);
    
    static {
        // 修复样式和问题的数量不一致
        List<QuestionStyle> styleList = ExcelParser.parse(ExcelParser.S_PATH, QuestionStyle.class);
        for (int i = 0; i < questions.size(); i++) {
            if (i < styleList.size()) {
                styleList.get(i).setStageNo(questions.get(i).getStageNumber());
            }
        }
        
        if (styleList.size() < questions.size()) {
            int i = styleList.size();
            for (; i < questions.size(); i++) {
                QuestionStyle questionStyle = new QuestionStyle();
                questionStyle.setStageNo(questions.get(i).getStageNumber());
                styleList.add(questionStyle);
            }
        }

        ExcelParser.write(ExcelParser.S_PATH, styleList, QuestionStyle.class);

    }

    public static List<Questions> getQuestions() {
        return questions;
    }
}
