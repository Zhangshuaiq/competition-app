package competition.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsFilters {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // 应用于所有的路径
                        .allowedOrigins("*") // 允许的来源
                        .allowedMethods("*") // 允许的方法
                        .allowedHeaders("*"); // 允许的头部
            }
        };
    }
}
