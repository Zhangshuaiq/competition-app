package competition.resp;

import lombok.Data;

@Data
public class Model6RestData {
    private String id;
    private String fullName;
    private Integer buzzStatus;
    private String answer;
    private Integer teamScore;
    /** 当前展示阶段：1=默认(非答题模式) 2=答题等待 3=作答中 4=答案公布 */
    private Integer phase;
}
