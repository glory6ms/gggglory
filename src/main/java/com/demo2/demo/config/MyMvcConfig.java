package com.demo2.demo.config;

import com.demo2.demo.compent.MyHandleInteceptor;
import com.demo2.demo.compent.MyLocaleResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class MyMvcConfig implements WebMvcConfigurer {
    public void addViewControllers(ViewControllerRegistry registry){
        registry.addViewController("/glory").setViewName("success");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
    }

    @Bean
    public WebMvcConfigurer myConfig(){

        WebMvcConfigurer configurer = new WebMvcConfigurer(){

            @Override
            public void addViewControllers(ViewControllerRegistry registry) {
                //配置首页
//                registry.addViewController("/").setViewName("login");
                //配置主页
                registry.addViewController("/index.html").setViewName("maps/indexMap");
                registry.addViewController("/main.html").setViewName("dashboard");
            }

            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(new MyHandleInteceptor()).addPathPatterns("/**")
                        .excludePathPatterns("/","/user/login","/webjars/**","/static/asserts/css/**","/static/asserts/js/**"
                                ,"/*.js","/*.css","/asserts/css/**","/asserts/js/**","/seaMap","/obs","/ajax/modal","/ajax/heat","/ajax/line"
                        ,"/jst","/ajax/draw","/ajax/draw_line");
            }
        };
        return configurer;
    }
    @Bean
    public LocaleResolver localeResolver(){

        return new MyLocaleResolver();
    }

}
