package competition;

import javafx.application.Application;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("competition.mapper")
@EnableScheduling  
public class Main {

    public static void main(String[] args) throws Exception {
        Application.launch(JavaFxApplication.class, args);
        // 启动 springboot ，主要作为 ws 服务器接收数据
        SpringApplication.run(Main.class, args);
        //
    }
}
 
