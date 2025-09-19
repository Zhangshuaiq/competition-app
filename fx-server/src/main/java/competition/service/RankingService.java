package competition.service;

import competition.entity.Teams;
import competition.rest.RankingItem;
import competition.util.TeamRuntime;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RankingService {

    // 模拟上次排名数据存储
    private Map<String, Integer> lastRankingMap = new HashMap<>();

    /**
     * 获取排行榜数据
     */
    public List<RankingItem> getRankingData() {
        List<String> teamNames = TeamRuntime.getTeams().stream()
                .map(Teams::getFullName).collect(Collectors.toList());

        List<RankingItem> currentData =  TeamRuntime.getTeams().stream()
                .map(team -> {
                    RankingItem item = new RankingItem();
                    item.setName(team.getFullName());
                    item.setScore(team.getTeamScore());
                    item.setPreviousRank(lastRankingMap.getOrDefault(team.getFullName(), 0));
                    return item;
                })
                .collect(Collectors.toList());

        // 按分数降序排序
        currentData.sort((a, b) -> b.getScore().compareTo(a.getScore()));

        // 设置当前排名和趋势
        for (int i = 0; i < currentData.size(); i++) {
            RankingItem item = currentData.get(i);
            int currentRank = i + 1;
            item.setCurrentRank(currentRank);

            // 计算趋势
            calculateTrend(item, currentRank);
        }

        // 更新上次排名记录
        updateLastRanking(currentData);

        return currentData;
    }
    /**
     * 计算趋势
     */
    private void calculateTrend(RankingItem item, int currentRank) {
        if (item.getPreviousRank() == 0) {
            item.setTrend("same");
            item.setTrendIcon("");
            item.setTrendClass("");
        } else if (currentRank < item.getPreviousRank()) {
            item.setTrend("up");
            item.setTrendIcon("fa-arrow-up");
            item.setTrendClass("row-up");
        } else if (currentRank > item.getPreviousRank()) {
            item.setTrend("down");
            item.setTrendIcon("fa-arrow-down");
            item.setTrendClass("row-down");
        } else {
            item.setTrend("same");
            item.setTrendIcon("");
            item.setTrendClass("");
        }
    }

    /**
     * 更新上次排名记录
     */
    private void updateLastRanking(List<RankingItem> currentData) {
        lastRankingMap.clear();
        for (RankingItem item : currentData) {
            lastRankingMap.put(item.getName(), item.getCurrentRank());
        }
    }

}
