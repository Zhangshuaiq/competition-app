package competition.rest;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import competition.mapper.TeamsMapper;
import competition.resp.JsonResp;
import competition.util.BaseDir;
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
