package competition.controller.backend;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import competition.controller.ControlPane;
import competition.controller.preview.PreviewPane;
import competition.entity.Configs;
import competition.entity.ScoreConfig;
import competition.entity.ScoreConfig1;
import competition.mapper.ConfigsMapper;
import competition.mapper.ScoreConfigMapper;
import competition.mapper.ScoreConfigMapper1;
import competition.util.Information;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.*;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Component
public class PaneConfig {


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
    
    
    
    @FXML
    public void initialize(){

        
        showData();

        scoreWebsite.textProperty().bindBidirectional(ipaddr);
        scoreWebsite1.textProperty().bindBidirectional(ipaddr);
        scoreWebsite11.textProperty().bindBidirectional(ipaddr);
        memberWebsite.textProperty().bindBidirectional(ipaddr);
        holderWebsite.textProperty().bindBidirectional(ipaddr);
        
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface iface : Collections.list(interfaces)) {
                // 检查接口是否激活，且不是回环接口
                if (iface.isUp() && !iface.isLoopback()) {
                    Enumeration<InetAddress> addresses = iface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        InetAddress addr = addresses.nextElement();
                        // 确保IPv4地址且非localhost
                        if (addr.getHostAddress().indexOf(':') == -1 && !addr.isLoopbackAddress()) {
                            ipaddr.set(addr.getHostAddress());
                            break;
                        }
                    }
                    if (!StringUtils.isEmpty(ipaddr.get())) {
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
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
