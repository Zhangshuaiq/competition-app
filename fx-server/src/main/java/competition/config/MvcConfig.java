package competition.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    
    @Resource
    private FrontInterceptor interceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
       // registry.addInterceptor(interceptor).addPathPatterns("/**");
    }
    
}


