package competition.modal;

import lombok.Data;

// 竞技排行大屏数据模型
@Data
public class Model7RankData implements Comparable<Model7RankData>{
    
    // 团队
    private String team;
    
    // 分数
    private Integer score;
    
    // 时间
    private String time;
    
    // 秒数
    private Integer second;
    
    // 排名
    private Integer rank;

    @Override
    public int compareTo(Model7RankData o) {
        if (this.getScore() < o.getScore()) {
            return 1;
        } else if (this.getScore() > o.getScore()) {
            return -1;
        } else {
//            if (o.getSecond() - this.second > 0) {
//                return 1;
//            }
            return this.getSecond() - o.getSecond();
        }
    }
}
