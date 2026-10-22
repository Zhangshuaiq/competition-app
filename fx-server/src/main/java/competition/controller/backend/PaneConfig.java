package competition.controller.backend;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import competition.controller.ControlPane;
import competition.entity.Configs;
import competition.entity.ScoreConfig;
import competition.entity.ScoreConfig1;
import competition.mapper.ConfigsMapper;
import competition.mapper.ScoreConfigMapper;
import competition.mapper.ScoreConfigMapper1;
import competition.util.IPUtil;
import competition.util.Information;
import competition.util.Navigator;
import competition.util.RuntimeUtil;
import competition.ws.WsMessageHandler;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.*;
import java.util.*;

@Component
public class PaneConfig {


    // 链接状态控制器 // 平板分数条展示控制
    @FXML
    private CheckBox connectionStateControl, scoreStateControl;

    @FXML
    private ImageView holderQrcode;

    @FXML
    private CheckBox showCode;

    @FXML
    private TextField codeSize;

    @FXML
    private TextField teamY;

    @FXML
    private ColorPicker color3;

    @FXML
    private ColorPicker color1;

    @FXML
    private ColorPicker color2;

    @FXML
    private TextField size3;

    @FXML
    private TextField size1;

    @FXML
    private TextField  y1, y2, y3;
    

    @FXML
    private TextField scoreSize;

    @FXML
    private TextField size2;

    @FXML
    private ImageView scoreQrcode, scoreQrcode1, scoreQrcode11;
    

    @FXML
    private TextField cols;


    @FXML
    private TextField scoreY;

    @FXML
    private ImageView memberQrcode;

    @FXML
    private TextField holderWebsite;

    @FXML
    private ColorPicker scoreColor;

    @FXML
    private TextField codeY;


    @FXML
    private TextField rows;

    @FXML
    private TextField scoreWebsite, scoreWebsite1, scoreWebsite11;

    @FXML
    private TextArea contactText;

    @FXML
    private ColorPicker codeColor;

    @FXML
    private TextField memberWebsite;

    @FXML
    private TextField teamSize;

    @FXML
    private CheckBox showTeam;

    @FXML
    private ColorPicker teamColor;
    
    @Resource
    private ScoreConfigMapper scoreConfigMapper;
    
    @Resource
    private ScoreConfigMapper1 scoreConfigMapper1;

    @Resource
    private ConfigsMapper configsMapper;

    @Resource
    private ControlPane controlPane;



    
    private SimpleStringProperty ipaddr = new SimpleStringProperty("");
    @Autowired
    private WsMessageHandler wsMessageHandler;


    @FXML
    public void initialize(){

        Configs configs = configsMapper.selectById(12);
        String config = configs.getConfig();

        Configs barConfig = configsMapper.selectById(14);
        connectionStateControl.setSelected(Objects.equals("1", config));

        scoreStateControl.setSelected(RuntimeUtil.showBarStatus);
        connectionStateControl.selectedProperty().addListener((observable, oldValue, newValue) -> {

            RuntimeUtil.showConnectionStatus = newValue;
            if (RuntimeUtil.showConnectionStatus) {
                // 展示前端链接状态
                Navigator.connectionBar("/view/board/preview/TeamConnectionBar.fxml", "", "/view/style.css", true);
                configs.setConfig("1");
                configsMapper.updateById(configs);
            } else {
                configs.setConfig("0");
                configsMapper.updateById(configs);
                WindowEvent closeEvent = new WindowEvent( Navigator.connectionStage, WindowEvent.WINDOW_CLOSE_REQUEST);
                Event.fireEvent( Navigator.connectionStage, closeEvent);  // 这会触发 setOnCloseRequest()
            }
        });

        scoreStateControl.selectedProperty().addListener((observable, oldValue, newValue) -> {

            RuntimeUtil.showBarStatus = newValue;
            wsMessageHandler.notifyPage("", null);
            if (RuntimeUtil.showBarStatus) {
                // 展示前端链接状态
                barConfig.setConfig("1");
                configsMapper.updateById(barConfig);
            } else {
                barConfig.setConfig("0");
                configsMapper.updateById(barConfig);
            }
        });

        showData();

        scoreWebsite.textProperty().bindBidirectional(ipaddr);
        scoreWebsite1.textProperty().bindBidirectional(ipaddr);
        scoreWebsite11.textProperty().bindBidirectional(ipaddr);
        memberWebsite.textProperty().bindBidirectional(ipaddr);
        holderWebsite.textProperty().bindBidirectional(ipaddr);
        ipaddr.set(IPUtil.getLocalIpAddress());
        // http://localhost:8080/index.html#/
        try {
            memberQrcode.setImage(generateQRCodeImage("http://" + ipaddr.get() + ":8080/index.html#/"));
            holderQrcode.setImage(generateQRCodeImage("http://" + ipaddr.get() + ":8080/index.html#/hwait"));
            scoreQrcode.setImage(generateQRCodeImage("http://" + ipaddr.get() + ":8080/index.html#/allScore"));
            scoreQrcode1.setImage(generateQRCodeImage("http://" + ipaddr.get() + ":8080/index.html#/scoreLogin"));
            scoreQrcode11.setImage(generateQRCodeImage("http://" + ipaddr.get() + ":8080/index.html#/buzzLogin"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void showData(){
        
        // global
        ScoreConfig1 config1 = scoreConfigMapper1.selectById(1);
        rows.setText(config1.getRows() + "");
        cols.setText(config1.getCols() + "");
        size1.setText(config1.getSize() + "");
        color1.setValue(Color.valueOf(config1.getColor()));
        y1.setText(config1.getY() + "");

        ScoreConfig1 config2 = scoreConfigMapper1.selectById(2);
        rows.setText(config2.getRows() + "");
        cols.setText(config2.getCols() + "");
        size2.setText(config2.getSize() + "");
        color2.setValue(Color.valueOf(config2.getColor()));
        y2.setText(config2.getY() + "");

        ScoreConfig1 config3 = scoreConfigMapper1.selectById(3);
        rows.setText(config3.getRows() + "");
        cols.setText(config3.getCols() + "");
        size3.setText(config3.getSize() + "");
        color3.setValue(Color.valueOf(config3.getColor()));
        y3.setText(config3.getY() + "");
        
        // personal

        ScoreConfig data1 = scoreConfigMapper.selectById(1);
        ScoreConfig data2 = scoreConfigMapper.selectById(2);
        ScoreConfig data3 = scoreConfigMapper.selectById(3);

        Configs configs = configsMapper.selectById(9);
        
        codeColor.setValue(Color.valueOf(data1.getColor()));
        codeSize.setText(data1.getSize() + "");
        codeY.setText(data1.getY() + "");
        showCode.setSelected(data1.getShow() == 1);

        teamColor.setValue(Color.valueOf(data2.getColor()));
        teamSize.setText(data2.getSize() + "");
        teamY.setText(data2.getY() + "");
        showTeam.setSelected(data2.getShow() == 1);

        scoreColor.setValue(Color.valueOf(data3.getColor()));
        scoreSize.setText(data3.getSize() + "");
        scoreY.setText(data3.getY() + "");
        if (configs != null && !StringUtils.isEmpty(configs.getConfig())) {
            contactText.setText(configs.getConfig());
        }
    }

    private Image generateQRCodeImage(String text) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.MARGIN, 1); // 设置边框大小
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 120, 120, hints);
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        byte[] pngData = pngOutputStream.toByteArray();

        return new Image(new ByteArrayInputStream(pngData));
    }
    
    @FXML
    public void largeMemberQrcode(){
        Stage newStage = new Stage();
        Image image = null;
        try {
            image = generateQRCodeImage("http://" + ipaddr.get() + ":8080/index.html#/");
            ImageView zoomedImageView = new ImageView(image);
            zoomedImageView.setPreserveRatio(true);
            zoomedImageView.setFitWidth(image.getWidth() * 2); // 放大到原始大小的两倍
            zoomedImageView.setFitHeight(image.getHeight() * 2);

            // 设置背景
            StackPane newRoot = new StackPane(zoomedImageView);
            newRoot.setBackground(new Background(new BackgroundFill(Color.gray(0.3, 0.5), null, null))); // 灰色半透明背景

            Scene newScene = new Scene(newRoot, 1000, 600); // 可以调整新窗口的大小
            newStage.setScene(newScene);
            newStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    public void largeHolderQrcode(){
        Stage newStage = new Stage();
        Image image = null;
        try {
            image = generateQRCodeImage("http://" + ipaddr.get() + ":8080/index.html#/hwait");
            ImageView zoomedImageView = new ImageView(image);
            zoomedImageView.setPreserveRatio(true);
            zoomedImageView.setFitWidth(image.getWidth() * 2); // 放大到原始大小的两倍
            zoomedImageView.setFitHeight(image.getHeight() * 2);

            // 设置背景
            StackPane newRoot = new StackPane(zoomedImageView);
            newRoot.setBackground(new Background(new BackgroundFill(Color.gray(0.3, 0.5), null, null))); // 灰色半透明背景

            Scene newScene = new Scene(newRoot, 1000, 600); // 可以调整新窗口的大小
            newStage.setScene(newScene);
            newStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
       
    }
    
    @FXML
    public void largeScoreQrcode(){
        Stage newStage = new Stage();
        Image image = null;
        try {
            image = generateQRCodeImage("http://" + ipaddr.get() + ":8080/index.html#/allScore");
            ImageView zoomedImageView = new ImageView(image);
            zoomedImageView.setPreserveRatio(true);
            zoomedImageView.setFitWidth(image.getWidth() * 2); // 放大到原始大小的两倍
            zoomedImageView.setFitHeight(image.getHeight() * 2);

            // 设置背景
            StackPane newRoot = new StackPane(zoomedImageView);
            newRoot.setBackground(new Background(new BackgroundFill(Color.gray(0.3, 0.5), null, null))); // 灰色半透明背景

            Scene newScene = new Scene(newRoot, 1000, 600); // 可以调整新窗口的大小
            newStage.setScene(newScene);
            newStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void largeScoreQrcode1(){
        Stage newStage = new Stage();
        Image image = null;
        try {
            image = generateQRCodeImage("http://" + ipaddr.get() + ":8080/index.html#/scoreLogin");
            ImageView zoomedImageView = new ImageView(image);
            zoomedImageView.setPreserveRatio(true);
            zoomedImageView.setFitWidth(image.getWidth() * 2); // 放大到原始大小的两倍
            zoomedImageView.setFitHeight(image.getHeight() * 2);

            // 设置背景
            StackPane newRoot = new StackPane(zoomedImageView);
            newRoot.setBackground(new Background(new BackgroundFill(Color.gray(0.3, 0.5), null, null))); // 灰色半透明背景

            Scene newScene = new Scene(newRoot, 1000, 600); // 可以调整新窗口的大小
            newStage.setScene(newScene);
            newStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Resource
    private competition.service.TeamRankStyleService teamRankStyleService;

    private Stage teamRankStyleStage;

    @FXML
    public void configureTeamRankStyle() {
        if (teamRankStyleStage != null && teamRankStyleStage.isShowing()) {
            teamRankStyleStage.toFront();
            teamRankStyleStage.requestFocus();
            return;
        }
        competition.service.TeamRankStyleService.Style style = teamRankStyleService.load();
        Stage panelStage = new Stage();
        panelStage.initOwner(memberWebsite.getScene().getWindow());
        panelStage.initModality(javafx.stage.Modality.NONE);
        panelStage.setAlwaysOnTop(true);
        panelStage.setTitle("团队排行榜样式设置");
        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(16);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(16));
        javafx.scene.control.Spinner<Integer> titleSize = new javafx.scene.control.Spinner<>(8, 120, style.getTitleFontSize());
        javafx.scene.control.Spinner<Integer> headerSize = new javafx.scene.control.Spinner<>(8, 120, style.getHeaderFontSize());
        javafx.scene.control.Spinner<Integer> rowSize = new javafx.scene.control.Spinner<>(8, 120, style.getRowFontSize());
        CheckBox showBackground = new CheckBox("显示表头和数据行背景颜色");
        showBackground.setSelected(style.isShowRowBackground());
        ColorPicker headerBackground = new ColorPicker(Color.web(style.getHeaderBackground()));
        ColorPicker headerColor = new ColorPicker(Color.web(style.getHeaderColor()));
        ColorPicker rowBackground = new ColorPicker(Color.web(style.getRowBackground()));
        grid.addRow(0, new javafx.scene.control.Label("标题字号（px）"), titleSize);
        grid.addRow(1, new javafx.scene.control.Label("表头字号（px）"), headerSize);
        grid.addRow(2, new javafx.scene.control.Label("每行字号（px）"), rowSize);
        grid.addRow(3, new javafx.scene.control.Label("背景显示"), showBackground);
        grid.addRow(4, new javafx.scene.control.Label("表头背景颜色"), headerBackground);
        grid.addRow(5, new javafx.scene.control.Label("表头文字颜色"), headerColor);
        grid.addRow(6, new javafx.scene.control.Label("其余名次背景颜色"), rowBackground);
        java.util.List<ColorPicker> rowPickers = new ArrayList<>();
        headerBackground.disableProperty().bind(showBackground.selectedProperty().not());
        rowBackground.disableProperty().bind(showBackground.selectedProperty().not());
        for (int i = 0; i < 3; i++) {
            String color = i < style.getRowColors().size() ? style.getRowColors().get(i) : style.getRowBackground();
            ColorPicker picker = new ColorPicker(Color.web(color));
            picker.disableProperty().bind(showBackground.selectedProperty().not());
            rowPickers.add(picker);
            grid.addRow(7 + i, new javafx.scene.control.Label("第 " + (i + 1) + " 名背景颜色"), picker);
        }
        CheckBox showContainerBackground = new CheckBox("显示内容整体背景");
        showContainerBackground.setSelected(style.isShowContainerBackground());
        ColorPicker containerBackground = new ColorPicker(Color.web(style.getContainerBackground()));
        containerBackground.disableProperty().bind(showContainerBackground.selectedProperty().not());
        CheckBox autoCenter = new CheckBox("内容自动居中");
        autoCenter.setSelected(style.isAutoCenter());
        TextField contentX = new TextField(String.valueOf(style.getContentX()));
        TextField contentY = new TextField(String.valueOf(style.getContentY()));
        contentX.disableProperty().bind(autoCenter.selectedProperty());
        contentY.disableProperty().bind(autoCenter.selectedProperty());
        grid.addRow(10, new javafx.scene.control.Label("整体背景显示"), showContainerBackground);
        grid.addRow(11, new javafx.scene.control.Label("整体背景颜色"), containerBackground);
        grid.addRow(12, new javafx.scene.control.Label("内容位置"), autoCenter);
        grid.addRow(13, new javafx.scene.control.Label("左上角 X（网页像素）"), contentX);
        grid.addRow(14, new javafx.scene.control.Label("左上角 Y（网页像素）"), contentY);
        javafx.scene.control.Label positionHint = new javafx.scene.control.Label("取消自动居中后，X 向右、Y 向下，以网页左上角为原点。");
        positionHint.setWrapText(true);
        positionHint.setMaxWidth(420);
        grid.add(positionHint, 0, 15, 2, 1);
        javafx.scene.control.ScrollPane scroll = new javafx.scene.control.ScrollPane(grid);
        scroll.setFitToWidth(true);
        scroll.setPrefViewportHeight(540);
        javafx.scene.control.Label status = new javafx.scene.control.Label();
        status.setWrapText(true);
        javafx.scene.control.Button save = new javafx.scene.control.Button("保存");
        javafx.scene.control.Button close = new javafx.scene.control.Button("关闭");
        close.setOnAction(event -> panelStage.close());
        javafx.scene.layout.HBox actions = new javafx.scene.layout.HBox(12, status, save, close);
        actions.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        actions.setPadding(new javafx.geometry.Insets(12, 16, 12, 16));
        javafx.scene.layout.BorderPane pane = new javafx.scene.layout.BorderPane();
        pane.setCenter(scroll);
        pane.setBottom(actions);
        panelStage.setScene(new Scene(pane, 480, 620));
        save.setOnAction(event -> {
            int x;
            int y;
            try {
                x = Integer.parseInt(contentX.getText().trim());
                y = Integer.parseInt(contentY.getText().trim());
            } catch (NumberFormatException ex) {
                status.setText("X、Y 坐标请输入整数");
                return;
            }
            style.setContentX(x);
            style.setContentY(y);
            style.setAutoCenter(autoCenter.isSelected());
            style.setShowContainerBackground(showContainerBackground.isSelected());
            style.setContainerBackground(webColor(containerBackground.getValue()));
            style.setTitleFontSize(titleSize.getValue());
            style.setHeaderFontSize(headerSize.getValue());
            style.setRowFontSize(rowSize.getValue());
            style.setShowRowBackground(showBackground.isSelected());
            style.setHeaderBackground(webColor(headerBackground.getValue()));
            style.setHeaderColor(webColor(headerColor.getValue()));
            style.setRowBackground(webColor(rowBackground.getValue()));
            java.util.List<String> colors = new ArrayList<>();
            for (ColorPicker picker : rowPickers) colors.add(webColor(picker.getValue()));
            style.setRowColors(colors);
            try {
                teamRankStyleService.save(style);
                status.setText("已保存，排行榜约 3 秒内更新");
            } catch (Exception ex) {
                status.setText("保存失败，请重试");
            }
        });
        teamRankStyleStage = panelStage;
        panelStage.setOnHidden(event -> teamRankStyleStage = null);
        panelStage.show();
        panelStage.toFront();
    }

    private String webColor(Color color) {
        return String.format("#%02x%02x%02x", Math.round(color.getRed() * 255),
                Math.round(color.getGreen() * 255), Math.round(color.getBlue() * 255));
    }

    @FXML
    public void openRankPage(){
        try {
            Desktop.getDesktop().browse(new URI("http://" + ipaddr.get() + ":8080/index.html#/allRank"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void openMemberRankPage(){
        try {
            Desktop.getDesktop().browse(new URI("http://" + ipaddr.get() + ":8080/index.html#/allMemberRank"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void largeScoreQrcode11(){
        Stage newStage = new Stage();
        Image image = null;
        try {
            image = generateQRCodeImage("http://" + ipaddr.get() + ":8080/index.html#/buzzLogin");
            ImageView zoomedImageView = new ImageView(image);
            zoomedImageView.setPreserveRatio(true);
            zoomedImageView.setFitWidth(image.getWidth() * 2); // 放大到原始大小的两倍
            zoomedImageView.setFitHeight(image.getHeight() * 2);

            // 设置背景
            StackPane newRoot = new StackPane(zoomedImageView);
            newRoot.setBackground(new Background(new BackgroundFill(Color.gray(0.3, 0.5), null, null))); // 灰色半透明背景

            Scene newScene = new Scene(newRoot, 1000, 600); // 可以调整新窗口的大小
            newStage.setScene(newScene);
            newStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    public void update1(){
        try {
            memberQrcode.setImage(generateQRCodeImage("http://" + ipaddr.get() + ":8080/index.html#/"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void update2(){
        try {
            holderQrcode.setImage(generateQRCodeImage("http://" + ipaddr.get() + ":8080/index.html#/hwait"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void update3(){
        try {
            scoreQrcode.setImage(generateQRCodeImage("http://" + ipaddr.get() + ":8080/index.html#/allScore"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void update4(){
        try {
            scoreQrcode1.setImage(generateQRCodeImage("http://" + ipaddr.get() + ":8080/index.html#/scoreLogin"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void update44(){
        try {
            scoreQrcode11.setImage(generateQRCodeImage("http://" + ipaddr.get() + ":8080/index.html#/buzzLogin"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void copy1(){
        doCopy("http://" + ipaddr.get() + ":8080/index.html#/");
    }

    @FXML
    public void copy2(){
        doCopy("http://" + ipaddr.get() + ":8080/index.html#/hwait");
    }

    @FXML
    public void copy3(){
        doCopy("http://" + ipaddr.get() + ":8080/index.html#/allScore");
    }

    @FXML
    public void copy4(){
        doCopy("http://" + ipaddr.get() + ":8080/index.html#/scoreLogin");
    }

    @FXML
    public void copy5(){
        doCopy("http://" + ipaddr.get() + ":8080/index.html#/buzzLogin");
    }

    private void doCopy(String text){
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        // 设置文本内容
        content.putString(text);
        // 将内容放入剪贴板
        clipboard.setContent(content);
    }


    @FXML
    public void update5(){
        String text = contactText.getText();
        if (StringUtils.isEmpty(text)) {
            Information.error("请填写联系信息");
            return;
        }
        Configs configs = configsMapper.selectById(9);
        if (configs == null) {
            configs = new Configs();
            configs.setId(9);
            configs.setType("联系方式");
            configs.setConfig(text);
            configsMapper.insert(configs);
        } else {
            configs.setType("联系方式");
            configs.setConfig(text);
            configsMapper.updateById(configs);
        }
    }
    
    
    @FXML
    public void updateConfig(){
        ScoreConfig1 config1 = scoreConfigMapper1.selectById(1);
        ScoreConfig1 config2 = scoreConfigMapper1.selectById(2);
        ScoreConfig1 config3 = scoreConfigMapper1.selectById(3);

        ScoreConfig data1 = scoreConfigMapper.selectById(1);
        ScoreConfig data2 = scoreConfigMapper.selectById(2);
        ScoreConfig data3 = scoreConfigMapper.selectById(3);
        
        config1.setCols(Integer.parseInt(cols.getText()));
        config1.setRows(Integer.parseInt(rows.getText()));
        config1.setSize(Integer.parseInt(size1.getText()));
        config1.setColor(color1.getValue().toString());
        config1.setY(Integer.parseInt(y1.getText()));

        config2.setCols(Integer.parseInt(cols.getText()));
        config2.setRows(Integer.parseInt(rows.getText()));
        config2.setSize(Integer.parseInt(size2.getText()));
        config2.setColor(color2.getValue().toString());
        config2.setY(Integer.parseInt(y2.getText()));

        config3.setCols(Integer.parseInt(cols.getText()));
        config3.setRows(Integer.parseInt(rows.getText()));
        config3.setSize(Integer.parseInt(size3.getText()));
        config3.setColor(color3.getValue().toString());
        config3.setY(Integer.parseInt(y3.getText()));

        data1.setColor(codeColor.getValue().toString());
        data1.setShow(showCode.isSelected() ?1: 0);
        data1.setY(Integer.parseInt(codeY.getText()));
        data1.setSize(Integer.parseInt(codeSize.getText()));

        data2.setColor(teamColor.getValue().toString());
        data2.setShow(showTeam.isSelected() ?1: 0);
        data2.setY(Integer.parseInt(teamY.getText()));
        data2.setSize(Integer.parseInt(teamSize.getText()));

        data3.setColor(scoreColor.getValue().toString());
        data3.setShow(1);
        data3.setY(Integer.parseInt(scoreY.getText()));
        data3.setSize(Integer.parseInt(scoreSize.getText()));

        scoreConfigMapper1.updateById(config1);
        scoreConfigMapper1.updateById(config2);
        scoreConfigMapper1.updateById(config3);

        scoreConfigMapper.updateById(data1);
        scoreConfigMapper.updateById(data2);
        scoreConfigMapper.updateById(data3);
        
    }
}
