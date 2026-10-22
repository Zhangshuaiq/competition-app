package competition.service;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import competition.entity.Configs;
import competition.mapper.ConfigsMapper;
import lombok.Data;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Web 团队排行榜独立配置，不影响桌面排行榜。 */
@Service
public class TeamRankStyleService {
    private static final String TYPE = "webTeamRankStyle";
    @Resource private ConfigsMapper configsMapper;

    @Data
    public static class Style {
        private int titleFontSize = 42;
        private int headerFontSize = 22;
        private int rowFontSize = 22;
        private boolean showContainerBackground = false;
        private String containerBackground = "#ffffff";
        private boolean autoCenter = true;
        private int contentX = 0;
        private int contentY = 0;
        private boolean showRowBackground = true;
        private String headerBackground = "#ffffff";
        private String headerColor = "#000000";
        private String rowBackground = "#ffffff";
        private List<String> rowColors = new ArrayList<>(Arrays.asList("#ff0000", "#0000ff", "#ffff00"));
    }

    private Configs find() {
        return configsMapper.selectOne(new LambdaQueryWrapper<Configs>().eq(Configs::getType, TYPE));
    }

    public Style load() {
        Configs config = find();
        if (config == null || config.getConfig() == null) return new Style();
        Style style = JSON.parseObject(config.getConfig(), Style.class);
        return normalize(style == null ? new Style() : style);
    }

    private Style normalize(Style style) {
        List<String> defaults = Arrays.asList("#ff0000", "#0000ff", "#ffff00");
        List<String> colors = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            colors.add(style.getRowColors() != null && i < style.getRowColors().size()
                    && style.getRowColors().get(i) != null ? style.getRowColors().get(i) : defaults.get(i));
        }
        style.setRowColors(colors);
        return style;
    }

    public synchronized void save(Style style) {
        normalize(style);
        Configs config = find();
        boolean insert = config == null;
        if (insert) {
            config = new Configs();
            config.setType(TYPE);
        }
        config.setConfig(JSON.toJSONString(style));
        if (insert) configsMapper.insert(config);
        else configsMapper.updateById(config);
    }
}
