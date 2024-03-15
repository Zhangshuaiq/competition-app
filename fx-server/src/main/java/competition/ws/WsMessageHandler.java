package competition.ws;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import competition.controller.match.Model60;
import competition.controller.match.Model61;
import competition.controller.match.Model7;
import competition.entity.Questions;
import competition.entity.Teams;
import competition.mapper.TeamsMapper;
import javafx.application.Platform;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class WsMessageHandler {
    
    // 发起连接，进入竞技模式答题主题
    public static final String ENTER_TOPIC = "/topic/enter";
    
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
    
    
    @Resource
    private TeamsMapper mapper;
    
    @Resource
    private Model7 model7;

    @Resource
    private Model60 model60;


    @Resource
    private Model61 model61;
    
    
    @Resource
    private SimpMessagingTemplate messagingTemplate;
    
    @MessageMapping("/enter")
    public void enter(byte[] msg){
        String jsonText = new String(msg);
        Map parse = JSON.parseObject(jsonText, Map.class);
        String team = parse.get("team").toString();
        WsTeamMemberWaiter.addWaiter(team);
    }
    
    
    // 竞技模式提交触发
    @MessageMapping("/model7/submit")
    public void submitModel7(byte[] msg){
        String jsonText = new String(msg);
        Map parse = JSON.parseObject(jsonText, Map.class);
        Model7RuntimeData.ModelData data = JSON.parseObject(parse.get("data").toString(), Model7RuntimeData.ModelData.class);
        String second = parse.get("time").toString();
        String teamName = data.getTeamName();
        List<Teams> teams = Model7RuntimeData.getTeams();
        

        for (int i = 0; i < teams.size(); i++) {
            if (teams.get(i).getFullName().equals(teamName)) {
                Model7RuntimeData.setRuntimeData(i, data, second);
            }
        }
        Platform.runLater(() -> {
            //model7.reload(second);
        });
    }
    
    // 个人答题模式提交触发
    @MessageMapping("/model61/submit")
    public void submitModel61(byte[] msg){
        String jsonText = new String(msg);
        Map parse = JSON.parseObject(jsonText, Map.class);
        Model61RuntimeData.ModelData data = JSON.parseObject(parse.get("data").toString(), Model61RuntimeData.ModelData.class);
        Model61RuntimeData.setRuntimeData(data);

        Platform.runLater(() -> {
            model61.reload();
        });
    }


    @MessageMapping("/model60/submit")
    public void submitModel60(byte[] msg){
        String jsonText = new String(msg);
        Map parse = JSON.parseObject(jsonText, Map.class);
        Model60RuntimeData.ModelData data = JSON.parseObject(parse.get("data").toString(), Model60RuntimeData.ModelData.class);
        Model60RuntimeData.setRuntimeData(data);
        Platform.runLater(() -> {
            model60.reload();
        });
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

    // 竞技模式时间同步
    public void sendModel7Time(String time){
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("type", "time");
        dataMap.put("data", time);
        messagingTemplate.convertAndSend(QUESTION_GROUP_TOPIC, JSON.toJSONString(dataMap));
    }
    
    public void sendModel60Start(){
        Map<String, String> data = new HashMap<>();
        messagingTemplate.convertAndSend(MODEL_60_START_TOPIC, JSON.toJSONString(data));
    }
    
    public void sendModel61Start(){
        Map<String, String> data = new HashMap<>();
        messagingTemplate.convertAndSend(MODEL_61_START_TOPIC, JSON.toJSONString(data));
    }

    public void sendModel7Start() {
        Map<String, String> data = new HashMap<>();
        messagingTemplate.convertAndSend(MODEL_7_START_TOPIC, JSON.toJSONString(data));
    }
    
    
    public void sendQuestionTime(String time) {
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("type", "time");
        dataMap.put("data", time);
        messagingTemplate.convertAndSend(QUESTION_TOPIC, JSON.toJSONString(dataMap));
    }
    
    public void sendQuestion(Questions questions){
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("type", "Q");
        dataMap.put("data", questions);
        messagingTemplate.convertAndSend(QUESTION_TOPIC, JSON.toJSONString(dataMap));
    }
    
    public void sendQuestionGroup(List<Questions> questionsList) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("type", "G");
        dataMap.put("data", questionsList);
        messagingTemplate.convertAndSend(QUESTION_GROUP_TOPIC, JSON.toJSONString(dataMap));
    }
    
    
    public void notifyEnterWait(String type, String rule){
        Map<String, String> data = new HashMap<>();
        data.put("event", "enter");
        data.put("type", type);
        data.put("rule", rule);
        messagingTemplate.convertAndSend(ENTER_TOPIC, JSON.toJSONString(data));
    }
    
    public void notifyExit(){
        Map<String, String> data = new HashMap<>();
        data.put("event", "wait");
        messagingTemplate.convertAndSend(ENTER_WAIT_TOPIC, JSON.toJSONString(data));
    }
}
