package competition.controller.preview;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import competition.JavaFxApplication;
import competition.controller.ControlPane;
import competition.controller.match.BuzzResult;
import competition.controller.match.CluePreview;
import competition.entity.Configs;
import competition.entity.Teams;
import competition.mapper.ConfigsMapper;
import competition.util.ExtendedScreen;
import competition.util.FontMap;
import competition.util.Navigator;
import competition.util.ScreenAdapter;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import java.util.HashMap;
import java.util.Map;

@Component
public class BuzzResultPreview extends PreviewPane{

    @FXML
    private VBox container;


    private static Teams teams;

    private Teams inner;

    private static boolean success;
    private boolean innerStatus;

    public static void setTeams(Teams teams, boolean success) {
        BuzzResultPreview.teams = teams;
        BuzzResultPreview.success = success;
    }

    @FXML
    public void initPage() {
        inner = teams;
        innerStatus = success;
        this.shows();
    }

    @Override
    public void shows() {
        if (inner == null) {
            return;
        }
        ControlPane controlPane = JavaFxApplication.getApplicationContext().getBean(ControlPane.class);
        controlPane.setBuzzResultPreview(this);
        if (ExtendedScreen.hasShow()) {
            Map<String, Object> data = new HashMap<>();
            data.put("teams", inner);
            data.put("success", innerStatus);
            Object res = ExtendedScreen.buzzResult("/view/match/BuzzResult.fxml", "", "/view/style.css", true, data);
            if (res != null) {
                controlPane.setBuzzResult((BuzzResult) res);
            }
        }

        ConfigsMapper configsMapper = JavaFxApplication.getApplicationContext().getBean(ConfigsMapper.class);
        Configs configs = configsMapper.selectById(10);
        String config = configs.getConfig();
        if (!StringUtils.isEmpty(config)) {
            JSONObject jsonObject = JSON.parseObject(config);
            Label xuhao = new Label(inner.getCode());

            xuhao.setAlignment(Pos.CENTER.CENTER);
            String xuhaoFont = jsonObject.getOrDefault("xuhaoFont", "默认.ttf").toString();
            double xuhaoSize =  ScreenAdapter.calculateMainX(Double.parseDouble(jsonObject.getOrDefault("xuhaoSize", "12").toString()));
            xuhao.setFont(FontMap.load(xuhaoFont, xuhaoSize));
            xuhao.setTextFill(Color.valueOf(jsonObject.getOrDefault("xuhaoColor", "BLACK").toString()));

            Label jiancheng = new Label(inner.getShortName());
            jiancheng.setAlignment(Pos.CENTER.CENTER);
            String jianchengFont = jsonObject.getOrDefault("jianchengFont", "默认.ttf").toString();
            double jianchengSize =  ScreenAdapter.calculateMainX(Double.parseDouble(jsonObject.getOrDefault("jianchengSize", "12").toString()));
            jiancheng.setFont(FontMap.load(jianchengFont, jianchengSize));
            jiancheng.setTextFill(Color.valueOf(jsonObject.getOrDefault("shortColor", "BLACK").toString()));

            Label quancheng = new Label(inner.getFullName());
            String quanchengFont = jsonObject.getOrDefault("quanchengFont", "默认.ttf").toString();
            double quanchengSize =  ScreenAdapter.calculateMainX(Double.parseDouble(jsonObject.getOrDefault("quanchengSize", "12").toString()));
            quancheng.setAlignment(Pos.CENTER.CENTER);
            quancheng.setFont(FontMap.load(quanchengFont, quanchengSize));
            quancheng.setTextFill(Color.valueOf(jsonObject.getOrDefault("fullColor", "BLACK").toString()));


            Label result = new Label();
            result.setAlignment(Pos.CENTER.CENTER);
            if (success) {
                result.setText("抢答成功");
                String successFont = jsonObject.getOrDefault("successFont", "默认.ttf").toString();
                double successSize =  ScreenAdapter.calculateMainX(Double.parseDouble(jsonObject.getOrDefault("successSize", "12").toString()));
                result.setFont(FontMap.load(successFont,(successSize)));
                result.setTextFill(Color.valueOf(jsonObject.getOrDefault("successColor", "BLACK").toString()));
            } else {
                result.setText("抢答犯规");
                String errorFont = jsonObject.getOrDefault("errorFont", "默认.ttf").toString();
                double errorSize =  ScreenAdapter.calculateMainX(Double.parseDouble(jsonObject.getOrDefault("errorSize", "12").toString()));
                result.setFont(FontMap.load(errorFont, errorSize));
                result.setTextFill(Color.valueOf(jsonObject.getOrDefault("failColor", "BLACK").toString()));
            }

            double width = ScreenAdapter.calculateMainX(Double.parseDouble(jsonObject.getOrDefault("width", "200").toString()));
            xuhao.setPrefWidth(width);
            jiancheng.setPrefWidth(width);
            quancheng.setPrefWidth(width);
            result.setPrefWidth(width);

            xuhao.setPadding(new Insets(10, 0, 10, 0));
            jiancheng.setPadding(new Insets(10, 0, 10, 0));
            quancheng.setPadding(new Insets(10, 0, 10, 0));
            result.setPadding(new Insets(10, 0, 10, 0));

            Color color = Color.valueOf(jsonObject.getOrDefault("backColor", "BLACK").toString());
            container.setBackground(new Background(new BackgroundFill(color, new CornerRadii(8), null)));
            container.setPrefWidth(width);
            container.setPrefHeight(ScreenAdapter.calculateMainY(Double.parseDouble(jsonObject.getOrDefault("height", "200").toString())));
            container.setSpacing(ScreenAdapter.calculateMainY(Double.parseDouble(jsonObject.getOrDefault("rowSpace", "10").toString())));
            //container.setLayoutX(ScreenAdapter.calculateMainX(Double.parseDouble(jsonObject.getOrDefault("x", "200").toString())));
            //container.setLayoutY(ScreenAdapter.calculateMainY(Double.parseDouble(jsonObject.getOrDefault("y", "200").toString())));

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
            Navigator.buzzBoardStage.setWidth(container.getPrefWidth());
            Navigator.buzzBoardStage.setHeight(container.getPrefHeight());
        }
    }
}
