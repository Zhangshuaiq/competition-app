package competition.util;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import competition.entity.Teams;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

// 分数保存
public class ScoreSaver {
    
    public static void saveScore(){
        List<Teams> teams = TeamRuntime.getTeams();
        String scoreTexts = JSON.toJSONString(teams);
        FileUtil.writeBytes(scoreTexts.getBytes(StandardCharsets.UTF_8), TextResource.FS);
    }
    
    public static  List<Teams> loadScore(){
        List<Teams> teams = new ArrayList<>();
        byte[] bytes = FileUtil.readBytes(TextResource.FS);
        String json = new String(bytes);
        if (StringUtils.isEmpty(json)) {
            return teams;
        }
        JSONArray objects = JSON.parseArray(json);
        for (Object object : objects) {
            teams.add(JSON.parseObject(JSON.toJSONString(object), Teams.class));
        }
        return teams;
    }
}
