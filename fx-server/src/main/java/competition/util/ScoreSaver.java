package competition.util;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson2.JSON;
import competition.entity.Teams;

import java.nio.charset.StandardCharsets;
import java.util.List;

// 分数保存
public class ScoreSaver {
    
    public static void saveScore(){
        List<Teams> teams = TeamRuntime.getTeams();
        String scoreTexts = JSON.toJSONString(teams);
        FileUtil.writeBytes(scoreTexts.getBytes(StandardCharsets.UTF_8), TextResource.FS);
    }
}
