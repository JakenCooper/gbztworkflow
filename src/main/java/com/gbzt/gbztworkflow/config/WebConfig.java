package com.gbzt.gbztworkflow.config;

import com.gbzt.gbztworkflow.interceptors.CorsInterceptor;
import com.gbzt.gbztworkflow.interceptors.EncodeInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.List;
import java.util.Properties;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"com.gbzt.gbztworkflow.modules",
        "com.gbzt.gbztworkflow.interceptors"})
public class WebConfig extends WebMvcConfigurerAdapter {


    @Autowired
    private EncodeInterceptor encodeInterceptor;

    @Autowired
    private CorsInterceptor corsInterceptor;

    @Bean(name="defaultViewResovler")
    public InternalResourceViewResolver getDefaultViewResolver(){
        InternalResourceViewResolver irv = new InternalResourceViewResolver();
        irv.setPrefix("/WEB-INF/modules/");
        //irv.setSuffix(".html");
        irv.setContentType("UTF-8");
        return irv;
    }

    @Bean
    public SimpleUrlHandlerMapping getSimpleUrlHandlerMapping(){
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setOrder(Integer.MAX_VALUE -2 );
        Properties prop = new Properties();
        prop.setProperty("/flowservice","flowService");
        mapping.setMappings(prop);
        return mapping;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(encodeInterceptor);
        registry.addInterceptor(corsInterceptor);
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter jacksonMessageConverter = new MappingJackson2HttpMessageConverter();
        converters.add(jacksonMessageConverter);
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/img/**").addResourceLocations("/img/");
    }
}
