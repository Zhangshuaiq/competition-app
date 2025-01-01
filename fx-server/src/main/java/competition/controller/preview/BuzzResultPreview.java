package competition.controller.preview;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import competition.entity.Configs;
import competition.entity.Teams;
import competition.mapper.ConfigsMapper;
import competition.util.FontMap;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

@Component
@Scope("prototype")
public class BuzzResultPreview extends PreviewPane{

    @FXML
    private VBox container;

    @Resource
    private ConfigsMapper configsMapper;

    private static Teams teams;

    private Teams inner;

    private static boolean success;
    private boolean innerStatus;

    public static void setTeams(Teams teams, boolean success) {
        BuzzResultPreview.teams = teams;
        BuzzResultPreview.success = success;
    }

    @FXML
    public void initialize() {
        inner = teams;
        innerStatus = success;
        this.shows();
    }

    @Override
    public void shows() {
        Configs configs = configsMapper.selectById(10);
        String config = configs.getConfig();
        if (!StringUtils.isEmpty(config)) {
            JSONObject jsonObject = JSON.parseObject(config);
            Label xuhao = new Label(inner.getCode());

            String xuhaoFont = jsonObject.getOrDefault("xuhaoFont", "默认.ttf").toString();
            double xuhaoSize = Double.parseDouble(jsonObject.getOrDefault("xuhaoSize", "12").toString());
            xuhao.setFont(FontMap.load(xuhaoFont, xuhaoSize));
            xuhao.setTextFill(Color.valueOf(jsonObject.getOrDefault("xuhaoColor", "BLACK").toString()));

            Label jiancheng = new Label(inner.getShortName());

            String jianchengFont = jsonObject.getOrDefault("jianchengFont", "默认.ttf").toString();
            double jianchengSize = Double.parseDouble(jsonObject.getOrDefault("jianchengSize", "12").toString());
            jiancheng.setFont(FontMap.load(jianchengFont, jianchengSize));
            jiancheng.setTextFill(Color.valueOf(jsonObject.getOrDefault("shortColor", "BLACK").toString()));

            Label quancheng = new Label(inner.getFullName());
            String quanchengFont = jsonObject.getOrDefault("quanchengFont", "默认.ttf").toString();
            double quanchengSize = Double.parseDouble(jsonObject.getOrDefault("quanchengSize", "12").toString());
            quancheng.setFont(FontMap.load(quanchengFont, quanchengSize));
            quancheng.setTextFill(Color.valueOf(jsonObject.getOrDefault("fullColor", "BLACK").toString()));


            Label result = new Label("抢答成功");

            if (innerStatus) {
                String successFont = jsonObject.getOrDefault("successFont", "默认.ttf").toString();
                double successSize = Double.parseDouble(jsonObject.getOrDefault("successSize", "12").toString());
                result.setFont(FontMap.load(successFont, successSize));
                result.setTextFill(Color.valueOf(jsonObject.getOrDefault("successColor", "BLACK").toString()));
            } else {
                String errorFont = jsonObject.getOrDefault("errorFont", "默认.ttf").toString();
                double errorSize = Double.parseDouble(jsonObject.getOrDefault("errorSize", "12").toString());
                result.setFont(FontMap.load(errorFont, errorSize));
                result.setTextFill(Color.valueOf(jsonObject.getOrDefault("failColor", "BLACK").toString()));
            }

            Color color = Color.valueOf(jsonObject.getOrDefault("backColor", "BLACK").toString());
            container.setBackground(new Background(new BackgroundFill(color, new CornerRadii(8), null)));
            container.setPrefWidth(Double.parseDouble(jsonObject.getOrDefault("width", "200").toString()));
            container.setPrefHeight(Double.parseDouble(jsonObject.getOrDefault("height", "200").toString()));
            container.setSpacing(Double.parseDouble(jsonObject.getOrDefault("rowSpace", "10").toString()));

            if (Boolean.parseBoolean(jsonObject.getOrDefault("xuhao", "false").toString())) {
                container.getChildren().add(xuhao);
            }

            if (Boolean.parseBoolean(jsonObject.getOrDefault("jiancheng", "false").toString())) {
                container.getChildren().add(jiancheng);
            }

            if (Boolean.parseBoolean(jsonObject.getOrDefault("quancheng", "false").toString())) {
                container.getChildren().add(quancheng);
            }

            container.getChildren().add(result);
        }
    }
}
