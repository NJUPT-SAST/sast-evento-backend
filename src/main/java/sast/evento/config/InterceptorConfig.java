package sast.evento.config;

import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import sast.evento.interceptor.HttpInterceptor;
import sast.evento.interceptor.LogInterceptor;

/**
 * @author: NicoAsuka
 * @date: 4/29/23
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Resource
    private LogInterceptor logInterceptor;
    @Resource
    private HttpInterceptor httpInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(httpInterceptor)
                .addPathPatterns("/**");
        registry.addInterceptor(logInterceptor)
                .addPathPatterns("/**");
    }


}
