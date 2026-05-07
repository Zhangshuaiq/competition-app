package competition.rest;

import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import competition.JavaFxApplication;
import competition.controller.ControlPane;
import competition.controller.board.BoardPane;
import competition.controller.preview.Model7Preview;
import competition.entity.*;
import competition.mapper.*;
import competition.modal.Model7RankData;
import competition.resp.JsonResp;
import competition.resp.Model6RestData;
import competition.resp.Model7RestData;
import competition.service.RankingService;
import competition.util.*;
import competition.ws.Model60RuntimeData;
import competition.ws.Model7RuntimeData;
import competition.ws.StageSelectRuntime;
import competition.ws.WsMessageHandler;
import javafx.application.Platform;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rest")
public class RestApi {

    // 服务联系方式（微信同号）：陈先生 18008320850
    @Resource
    private TeamsMapper teamsMapper;

    @Resource
    private ConfigsMapper configsMapper;

    @Resource
    private ScoreStyleMapper scoreStyleMapper;

    @Resource
    private ScoreConfigMapper scoreConfigMapper;

    @Resource
    private ScoreConfigMapper1 scoreConfigMapper1;

    @Resource
    private Model7StatusMapper statusMapper;

    @Resource
    private QuestionsMapper questionsMapper;

    @Resource
    private RankingService rankingService;

    @Resource
    private ControlPane controlPane;

    @Resource
    private WsMessageHandler messageHandler;


    private static final String TEAM_RANK_TITLE = BaseDir.PB_RES_DIR + "团队排名标题.txt";

    private static final String MEMBER_RANK_TITLE = BaseDir.PB_RES_DIR + "选手排名标题.txt";

    @GetMapping("/teams")
    public JsonResp teamList() {
        // 队伍数量限制
        List<Teams> teams = teamsMapper.selectList(new LambdaQueryWrapper<>());
        teams.sort(Comparator.comparingInt(o -> Integer.parseInt(o.getCode())));
        List<Teams> teamWithStatus = TeamRuntime.getTeams();
        for (Teams team : teams) {
            for (Teams teams1 : teamWithStatus) {
                if (team.getCode().equals(teams1.getCode())) {
                    team.setBuzzStatus(teams1.getBuzzStatus());
                    break;
                }
            }
        }
        int maxTeamNumber = GlobalCache.MAX_TEAM_NUMBER;
        if (teams.size() <= maxTeamNumber) {
            return JsonResp.success(teams);
        }
        teams = teams.subList(0, maxTeamNumber);
        return JsonResp.success(teams);
    }

    @GetMapping("/heartbeat")
    public JsonResp heartbeat() {
        return JsonResp.success();
    }


    @GetMapping("/video/{filename}")
    public ResponseEntity<org.springframework.core.io.Resource> getVideo(@PathVariable String filename) {
        try {
            Path file = Paths.get(BaseDir.TIKU_RES_DIR).resolve(filename);
            org.springframework.core.io.Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(resource);
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(null);
        }
    }

    @GetMapping("/holderPass")
    public JsonResp holderPassword() {
        return JsonResp.success(scoreStyleMapper.selectById(2).getStyles());
    }


    @GetMapping("/answerPrefix")
    public JsonResp answerPrefix() {
        // 0x000000ff
        // %%0xffffffff
        // %%1
        // %%1
        // %%正确答案:
        // %%200%%200%%1550%%200%%2%%2%%0xffffffff%%true%%3%%1%%0xffffffff%%0x334db3ff%%200%%5%%100%%40%%0xffffffff%%0xcc3333ff%%0xcc3333ff%%100%%0x664db3ff%%0xb3b3b3ff%%0x4d804dff%%0x990000ff%%1600%%80%%20%%0xe6e6e6ff
        Configs configs = configsMapper.selectById(1);
        String config = configs.getConfig();
        String[] split = config.split(ConfigSpliter.SPLITER);
        if (split.length > 4) {
            return JsonResp.success(split[4]);
        }
        return JsonResp.success("");
    }


    @GetMapping("/image/{filename:.+}")
    public ResponseEntity<org.springframework.core.io.Resource> getImage(@PathVariable String filename) {
        try {
            Path path = Paths.get(BaseDir.TIKU_RES_DIR).resolve(filename);
            org.springframework.core.io.Resource resource = new UrlResource(path.toUri());
            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok().body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @GetMapping("/scoreConfig")
    public JsonResp scoreConfig() {
        List<ScoreConfig> scoreConfigs = scoreConfigMapper.selectList(new LambdaQueryWrapper<>());
        List<ScoreConfig1> config1List = scoreConfigMapper1.selectList(new LambdaQueryWrapper<>());
        Map<String, List> result = new HashMap<>();
        result.put("item", scoreConfigs);
        result.put("total", config1List);
        return JsonResp.success(result);
    }


    @GetMapping("/score/{teamId}")
    public JsonResp queryScore(@PathVariable Integer teamId) {
        Teams targetTeam = teamsMapper.selectById(teamId);
        List<Teams> teamWithStatus = TeamRuntime.getTeams();
        for (Teams teams1 : teamWithStatus) {
            if (targetTeam.getCode().equals(teams1.getCode())) {
                targetTeam.setBuzzStatus(teams1.getBuzzStatus());
                break;
            }
        }
        return JsonResp.success(targetTeam);
    }


    @PostMapping("/saveStatus")
    public JsonResp saveStatus(@RequestBody Model7Status status) {
        // 删除旧的
        statusMapper.delete(new LambdaQueryWrapper<Model7Status>()
                .eq(Model7Status::getTeamId, status.getTeamId())
        );
        // 加入新的
        statusMapper.insert(status);
        return JsonResp.success();
    }

    @GetMapping("/status/{teamId}")
    public JsonResp queryStatus(@PathVariable String teamId) {
        Model7Status model7Status = statusMapper.selectOne(new LambdaQueryWrapper<Model7Status>()
                .eq(Model7Status::getTeamId, teamId)
        );
        return JsonResp.success(model7Status);
    }


    @GetMapping("/contact")
    public JsonResp contact() {
        Configs configs = configsMapper.selectById(9);
        if (configs == null) {
            return JsonResp.success("");
        }
        return JsonResp.success(configs.getConfig());
    }


    // 抢答题目
    @PostMapping("/quzz")
    public JsonResp quzzQuestion(@RequestBody Map<String, String> data) {


        String team = data.get("team");
        String member = data.get("username");
        // 更新数据库的状态，记录抢答的题目和人
        ControlPane controlPane = JavaFxApplication.getApplicationContext().getBean(ControlPane.class);
        if (RuntimeUtil.buzzStart <= 0) {
            return JsonResp.success("false");
        }
        controlPane.notifyBuzz(team, member);
        return JsonResp.success("true");
    }

    // 获取当前环节的题目列表 - 竞技模式
    @GetMapping("/currentStageQuestions")
    public JsonResp getCurrentStageQuestions() {
        Model7Preview Model7Preview = JavaFxApplication.getApplicationContext().getBean(Model7Preview.class);
        return JsonResp.success(Model7Preview.getQuestionCache());
    }

    @MessageMapping("/heartbeat")
    @SendTo("/topic/heartbeat")
    public Map<String, Object> handleHeartbeat(@Payload String heartbeatData) {
        Map<String, Object> response = new HashMap<>();
        response.put("type", "pong");
        response.put("timestamp", System.currentTimeMillis());

        return response;
    }


    @GetMapping("/ranklist")
    public JsonResp getRanking() {
        try {
            List<RankingItem> rankingData = rankingService.getRankingData();
            return JsonResp.success(rankingData);
        } catch (Exception e) {
            return JsonResp.error("获取排行榜数据失败: " + e.getMessage());
        }
    }

    @GetMapping("/memberRanklist")
    public JsonResp getMemberRanking() {
        try {
            List<RankingItem> rankingData = rankingService.getMemberRankingData();
            return JsonResp.success(rankingData);
        } catch (Exception e) {
            return JsonResp.error("获取排行榜数据失败: " + e.getMessage());
        }
    }

    @GetMapping("/memberRankTitle")
    public JsonResp getMemberRankingTitle() {
        List<String> strings = FileUtil.readLines(MEMBER_RANK_TITLE, "utf-8");
        Map<String, String> titleMap = new HashMap<>();
        if (!strings.isEmpty()) {
            titleMap.put("title", strings.get(0));
        } else {
            titleMap.put("title", "排行榜");
        }
        if (strings.size() >= 2) {
            titleMap.put("col1", strings.get(1));
        } else {
            titleMap.put("col1", "名次");
        }
        if (strings.size() >= 3) {
            titleMap.put("col2", strings.get(2));
        } else {
            titleMap.put("col2", "选手");
        }
        if (strings.size() >= 4) {
            titleMap.put("col3", strings.get(3));
        } else {
            titleMap.put("col3", "团队");
        }

        if (strings.size() >= 5) {
            titleMap.put("col4", strings.get(4));
        } else {
            titleMap.put("col4", "分数");
        }
        return JsonResp.success(titleMap);
    }

    @GetMapping("/rankTitle")
    public JsonResp getRankingTitle() {
        List<String> strings = FileUtil.readLines(TEAM_RANK_TITLE, "utf-8");
        Map<String, String> titleMap = new HashMap<>();
        if (!strings.isEmpty()) {
            titleMap.put("title", strings.get(0));
        } else {
            titleMap.put("title", "排行榜");
        }
        if (strings.size() >= 2) {
            titleMap.put("col1", strings.get(1));
        } else {
            titleMap.put("col1", "名次");
        }
        if (strings.size() >= 3) {
            titleMap.put("col2", strings.get(2));
        } else {
            titleMap.put("col2", "团队");
        }
        if (strings.size() >= 4) {
            titleMap.put("col3", strings.get(3));
        } else {
            titleMap.put("col3", "分数");
        }
        return JsonResp.success(titleMap);
    }

    /**
     * 前端初始化时拉取 model6 配置
     */
    @GetMapping("/model6BoardStyle")
    public JsonResp getModel6() throws Exception {
        int CONFIG_ID = 15;
        ObjectMapper objectMapper = new ObjectMapper();

        Configs configs = configsMapper.selectById(CONFIG_ID);
        if (configs == null || configs.getConfig() == null) {
            return JsonResp.success();
        }
        TypeReference<Map<String, Object>> typeReference = new TypeReference<Map<String, Object>>() {
        };
        Map<String, Object> root = objectMapper.readValue(configs.getConfig(), typeReference);
        @SuppressWarnings("unchecked")
        Map<String, Object> model6 = (Map<String, Object>) root.get("model6");
        return JsonResp.success(model6);
    }


    /**
     * 前端初始化时拉取 model6 配置
     */
    @GetMapping("/model7BoardStyle")
    public JsonResp getModel7() throws Exception {
        int CONFIG_ID = 15;
        ObjectMapper objectMapper = new ObjectMapper();

        Configs configs = configsMapper.selectById(CONFIG_ID);
        if (configs == null || configs.getConfig() == null) {
            return JsonResp.success();
        }
        TypeReference<Map<String, Object>> typeReference = new TypeReference<Map<String, Object>>() {
        };
        Map<String, Object> root = objectMapper.readValue(configs.getConfig(), typeReference);
        @SuppressWarnings("unchecked")
        Map<String, Object> model6 = (Map<String, Object>) root.get("model7");
        return JsonResp.success(model6);
    }


    @GetMapping("/model6BoardData")
    public JsonResp getModel6Data() throws Exception {
        List<Teams> teams = TeamRuntime.getTeams();
        List<Model60RuntimeData.ModelData> runtimeData = Model60RuntimeData.getRuntimeData();

        // 计算环节总分
        List<List<Model60RuntimeData.ModelData>> runtimeData60 = Model60RuntimeData.getTotalData();

        List<Model7RankData> memeryData = new ArrayList<>();
        for (List<Model60RuntimeData.ModelData> modelDataList : runtimeData60) {
            for (Model60RuntimeData.ModelData modelData : modelDataList) {
                Model7RankData rankData = new Model7RankData();
                if (modelData.getAnswer().equals(modelData.getUserSelect())) {
                    rankData.setScore(modelData.getScore());
                } else {
                    Integer descScore = modelData.getDescScore();
                    if (descScore != null) {
                        rankData.setScore(-descScore);
                    } else {
                        rankData.setScore(-controlPane.getCurrentStage().getDesValue());
                    }
                }
                rankData.setTeam(modelData.getTeamName());
                memeryData.add(rankData);
            }
        }
        Map<String, Integer> totalScoreMap = new HashMap<>();
        Map<String, List<Model7RankData>> convertMap = memeryData.stream().collect(Collectors.groupingBy(Model7RankData::getTeam));
        for (Map.Entry<String, List<Model7RankData>> entry : convertMap.entrySet()) {
            List<Model7RankData> itemList = entry.getValue();

            int score = 0;
            for (Model7RankData data : itemList) {
                score += data.getScore();
            }
            totalScoreMap.put(entry.getKey(), score);
        }

        // 返回结果
        Map<String, Model60RuntimeData.ModelData> collect = runtimeData.stream().collect(Collectors.toMap(Model60RuntimeData.ModelData::getTeamName, data -> data));
        List<Model6RestData> responseData = new ArrayList<>();
        // 返回所有队伍，如果队伍已回答，那么找出 runtimeData 中对应的数据
        for (Teams team : teams) {
            Model6RestData restData = new Model6RestData();
            restData.setId(team.getId().toString());
            restData.setFullName(team.getFullName());

            // 找到 runtimeData 中对应的数据
            Model60RuntimeData.ModelData scoreVal = collect.get(team.getFullName());
            restData.setAnswer(scoreVal == null ? null : scoreVal.getUserSelect());
            restData.setBuzzStatus(scoreVal == null ? 0 : StringUtils.hasText(scoreVal.getUserSelect()) ? 1 : 0);
            restData.setTeamScore(totalScoreMap.getOrDefault(team.getFullName(), 0));
            responseData.add(restData);
        }
        return JsonResp.success(responseData);
    }


    @GetMapping("/model7BoardData")
    public JsonResp getModel7Data() throws Exception {
        // 所有题目
        Model7Preview Model7Preview = JavaFxApplication.getApplicationContext().getBean(Model7Preview.class);
        List<Questions> totalQuestions = Model7Preview.getQuestionsWithCache();
        // 补充未答题的分数扣除
        Map<Integer, String> desMap = totalQuestions.stream().collect(Collectors.toMap(Questions::getQuestionNumber,
                q -> q.getDescScore() != null ? q.getDescScore() : ""));
        List<Teams> teams = TeamRuntime.getTeams();
        List<List<Model7RuntimeData.ModelData>> runtimeData = Model7RuntimeData.getRuntimeData();

        // 答对的题目数量
        Map<String, Integer> correcetMap = new HashMap<>();
        // 答题获得的分数
        Map<String, Integer> scoreMap = new HashMap<>();

        // 已答数量
        Map<String, Integer> answerMap = new HashMap<>();

        boolean isFinish = true;
        List<Boolean> finished = Model7RuntimeData.getFinished();
        for (Boolean b : finished) {
            isFinish = isFinish & b;
        }

        for (int i = 0; i < runtimeData.size(); i++) {

            List<Model7RuntimeData.ModelData> modelData = runtimeData.get(i);

            int score = 0;
            int correct = 0;
            if (isFinish) {
                for (Model7RuntimeData.ModelData data : modelData) {
                    if (data.getUserSelect() != null && data.getUserSelect().equals(data.getAnswer())) {
                        score += data.getScore();
                        correct++;
                    } else {
                        score -= data.getDesValue();
                    }
                }
                List<Integer> answeredQuestion = modelData.stream()
                        .map(Model7RuntimeData.ModelData::getQuestionNumber).collect(Collectors.toList());
                Integer unAnsweredValue = 0;
                for (Map.Entry<Integer, String> entrySet : desMap.entrySet()) {
                    int num = entrySet.getKey();
                    String val = entrySet.getValue();
                    if (!answeredQuestion.contains(num) && NumberUtil.isInt(val)) {
                        unAnsweredValue += Integer.parseInt(val);
                    } else if (!answeredQuestion.contains(num)) {
                        unAnsweredValue += controlPane.getCurrentStage().getDesValue();
                    }
                }
                score = score - unAnsweredValue;
            } else {
                for (Model7RuntimeData.ModelData data : modelData) {
                    if (data.getUserSelect() != null && data.getUserSelect().equals(data.getAnswer())) {
                        score += data.getScore();
                        correct++;
                    } else {
                        score -= data.getDesValue();
                    }
                }
            }

            String team = Model7RuntimeData.getTeams().get(i).getFullName();

            scoreMap.put(team, score);
            correcetMap.put(team, correct);
            answerMap.put(team, modelData.size());
        }


        List<Model7RestData> responseData = new ArrayList<>();
        for (Teams team : teams) {
            Model7RestData restData = new Model7RestData();
            restData.setId(team.getId().toString());
            restData.setFullName(team.getFullName());
            restData.setTeamScore(scoreMap.getOrDefault(team.getFullName(), 0));
            restData.setAnsweredCount(answerMap.getOrDefault(team.getFullName(), 0));
            restData.setCorrectRatio((int)(100.0 * correcetMap.getOrDefault(team.getFullName(), 0) / totalQuestions.size()));
            responseData.add(restData);
        }
        return JsonResp.success(responseData);
    }


    /**
     * 接收前端自选环节提交
     * 参数: { stage: 环节名, team: 队伍, member: 选手 }
     */
    @PostMapping("/selectStage")
    public JsonResp selectStage(@RequestBody StageSelectRuntime.StageSelectData data) {
        if (!StringUtils.hasText(data.getStage())
            || !StringUtils.hasText(data.getTeam())
            || !StringUtils.hasText(data.getMember())) {
            return JsonResp.error("参数不完整");
        }
        StageSelectRuntime.save(data);
        // 将提交队伍的状态置为 1（已选择，但不揭示实际内容）
        for (Teams team : TeamRuntime.getTeams()) {
            if (team.getFullName().equals(data.getTeam())) {
                team.setStatus(1);
                break;
            }
        }
        messageHandler.notifyPage("", null);
        Platform.runLater(
                () -> {
                    BoardPane scoreBoard = controlPane.getScoreBoardPreview();
                    scoreBoard.reload();
                }
        );
        return JsonResp.success();
    }
}
