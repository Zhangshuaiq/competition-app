package competition.rest;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import competition.mapper.TeamsMapper;
import competition.resp.JsonResp;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/rest")
public class RestApi {
    
    
    @Resource
    private TeamsMapper teamsMapper;
    
    
    @GetMapping("/teams")
    public JsonResp teamList(){
        return JsonResp.success(teamsMapper.selectList(new LambdaQueryWrapper<>()));
    }
    
    @GetMapping("/heartbeat")
    public JsonResp heartbeat(){
        return JsonResp.success();
    }
    
}
