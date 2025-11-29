package competition.modal;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;

// 竞技排行大屏数据模型
@Data
@ContentRowHeight(20)
@HeadRowHeight(25)
public class Model7RankData implements Comparable<Model7RankData>{
    
    // 团队
    @ExcelProperty(value = "团队", index = 0)
    private String team;
    
    // 分数
    @ExcelProperty(value = "分数", index = 1)
    private Integer score;
    
    // 时间
    @ExcelProperty(value = "时间", index = 2)
    private String time;
    
    // 秒数
    @ExcelProperty(value = "秒数", index = 3)
    private Integer second;
    
    // 排名
    @ExcelProperty(value = "排名", index = 4)
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
