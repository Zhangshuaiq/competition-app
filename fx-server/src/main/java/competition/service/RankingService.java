package competition.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import competition.entity.Teams;
import competition.rest.RankingItem;
import competition.util.TeamRuntime;
import competition.util.TeamMemberCodec;
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

    private Map<String, Integer> lastMemberRankingMap = new HashMap<>();


    public List<RankingItem> getMemberRankingData() {
        List<List<RankingItem>> memberData =  TeamRuntime.getTeams().stream()
                .map(team -> {
                    String members = team.getMembers();
                    String [] memberArr = TeamMemberCodec.split(members);
                    JSONObject jsonObject = JSON.parseObject(team.getMemberScore());
                    List<RankingItem> items = new ArrayList<>();
                    for (String s : memberArr) {
                        RankingItem item = new RankingItem();
                        item.setName(s);
                        item.setScore(Integer.parseInt(jsonObject.getOrDefault(s, "0").toString()));
                        item.setTeam(team.getFullName());
                        item.setPreviousRank(lastMemberRankingMap.getOrDefault(s + "-" + team.getFullName(), 0));
                        items.add(item);
                    }
                    return items;
                })
                .collect(Collectors.toList());

        List<RankingItem> currentData = memberData.stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());

        // 按分数降序排序
        currentData.sort((a, b) -> b.getScore().compareTo(a.getScore()));

        // 设置当前排名（处理相同分数的情况）
        setRankingsWithTies(currentData);

        // 设置趋势
        for (RankingItem item : currentData) {
            calculateTrend(item, item.getCurrentRank());
        }

        // 更新上次排名记录
        updateLastMemberRanking(currentData);
        return currentData;
    }

    /**
     * 获取排行榜数据
     */
    public List<RankingItem> getRankingData() {
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

        // 设置当前排名（处理相同分数的情况）
        setRankingsWithTies(currentData);

        // 设置趋势
        for (RankingItem item : currentData) {
            calculateTrend(item, item.getCurrentRank());
        }

        // 更新上次排名记录
        updateLastRanking(currentData);

        return currentData;
    }

    /**
     * 设置排名，处理相同分数并列的情况
     * 例如: 分数 [100, 100, 80] -> 排名 [1, 1, 2]
     */
    private void setRankingsWithTies(List<RankingItem> data) {
        if (data.isEmpty()) {
            return;
        }

        int currentRank = 1;
        Integer previousScore = null;

        for (int i = 0; i < data.size(); i++) {
            RankingItem item = data.get(i);
            Integer currentScore = item.getScore();

            // 如果是第一个元素，或者分数与前一个不同，则更新排名为下一个顺序排名
            if (i == 0 || !currentScore.equals(previousScore)) {
                currentRank = getCurrentSequentialRank(data, i);
            }
            // 如果分数相同，则使用相同的排名

            item.setCurrentRank(currentRank);
            previousScore = currentScore;
        }
    }

    /**
     * 获取当前位置的顺序排名
     * 考虑前面已经设置过排名的情况，确保排名连续不跳跃
     */
    private int getCurrentSequentialRank(List<RankingItem> data, int currentIndex) {
        if (currentIndex == 0) {
            return 1;
        }

        // 找到前一个不同分数的排名，然后加1
        int previousRank = data.get(currentIndex - 1).getCurrentRank();
        return previousRank + 1;
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

    private void updateLastMemberRanking(List<RankingItem> currentData) {
        lastMemberRankingMap.clear();
        for (RankingItem item : currentData) {
            lastMemberRankingMap.put(item.getName() + "-" + item.getTeam(), item.getCurrentRank());
        }
    }
}
