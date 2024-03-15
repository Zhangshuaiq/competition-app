package competition.util;

import competition.entity.Questions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CurrentQuestions {
    
    private static Map<String, Questions> questionsMap = new HashMap<>();
    
    private static Map<String, Boolean> answerMap = new HashMap<>();
    
    private static Integer stageId;
    
    
    public static List<Questions> getQuestionList(){
        return new ArrayList<>(questionsMap.values());
    }

    public static void setQuestions(List<Questions> questions, Integer stageId) {
        if (CurrentQuestions.stageId == null || !CurrentQuestions.stageId.equals(stageId)) {
            CurrentQuestions.stageId = stageId;
            questionsMap.clear();
            answerMap.clear();
            for (Questions q : questions) {
                questionsMap.put(q.getQuestionNumber().toString(), q);
            }
        }
        
    }
    
    public static Questions getQ(String key) {
        return questionsMap.get(key);
    }
    
    public static void recordAnswer(String key) {
        answerMap.put(key, true);
    }


    public static boolean hasAnswer(String key) {
        return answerMap.getOrDefault(key, false);
    }
}
