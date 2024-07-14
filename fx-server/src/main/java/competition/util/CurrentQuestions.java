package competition.util;

import competition.JavaFxApplication;
import competition.entity.Questions;
import competition.mapper.QuestionsMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CurrentQuestions {
    
    private static Map<String, Questions> questionsMap = new HashMap<>();

    // 普通题
    private static List<Questions> questions = new ArrayList<>();
    
    // 风险题，由于风险题展示的是多个子环节的题目，因此需要单独处理
    private static List<List<Questions>> m3Questions = new ArrayList<>();
    
    
    private static Integer stageId;
    
    
//    public static List<Questions> getQuestionList(){
//        return questions;
//    }

//    public static void setQuestions(List<Questions> questions, Integer stageId) {
//        CurrentQuestions.questions = questions;
//        if (CurrentQuestions.stageId == null || !CurrentQuestions.stageId.equals(stageId)) {
//            CurrentQuestions.stageId = stageId;
//            questionsMap.clear();
//            for (Questions q : questions) {
//                questionsMap.put(q.getQuestionNumber().toString(), q);
//            }
//        }
//    }
    
//    public static void setM3Questions(List<List<Questions>> questions, Integer stageId) {
//        CurrentQuestions.m3Questions = questions;
//        if (CurrentQuestions.stageId == null || !CurrentQuestions.stageId.equals(stageId)) {
//            CurrentQuestions.stageId = stageId;
//            questionsMap.clear();
//            for (List<Questions> question : questions) {
//                for (Questions q : question) {
//                    questionsMap.put(q.getStageNumber() + "-" + q.getQuestionNumber(), q);
//                }
//            }
//
//        }
//    }
    
//    public static Questions getQ(String key) {
//        return questionsMap.get(key);
//    }
//    
//    public static void recordAnswer(String key) {
//        QuestionsMapper questionsMapper = JavaFxApplication.getApplicationContext().getBean(QuestionsMapper.class);
//        Questions questions = questionsMap.get(key);
//        questions.setHasAnswer(true);
//        questionsMapper.updateById(questions);
//    }
//
//    public static Integer getStageId() {
//        return stageId;
//    }
//
//    public static boolean hasAnswer(String key) {
//        return questionsMap.get(key).isHasAnswer();
//    }
//
//    public static List<List<Questions>> getM3Questions() {
//        return m3Questions;
//    }
}
