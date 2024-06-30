package competition;

import competition.util.GlobalCache;
import competition.util.Navigator;
import javafx.application.Application;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.swing.*;

@SpringBootApplication
@MapperScan("competition.mapper")
public class Main {

    public static void main(String[] args) {
        Application.launch(JavaFxApplication.class, args);
        // 启动 springboot ，主要作为 ws 服务器接收数据
        SpringApplication.run(Main.class, args);
        //
    }
}
