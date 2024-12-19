package competition.ws;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import competition.JavaFxApplication;
import competition.controller.ControlPane;
import competition.controller.board.BoardPane;
import competition.controller.match.Model7;
import competition.controller.preview.*;
import competition.entity.Questions;
import competition.entity.Stages;
import competition.entity.Teams;
import competition.mapper.TeamsMapper;
import competition.util.GlobalCache;
import competition.util.TeamRuntime;
import javafx.application.Platform;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    
    @MessageMapping("/enter")
    public void enter(byte[] msg){
        String jsonText =  new String(msg, StandardCharsets.UTF_8);
        Map parse = JSON.parseObject(jsonText, Map.class);
        String team = parse.get("team").toString();
        WsTeamMemberWaiter.addWaiter(team);
    }
    
    
    // 竞技模式提交触发
    @MessageMapping("/model7/submit")
    public void submitModel7(byte[] msg){
        String jsonText = new String(msg, StandardCharsets.UTF_8);
        Map parse = JSON.parseObject(jsonText, Map.class);
        Model7RuntimeData.ModelData data = JSON.parseObject(parse.get("data").toString(), Model7RuntimeData.ModelData.class);
        String time = parse.get("time").toString();
        String teamName = data.getTeamName();
        String second =  parse.get("second").toString();
        List<Teams> teams = Model7RuntimeData.getTeams();
        

        for (int i = 0; i < teams.size(); i++) {
            if (teams.get(i).getFullName().equals(teamName)) {
                Model7RuntimeData.setRuntimeData(i, data, time, second);
            }
        }
        Platform.runLater(() -> {
            ControlPane controlPane = JavaFxApplication.getApplicationContext().getBean(ControlPane.class);
            PreviewPane previewPane = controlPane.getPreviewPane();
            if (previewPane != null) {
                previewPane.reload();
            }
        });
    }
    
    // 个人答题模式提交触发
    @MessageMapping("/model61/submit")
    public void submitModel61(byte[] msg){
        String jsonText =  new String(msg, StandardCharsets.UTF_8);
        Map parse = JSON.parseObject(jsonText, Map.class);
        Model61RuntimeData.ModelData data = JSON.parseObject(parse.get("data").toString(), Model61RuntimeData.ModelData.class);
        Model61RuntimeData.setRuntimeData(data);


        notifyPage("", null);

        Platform.runLater(() -> {
            ControlPane controlPane = JavaFxApplication.getApplicationContext().getBean(ControlPane.class);
            //controlPane.getPreviewPane().reload();
            BoardPane scoreBoardPreview = controlPane.getScoreBoardPreview();
            if (scoreBoardPreview != null) {
                scoreBoardPreview.reload();
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
            ControlPane controlPane = JavaFxApplication.getApplicationContext().getBean(ControlPane.class);
            //controlPane.getPreviewPane().reload();
            BoardPane scoreBoardPreview = controlPane.getScoreBoardPreview();
            if (scoreBoardPreview != null) {
                scoreBoardPreview.reload();
            }
        });
    }
    
    
    
    
    public void notifyPage(String route, Object data){
        ControlPane controlPane = JavaFxApplication.getApplicationContext().getBean(ControlPane.class);
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("route", route);
        dataMap.put("data", data);
        dataMap.put("teams", TeamRuntime.getTeams());
        dataMap.put("stage", controlPane.getCurrentStage());
        messagingTemplate.convertAndSend(PAGE_TOPIC, JSON.toJSONString(dataMap));
    }
    
    
//    // 答题模式-个人
//    @MessageMapping("/model61/enter")
//    public void model61Enter(byte[] msg){
//        String jsonText = new String(msg);
//        Map parse = JSON.parseObject(jsonText, Map.class);
//        String team = parse.get("team").toString();
//        WsTeamMemberWaiter.addWaiter(team);
//        List<Teams> teams = mapper.selectList(new LambdaQueryWrapper<>());
//        int size = 0;
//        for (Teams t : teams) {
//            String members = t.getMembers();
//            if (!StringUtils.isEmpty(members)) {
//                String[] split = members.split(",");
//                size += split.length;
//            }
//        }
//        if (size == WsTeamMemberWaiter.waitingMember.size()) {
//            Map<String, String> data = new HashMap<>();
//            data.put("event", "enter");
//            messagingTemplate.convertAndSend(ENTER_TOPIC, JSON.toJSONString(data));
//        }
//    }
    
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
    
//    public void sendModel60Start(){
//        Map<String, String> data = new HashMap<>();
//        messagingTemplate.convertAndSend(MODEL_60_START_TOPIC, JSON.toJSONString(data));
//    }
//    
//    public void sendModel61Start(){
//        Map<String, String> data = new HashMap<>();
//        messagingTemplate.convertAndSend(MODEL_61_START_TOPIC, JSON.toJSONString(data));
//    }
//
//    public void sendModel7Start() {
//        Map<String, String> data = new HashMap<>();
//        messagingTemplate.convertAndSend(MODEL_7_START_TOPIC, JSON.toJSONString(data));
//    }
    
    
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
    
    public void sendQuestionTime(String time) {
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("type", "time");
        dataMap.put("data", time);
        messagingTemplate.convertAndSend(QUESTION_TOPIC, JSON.toJSONString(dataMap));
    }

    public void notifyRule( String rule) {
        Map<String, String> data = new HashMap<>();
        data.put("type", "rule");
        data.put("data", rule);
        messagingTemplate.convertAndSend(QUESTION_TOPIC, JSON.toJSONString(data));
    }
    
//    public void sendQuestion(Questions questions){
//        Map<String, Object> dataMap = new HashMap<>();
//        dataMap.put("type", "Q");
//        dataMap.put("data", questions);
//        messagingTemplate.convertAndSend(QUESTION_TOPIC, JSON.toJSONString(dataMap));
//    }
    
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
}
