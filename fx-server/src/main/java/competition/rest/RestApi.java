package competition.rest;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import competition.entity.Configs;
import competition.entity.Stages;
import competition.entity.Teams;
import competition.mapper.ConfigsMapper;
import competition.mapper.StagesMapper;
import competition.mapper.TeamsMapper;
import competition.resp.JsonResp;
import competition.util.BaseDir;
import competition.util.ConfigSpliter;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/rest")
public class RestApi {
    
    
    @Resource
    private TeamsMapper teamsMapper;
    
    @Resource
    private ConfigsMapper configsMapper;
    
    
    @GetMapping("/teams")
    public JsonResp teamList(){
        List<Teams> teams = teamsMapper.selectList(new LambdaQueryWrapper<>());
        teams.sort((o1, o2) -> Integer.parseInt(o1.getCode()) - Integer.parseInt(o2.getCode()));
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
    
}
