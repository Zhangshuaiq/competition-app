package competition.controller.backend;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.Scene;
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
    private TextField scoreWebsite;

    @FXML
    private TextField memberWebsite;

    @FXML
    private ImageView memberQrcode;

    @FXML
    private ImageView scoreQrcode;

    @FXML
    private TextField holderWebsite;

    @FXML
    private ImageView holderQrcode;
    
    private SimpleStringProperty ipaddr = new SimpleStringProperty("");
    
    
    
    @FXML
    public void initialize(){


        scoreWebsite.textProperty().bindBidirectional(ipaddr);
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
        
        try {
            memberQrcode.setImage(generateQRCodeImage("http://" + ipaddr.get() + ":8080/#/"));
            holderQrcode.setImage(generateQRCodeImage("http://" + ipaddr.get() + ":8080/#/hwait"));
            scoreQrcode.setImage(generateQRCodeImage("http://" + ipaddr.get() + ":8080/#/"));
        } catch (Exception e) {
            e.printStackTrace();
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
            image = generateQRCodeImage("http://" + ipaddr.get() + ":8080/#/");
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
            image = generateQRCodeImage("http://" + ipaddr.get() + ":8080/#/hwait");
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
            image = generateQRCodeImage("http://" + ipaddr.get() + ":8080/#/");
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
            memberQrcode.setImage(generateQRCodeImage("http://" + ipaddr.get() + ":8080/#/"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void update2(){
        try {
            holderQrcode.setImage(generateQRCodeImage("http://" + ipaddr.get() + ":8080/#/hwait"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void update3(){
        try {
            scoreQrcode.setImage(generateQRCodeImage("http://" + ipaddr.get() + ":8080/#/"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
