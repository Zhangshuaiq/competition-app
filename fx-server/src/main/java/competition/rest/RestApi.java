package competition.rest;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import competition.JavaFxApplication;
import competition.controller.ControlPane;
import competition.entity.*;
import competition.mapper.*;
import competition.resp.JsonResp;
import competition.util.*;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    
    
    
    @GetMapping("/teams")
    public JsonResp teamList(){
        // 队伍数量限制
        List<Teams> teams = teamsMapper.selectList(new LambdaQueryWrapper<>());
        teams.sort((o1, o2) -> Integer.parseInt(o1.getCode()) - Integer.parseInt(o2.getCode()));
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
    public JsonResp heartbeat(){
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
    public JsonResp holderPassword(){
        return JsonResp.success(scoreStyleMapper.selectById(2).getStyles());
    }

    
    
    @GetMapping("/answerPrefix")
    public JsonResp answerPrefix(){
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
    public JsonResp queryScore(@PathVariable Integer teamId){
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
    public JsonResp saveStatus(@RequestBody Model7Status status){
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
    public JsonResp contact(){
        Configs configs = configsMapper.selectById(9);
        if(configs == null) {
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
        ControlPane controlPane = JavaFxApplication.getApplicationContext().getBean(ControlPane.class);
        Stages currentStage = controlPane.getCurrentStage();

        boolean random = currentStage.getrRandomSort() == 1;
        List<Questions> questions = questionsMapper.selectList(
            new LambdaQueryWrapper<Questions>().eq(Questions::getStageNumber, currentStage.getSortVal())
        );

        Configs configs = configsMapper.selectById(1);
        String cfgVal = configs.getConfig();
        String txbz = "4";
        if (!StringUtils.isEmpty(cfgVal)) {
            String[] split = cfgVal.split(ConfigSpliter.SPLITER);
            txbz = split[10];
        }

        for (Questions question : questions) {
            question.setScore(currentStage.getScorePerQ());
            question.setDesValue(currentStage.getDesValue());
            question.setTxbz(txbz);
            question.setShowNumber(currentStage.getShowNumber());
        }
        Map<String, Object> res = new HashMap<>();
        res.put("random", random);
        res.put("questions", questions);
        return JsonResp.success(res);
    }
    
}
