package competition.modal;

import lombok.Data;

import java.util.List;

@Data
public class TeamResult {
    
    private String correctAnswer;
    
    private List<DataItem> dataItemList;
    
    
    @Data
    public static class DataItem {
        private String teamName;
        
        private String member;
        
        private String answer;
        
        private int score;
    }
}
