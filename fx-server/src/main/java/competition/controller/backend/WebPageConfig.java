package competition.controller.backend;

import javax.annotation.Resource;

import competition.util.IPUtil;
import javafx.beans.property.SimpleStringProperty;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import competition.entity.Configs;
import competition.mapper.ConfigsMapper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import java.util.HashMap;
import java.util.Map;

@Component
public class WebPageConfig {

    @FXML private Button model6PageWebSiteCopyBtn;
    @FXML private Button model7PageWebSiteCopyBtn;
    @FXML private Button MemberScorePageWebSiteCopyBtn;
    @FXML private Button TeamScorePageWebSiteCopyBtn;
    @FXML private Button saveBtn;

    // ===== Model6 TextField =====
    @FXML private TextField model6PageWebSite;
    @FXML private TextField model6PageRow;
    @FXML private TextField model6PageCol;
    @FXML private TextField model6PageX;
    @FXML private TextField model6PageY;
    @FXML private TextField model6PageCspace;
    @FXML private TextField model6PageRspace;
    @FXML private TextField model6PageBorderW;
    @FXML private TextField model6PageBorderL;
    @FXML private TextField model6PageTeamFontsize;
    @FXML private TextField model6PageTeamFontWidth;
    @FXML private TextField model6PageAnswerFontsize;
    @FXML private TextField model6PageAnswerFontWidth;

    // ===== Model6 ColorPicker =====
    @FXML private ColorPicker model6PageBackColor;
    @FXML private ColorPicker model6PageTeamColor;
    @FXML private ColorPicker model6PageAnswerColor;
    @FXML private ColorPicker model6PageAnsweredColor;
    @FXML private ColorPicker model6PageRightColor;
    @FXML private ColorPicker model6PageErrorColor;
    @FXML private ColorPicker model6PageWaitColor;

    // ===== Model6 CheckBox =====
    @FXML private CheckBox model6ScoreShow;

    // ===== Model7 TextField =====
    @FXML private TextField model7PageWebSite;
    @FXML private TextField model7PageRow;
    @FXML private TextField model7PageCol;
    @FXML private TextField model7PageX;
    @FXML private TextField model7PageY;
    @FXML private TextField model7PageCspace;
    @FXML private TextField model7PageRspace;
    @FXML private TextField model7PageBorderW;
    @FXML private TextField model7PageBorderL;
    @FXML private TextField model7PageTeamFontsize;
    @FXML private TextField model7PageTeamFontWidth;
    @FXML private TextField model7PageScoreFontsize;
    @FXML private TextField model7PageScoreFontWidth;
    @FXML private TextField model7PageAnsweredFontsize;
    @FXML private TextField model7PageCorrectRatioFontsize;
    @FXML private TextField model7PageCorrectRatioFontWidth;

    // ===== Model7 ColorPicker =====
    @FXML private ColorPicker model7PageBackColor;
    @FXML private ColorPicker model7PageTeamColor;
    @FXML private ColorPicker model7PageScoreColor;
    @FXML private ColorPicker model7PageScoreLargeColor;
    @FXML private ColorPicker model7PageAnsweredColor;
    @FXML private ColorPicker model7PageCorrectRatioColor;

    // ===== Model7 CheckBox =====
    @FXML private CheckBox model7PageAnsweredShow;
    @FXML private CheckBox model7PageCorrectRatioShow;

    // ===== MemberScore / TeamScore =====
    @FXML private TextField MemberScorePageWebSite;
    @FXML private TextField TeamScorePageWebSite;

    @Resource
    private ConfigsMapper configsMapper;

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final int CONFIG_ID = 15;

    private SimpleStringProperty ipaddr = new SimpleStringProperty("");

    @FXML
    private void initialize() {
        ipaddr.set(IPUtil.getLocalIpAddress());
        setAddress();
        this.loadData();

        saveBtn.setOnMouseClicked(event -> saveConfig());

        // 复制按钮绑定
        model6PageWebSiteCopyBtn.setOnMouseClicked(e -> copyToClipboard(model6PageWebSite.getText()));
        model7PageWebSiteCopyBtn.setOnMouseClicked(e -> copyToClipboard(model7PageWebSite.getText()));
        MemberScorePageWebSiteCopyBtn.setOnMouseClicked(e -> copyToClipboard(MemberScorePageWebSite.getText()));
        TeamScorePageWebSiteCopyBtn.setOnMouseClicked(e -> copyToClipboard(TeamScorePageWebSite.getText()));
    }

    private void setAddress(){
        model6PageWebSite.setText("http://" + ipaddr.get() + ":8080/index.html#/model6Board");
        model7PageWebSite.setText("http://" + ipaddr.get() + ":8080/index.html#/model67oard");
    }

    // ==================== 加载数据到控件 ====================

    @SuppressWarnings("unchecked")
    private void loadData() {
        try {
            Configs configs = configsMapper.selectById(CONFIG_ID);
            if (configs == null || configs.getConfig() == null || configs.getConfig().isEmpty()) {
                return;
            }

            Map<String, Object> root = objectMapper.readValue(configs.getConfig(), Map.class);

            // ----- Model6 -----
            Map<String, Object> m6 = (Map<String, Object>) root.get("model6");
            if (m6 != null) {
//                setText(model6PageWebSite,         m6, "webSite");
                setText(model6PageRow,             m6, "row");
                setText(model6PageCol,             m6, "col");
                setText(model6PageX,               m6, "x");
                setText(model6PageY,               m6, "y");
                setText(model6PageCspace,          m6, "cspace");
                setText(model6PageRspace,          m6, "rspace");
                setText(model6PageBorderW,         m6, "borderW");
                setText(model6PageBorderL,         m6, "borderL");
                setText(model6PageTeamFontsize,    m6, "teamFontsize");
                setText(model6PageTeamFontWidth,   m6, "teamFontWidth");
                setText(model6PageAnswerFontsize,  m6, "answerFontsize");
                setText(model6PageAnswerFontWidth, m6, "answerFontWidth");
                setColor(model6PageBackColor,      m6, "backColor");
                setColor(model6PageTeamColor,      m6, "teamColor");
                setColor(model6PageAnswerColor,    m6, "answerColor");
                setColor(model6PageAnsweredColor,  m6, "answeredColor");
                setColor(model6PageRightColor,     m6, "rightColor");
                setColor(model6PageErrorColor,     m6, "errorColor");
                setColor(model6PageWaitColor,      m6, "waitColor");
                setCheck(model6ScoreShow,          m6, "scoreShow");
            }

            // ----- Model7 -----
            Map<String, Object> m7 = (Map<String, Object>) root.get("model7");
            if (m7 != null) {
//                setText(model7PageWebSite,                 m7, "webSite");
                setText(model7PageRow,                     m7, "row");
                setText(model7PageCol,                     m7, "col");
                setText(model7PageX,                       m7, "x");
                setText(model7PageY,                       m7, "y");
                setText(model7PageCspace,                  m7, "cspace");
                setText(model7PageRspace,                  m7, "rspace");
                setText(model7PageBorderW,                 m7, "borderW");
                setText(model7PageBorderL,                 m7, "borderL");
                setText(model7PageTeamFontsize,            m7, "teamFontsize");
                setText(model7PageTeamFontWidth,           m7, "teamFontWidth");
                setText(model7PageScoreFontsize,           m7, "scoreFontsize");
                setText(model7PageScoreFontWidth,          m7, "scoreFontWidth");
                setText(model7PageAnsweredFontsize,        m7, "answeredFontsize");
                setText(model7PageCorrectRatioFontsize,    m7, "correctRatioFontsize");
                setText(model7PageCorrectRatioFontWidth,   m7, "correctRatioFontWidth");
                setColor(model7PageBackColor,              m7, "backColor");
                setColor(model7PageTeamColor,              m7, "teamColor");
                setColor(model7PageScoreColor,             m7, "scoreColor");
                setColor(model7PageScoreLargeColor,        m7, "scoreLargeColor");
                setColor(model7PageAnsweredColor,          m7, "answeredColor");
                setColor(model7PageCorrectRatioColor,      m7, "correctRatioColor");
                setCheck(model7PageAnsweredShow,           m7, "answeredShow");
                setCheck(model7PageCorrectRatioShow,       m7, "correctRatioShow");
            }

            // ----- MemberScore / TeamScore -----
//            Map<String, Object> memberScore = (Map<String, Object>) root.get("memberScore");
//            if (memberScore != null) {
//                setText(MemberScorePageWebSite, memberScore, "webSite");
//            }
//
//            Map<String, Object> teamScore = (Map<String, Object>) root.get("teamScore");
//            if (teamScore != null) {
//                setText(TeamScorePageWebSite, teamScore, "webSite");
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ==================== 保存控件数据 ====================

    public void saveConfig() {
        try {
            Map<String, Object> root = new HashMap<>();

            // ----- Model6 -----
            Map<String, Object> m6 = new HashMap<>();
            m6.put("webSite",          model6PageWebSite.getText());
            m6.put("row",              model6PageRow.getText());
            m6.put("col",              model6PageCol.getText());
            m6.put("x",                model6PageX.getText());
            m6.put("y",                model6PageY.getText());
            m6.put("cspace",           model6PageCspace.getText());
            m6.put("rspace",           model6PageRspace.getText());
            m6.put("borderW",          model6PageBorderW.getText());
            m6.put("borderL",          model6PageBorderL.getText());
            m6.put("teamFontsize",     model6PageTeamFontsize.getText());
            m6.put("teamFontWidth",    model6PageTeamFontWidth.getText());
            m6.put("answerFontsize",   model6PageAnswerFontsize.getText());
            m6.put("answerFontWidth",  model6PageAnswerFontWidth.getText());
            m6.put("backColor",        toHex(model6PageBackColor.getValue()));
            m6.put("teamColor",        toHex(model6PageTeamColor.getValue()));
            m6.put("answerColor",      toHex(model6PageAnswerColor.getValue()));
            m6.put("answeredColor",    toHex(model6PageAnsweredColor.getValue()));
            m6.put("rightColor",       toHex(model6PageRightColor.getValue()));
            m6.put("errorColor",       toHex(model6PageErrorColor.getValue()));
            m6.put("waitColor",        toHex(model6PageWaitColor.getValue()));
            m6.put("scoreShow",        model6ScoreShow.isSelected());
            root.put("model6", m6);

            // ----- Model7 -----
            Map<String, Object> m7 = new HashMap<>();
            m7.put("webSite",                  model7PageWebSite.getText());
            m7.put("row",                       model7PageRow.getText());
            m7.put("col",                       model7PageCol.getText());
            m7.put("x",                         model7PageX.getText());
            m7.put("y",                         model7PageY.getText());
            m7.put("cspace",                    model7PageCspace.getText());
            m7.put("rspace",                    model7PageRspace.getText());
            m7.put("borderW",                   model7PageBorderW.getText());
            m7.put("borderL",                   model7PageBorderL.getText());
            m7.put("teamFontsize",              model7PageTeamFontsize.getText());
            m7.put("teamFontWidth",             model7PageTeamFontWidth.getText());
            m7.put("scoreFontsize",             model7PageScoreFontsize.getText());
            m7.put("scoreFontWidth",            model7PageScoreFontWidth.getText());
            m7.put("answeredFontsize",          model7PageAnsweredFontsize.getText());
            m7.put("correctRatioFontsize",      model7PageCorrectRatioFontsize.getText());
            m7.put("correctRatioFontWidth",     model7PageCorrectRatioFontWidth.getText());
            m7.put("backColor",                 toHex(model7PageBackColor.getValue()));
            m7.put("teamColor",                 toHex(model7PageTeamColor.getValue()));
            m7.put("scoreColor",                toHex(model7PageScoreColor.getValue()));
            m7.put("scoreLargeColor",           toHex(model7PageScoreLargeColor.getValue()));
            m7.put("answeredColor",             toHex(model7PageAnsweredColor.getValue()));
            m7.put("correctRatioColor",         toHex(model7PageCorrectRatioColor.getValue()));
            m7.put("answeredShow",              model7PageAnsweredShow.isSelected());
            m7.put("correctRatioShow",          model7PageCorrectRatioShow.isSelected());
            root.put("model7", m7);

            // ----- MemberScore / TeamScore -----
            Map<String, Object> memberScore = new HashMap<>();
            memberScore.put("webSite", MemberScorePageWebSite.getText());
            root.put("memberScore", memberScore);

            Map<String, Object> teamScore = new HashMap<>();
            teamScore.put("webSite", TeamScorePageWebSite.getText());
            root.put("teamScore", teamScore);

            // 序列化并持久化
            String json = objectMapper.writeValueAsString(root);

            Configs configs = configsMapper.selectById(CONFIG_ID);
            if (configs == null) {
                configs = new Configs();
                configs.setId(CONFIG_ID);
                configs.setConfig(json);
                configsMapper.insert(configs);
            } else {
                configs.setConfig(json);
                configsMapper.updateById(configs);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ==================== 工具方法 ====================

    /** 从 Map 读取字符串并填入 TextField，key 不存在时保持控件默认值 */
    private void setText(TextField field, Map<String, Object> map, String key) {
        Object val = map.get(key);
        if (val != null) {
            field.setText(val.toString());
        }
    }

    /** 从 Map 读取 #RRGGBB 字符串并填入 ColorPicker */
    private void setColor(ColorPicker picker, Map<String, Object> map, String key) {
        Object val = map.get(key);
        if (val != null) {
            try {
                picker.setValue(Color.web(val.toString()));
            } catch (IllegalArgumentException ignored) {}
        }
    }

    /** 从 Map 读取 boolean 并填入 CheckBox */
    private void setCheck(CheckBox box, Map<String, Object> map, String key) {
        Object val = map.get(key);
        if (val instanceof Boolean) {
            box.setSelected((Boolean) val);
        }
    }

    /** JavaFX Color → #RRGGBB */
    private String toHex(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed()   * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue()  * 255));
    }

    /** 复制文本到系统剪贴板 */
    private void copyToClipboard(String text) {
        ClipboardContent content = new ClipboardContent();
        content.putString(text);
        Clipboard.getSystemClipboard().setContent(content);
    }
}