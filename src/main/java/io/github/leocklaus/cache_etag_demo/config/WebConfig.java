package io.github.leocklaus.cache_etag_demo.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

@Configuration
public class WebConfig {

    //this is optional, only to customize shallowEtagFilter
    @Bean
    FilterRegistrationBean filterRegistrationBean(){
        FilterRegistrationBean<ShallowEtagHeaderFilter> filter
                = new FilterRegistrationBean<>(shallowEtagHeaderFilter());
        //filter.addUrlPatterns("/api/v1/**");
        filter.setName("etagFilter");
        return filter;
    }

    @Bean
    ShallowEtagHeaderFilter shallowEtagHeaderFilter(){
        return new ShallowEtagHeaderFilter();
    }

}
