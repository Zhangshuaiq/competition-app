package competition.modal;

import lombok.Data;

// 选手排行大屏数据模型
@Data
public class TeamMemberRankData implements Comparable<TeamMemberRankData>{
    
    // 排名
    private Integer rank;
    
    // 团队
    private String team;

    private String shortName;
    
    // 分数
    private Long score;
    
    // 成员
    private String member;


    @Override
    public int compareTo(TeamMemberRankData o) {
        return (int) (score - o.score);
    }
}
