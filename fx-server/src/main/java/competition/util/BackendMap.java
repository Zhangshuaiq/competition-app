package competition.util;

import java.util.HashMap;
import java.util.Map;

public class BackendMap {
    
    private static final Map<String, String> stageMap = new HashMap<>();
    
    static {
        stageMap.put("环节设置", "/view/backend/StageConfig.fxml");
        stageMap.put("界面设置", "/view/backend/PageConfig.fxml");
        stageMap.put("团队设置", "/view/backend/TeamConfig.fxml");
        stageMap.put("抢答设置", "/view/backend/BuzzConfig.fxml");
        stageMap.put("计分设置", "/view/backend/ScoreConfig.fxml");
    }
    
    public static final String getBackendPath(String backendCfgName) {
        return stageMap.get(backendCfgName);
    }
}
