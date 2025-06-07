package competition.ws;

import com.alibaba.fastjson2.JSON;
import competition.JavaFxApplication;
import competition.controller.ControlPane;
import competition.controller.board.BoardPane;
import competition.controller.preview.*;
import competition.entity.Questions;
import competition.entity.Teams;
import competition.mapper.TeamsMapper;
import competition.util.GlobalCache;
import competition.util.RuntimeUtil;
import competition.util.TeamRuntime;
import javafx.application.Platform;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Controller
public class WsMessageHandler {

    // 发起连接，进入竞技模式答题主题
    public static final String ENTER_TOPIC = "/topic/enter";

    // 显示规则的主题
    public static final String RULE_TOPIC = "/topic/rule";

    // 发起连接，进入答题模式-个人答题的主题
    public static final String MODEL_61_ENTER_TOPIC = "/topic/model61/enter";

    // 发起连接，进入答题模式-团队答题的主题
    public static final String MODEL_60_ENTER_TOPIC = "/topic/model60/enter";

    // 单个题目主题
    public static final String QUESTION_TOPIC = "/topic/question";

    // 题组主题
    public static final String QUESTION_GROUP_TOPIC = "/topic/question/group";

    // model 7 开始答题  竞技模式
    public static final String MODEL_7_START_TOPIC = "/topic/model7/start";

    // model 61 开始答题 答题模式-个人
    public static final String MODEL_61_START_TOPIC = "/topic/model61/start";

    // model 60 开始答题 答题模式-团队
    public static final String MODEL_60_START_TOPIC = "/topic/model60/start";

    // 返回等待答题页面
    public static final String ENTER_WAIT_TOPIC = "/topic/wait";

    // 界面跳转主题
    public static final String PAGE_TOPIC = "/topic/page";

    @Resource
    private TeamsMapper mapper;

    @Resource
    private SimpMessagingTemplate messagingTemplate;

    // 优化相关的字段
    private final Object dataLock = new Object(); // 数据同步锁
    private final Map<String, Integer> teamIndexCache = new ConcurrentHashMap<>(); // 团队索引缓存
    private final AtomicLong lastUpdateTime = new AtomicLong(0); // 上次更新时间
    private volatile ControlPane cachedControlPane; // 缓存ControlPane
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1); // 定时调度器

    @PostConstruct
    private void init() {
        // 注册UI更新回调
        Model7RuntimeData.setUIUpdateCallback(new Model7RuntimeData.UIUpdateCallback() {
            @Override
            public void onDataUpdated(String teamName, int teamIndex) {
                // 使用现有的防抖机制
                scheduleUIUpdate();
            }
        });
    }

    @MessageMapping("/enter")
    public void enter(byte[] msg, @Header("simpSessionId") String sessionId){
        String jsonText = new String(msg, StandardCharsets.UTF_8);
        Map parse = JSON.parseObject(jsonText, Map.class);
        String team = parse.get("team").toString();
        System.out.println("团队" + team + "加入,标识为：" + sessionId);
        WsTeamMemberWaiter.addConnectedTeam(sessionId, team);
    }

    /**
     * 优化后的竞技模式提交处理
     */
    @MessageMapping("/model7/submit")
    public void submitModel7(byte[] msg){
        try {
            // 1. 快速解析数据
            String jsonText = new String(msg, StandardCharsets.UTF_8);
            Map parse = JSON.parseObject(jsonText, Map.class);
            Model7RuntimeData.ModelData data = JSON.parseObject(parse.get("data").toString(), Model7RuntimeData.ModelData.class);
            String time = parse.get("time").toString();
            String teamName = data.getTeamName();
            String second = parse.get("second").toString();

            // 2. 线程安全的数据更新 - 现在这里会通过回调触发防抖UI更新
            updateTeamDataSafely(teamName, data, time, second);

        } catch (Exception e) {
            System.err.println("处理Model7提交时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 线程安全的团队数据更新
     */
    private boolean updateTeamDataSafely(String teamName, Model7RuntimeData.ModelData data, String time, String second) {
        synchronized (dataLock) {
            try {
                Integer teamIndex = getTeamIndexFromCache(teamName);
                if (teamIndex != null) {
                    // 这里会触发回调，进而触发防抖UI更新
                    Model7RuntimeData.setRuntimeData(teamIndex, data, time, second);
                    return true;
                }
                return false;
            } catch (Exception e) {
                System.err.println("更新团队数据失败: " + e.getMessage());
                return false;
            }
        }
    }

    /**
     * 获取团队索引（带缓存）
     */
    private Integer getTeamIndexFromCache(String teamName) {
        return teamIndexCache.computeIfAbsent(teamName, name -> {
            List<Teams> teams = Model7RuntimeData.getTeams();
            for (int i = 0; i < teams.size(); i++) {
                if (teams.get(i).getFullName().equals(name)) {
                    return i;
                }
            }
            return null;
        });
    }

    /**
     * 防抖的UI更新调度
     */
    private void scheduleUIUpdate() {
        long currentTime = System.currentTimeMillis();
        lastUpdateTime.set(currentTime);

        // 延迟100ms更新，如果在此期间有新的更新请求，则重新计时
        scheduler.schedule(() -> {
            // 检查是否有更新的请求
            if (System.currentTimeMillis() - lastUpdateTime.get() >= 100) {
                performUIUpdate();
            }
        }, 100, TimeUnit.MILLISECONDS);
    }

    /**
     * 执行UI更新 - 同时更新分数显示
     */
    private void performUIUpdate() {
        Platform.runLater(() -> {
            try {
                ControlPane controlPane = getCachedControlPane();
                if (controlPane != null) {
                    // 更新团队分数显示
                    updateTeamScoreDisplay(controlPane);

                    // 更新预览面板
                    PreviewPane previewPane = controlPane.getPreviewPane();
                    if (previewPane != null) {
                        previewPane.reload();
                    }
                }
            } catch (Exception e) {
                System.err.println("UI更新失败: " + e.getMessage());
            }
        });
    }

    /**
     * 更新团队分数显示
     */
    private void updateTeamScoreDisplay(ControlPane controlPane) {
        try {
            // 获取当前选中团队的分数并更新显示
            List<Teams> teams = TeamRuntime.getTeams();
            if (!teams.isEmpty() && controlPane.teamScore != null) {
                // 假设显示第一个团队的分数，或根据实际逻辑选择
                Teams currentTeam = teams.get(0); // 根据实际需求修改
                controlPane.teamScore.setText(currentTeam.getTeamScore().toString());
            }
        } catch (Exception e) {
            System.err.println("更新团队分数显示失败: " + e.getMessage());
        }
    }

    /**
     * 获取缓存的ControlPane
     */
    private ControlPane getCachedControlPane() {
        if (cachedControlPane == null) {
            synchronized (this) {
                if (cachedControlPane == null) {
                    cachedControlPane = JavaFxApplication.getApplicationContext().getBean(ControlPane.class);
                }
            }
        }
        return cachedControlPane;
    }

    // 个人答题模式提交触发
    @MessageMapping("/model61/submit")
    public void submitModel61(byte[] msg){
        String jsonText = new String(msg, StandardCharsets.UTF_8);
        Map parse = JSON.parseObject(jsonText, Map.class);
        Model61RuntimeData.ModelData data = JSON.parseObject(parse.get("data").toString(), Model61RuntimeData.ModelData.class);
        Model61RuntimeData.setRuntimeData(data);

        notifyPage("", null);

        Platform.runLater(() -> {
            ControlPane controlPane = getCachedControlPane();
            if (controlPane != null) {
                BoardPane scoreBoardPreview = controlPane.getScoreBoardPreview();
                if (scoreBoardPreview != null) {
                    scoreBoardPreview.reload();
                }
            }
        });
    }

    @MessageMapping("/model60/submit")
    public void submitModel60(byte[] msg){
        String jsonText = new String(msg, StandardCharsets.UTF_8);
        Map parse = JSON.parseObject(jsonText, Map.class);
        Model60RuntimeData.ModelData data = JSON.parseObject(parse.get("data").toString(), Model60RuntimeData.ModelData.class);
        Model60RuntimeData.setRuntimeData(data);
        notifyPage("", null);
        Platform.runLater(() -> {
            ControlPane controlPane = getCachedControlPane();
            if (controlPane != null) {
                BoardPane scoreBoardPreview = controlPane.getScoreBoardPreview();
                if (scoreBoardPreview != null) {
                    scoreBoardPreview.reload();
                }
            }
        });
    }

    public void notifyPage(String route, Object data){
        ControlPane controlPane = getCachedControlPane();
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("route", route);
        dataMap.put("data", data);
        dataMap.put("buzzStatus", RuntimeUtil.showBuzzBar);
        dataMap.put("teams", TeamRuntime.getTeams());
        dataMap.put("stage", controlPane != null ? controlPane.getCurrentStage() : null);
        messagingTemplate.convertAndSend(PAGE_TOPIC, JSON.toJSONString(dataMap));
    }

    // 强制结束
    public void model7Finish(){
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("type", "finish");
        messagingTemplate.convertAndSend(QUESTION_GROUP_TOPIC, JSON.toJSONString(dataMap));
    }

    // 竞技模式时间同步
    public void sendModel7Time(String time){
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("type", "time");
        dataMap.put("data", time);
        messagingTemplate.convertAndSend(QUESTION_GROUP_TOPIC, JSON.toJSONString(dataMap));
    }

    public void sendShowAnswer() {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("type", "answer");
        dataMap.put("data", GlobalCache.showAnswer);
        messagingTemplate.convertAndSend(QUESTION_TOPIC, JSON.toJSONString(dataMap));
    }

    public void sendClue(Object data) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("type", "clue");
        dataMap.put("data", data);
        messagingTemplate.convertAndSend(QUESTION_TOPIC, JSON.toJSONString(dataMap));
    }

    public void sendQuestionTime(String time, String isBuzz) {
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("type", "time");
        dataMap.put("data", time);
        dataMap.put("isBuzz", isBuzz);
        messagingTemplate.convertAndSend(QUESTION_TOPIC, JSON.toJSONString(dataMap));
    }

    public void notifyRule(String rule) {
        Map<String, String> data = new HashMap<>();
        data.put("type", "rule");
        data.put("data", rule);
        messagingTemplate.convertAndSend(QUESTION_TOPIC, JSON.toJSONString(data));
    }

    public void sendQuestionGroup(List<Questions> questionsList, boolean random) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("type", "G");
        dataMap.put("data", questionsList);
        dataMap.put("random", random);
        messagingTemplate.convertAndSend(QUESTION_GROUP_TOPIC, JSON.toJSONString(dataMap));
    }

    public void notifyEnterWait(String type){
        Map<String, String> data = new HashMap<>();
        data.put("event", "enter");
        data.put("type", type);
        messagingTemplate.convertAndSend(ENTER_TOPIC, JSON.toJSONString(data));
    }

    public void notifyExit(){
        Map<String, String> data = new HashMap<>();
        data.put("event", "wait");
        messagingTemplate.convertAndSend(ENTER_WAIT_TOPIC, JSON.toJSONString(data));
    }

    /**
     * 清理缓存的方法
     */
    public void clearCache() {
        teamIndexCache.clear();
        synchronized (this) {
            cachedControlPane = null;
        }
    }

    /**
     * Spring容器销毁时的清理工作
     */
    @PreDestroy
    public void cleanup() {
        clearCache();
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(1, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}