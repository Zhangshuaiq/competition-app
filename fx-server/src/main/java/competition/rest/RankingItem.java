package competition.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RankingItem {
    private String name;
    private Integer score;
    private Integer currentRank;
    private Integer previousRank;
    private String trend;
    private String trendIcon;
    private String trendClass;
}