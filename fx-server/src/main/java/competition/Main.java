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
        System.setProperty("javafx.preloader", "competition.SplashPreloader");
        Application.launch(JavaFxApplication.class, args);
    }
}
