package vip.aquan.specialcharacterfilter.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.DelegatingFilterProxy;
import vip.aquan.specialcharacterfilter.filter.RequestParamFilter;

import javax.servlet.DispatcherType;

/**
 * Filter配置
 *
 * @author wcp
 */
@Configuration
public class FilterConfig {
    @Bean
    public FilterRegistrationBean requestParamFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new RequestParamFilter());
        registration.addUrlPatterns("/*");
        //忽略过滤，需要配合Filter配合使用
//        registration.addInitParameter("exclusions", "web/generator/common/exportPdf");
        registration.setName("requestParamFilter");
        registration.setOrder(Integer.MAX_VALUE);
        return registration;
    }
}
