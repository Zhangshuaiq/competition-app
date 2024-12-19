package competition.config;

import competition.entity.RegisterInfo;
import competition.mapper.RegisterInfoMapper;
import competition.util.GlobalCache;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.InputStreamReader;

//@Configuration
public class AppConfig {

//    @Bean
//    public FilterRegistrationBean<CorsFilters> corsFilterRegistration() {
//        FilterRegistrationBean<CorsFilters> registrationBean = new FilterRegistrationBean<>();
//        registrationBean.setFilter(new CorsFilters());
//        registrationBean.addUrlPatterns("/*");
//        return registrationBean;
//    }

    @Resource
    private RegisterInfoMapper registerInfoMapper;


    private int port = 8080;
    
    private static boolean enabled = false;


    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> webServerFactoryWebServerFactoryCustomizer() {
        return factory -> {
            if (!enabled) {
                factory.setPort(55555);
            } else {
                factory.setPort(port);
            }
        };
    }
}

