package hello.login;

import hello.login.web.Interceptor.LogInterceptor;
import hello.login.web.argumentresolver.LoginMemberArgumentResolver;
import hello.login.web.filter.LogFilter;
import hello.login.web.filter.LoginCheckFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.Filter;
import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LoginMemberArgumentResolver());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LogInterceptor())
            .order(1)
            .addPathPatterns("/**")
            .excludePathPatterns("/css/**","/*.ico","/error");

        registry.addInterceptor(new LogInterceptor())
            .order(2)
                .addPathPatterns("/**")
                .excludePathPatterns("/","/css/**","/*.ico","/error","/members/add","/login","/logout");
    }

//    @Bean
    public FilterRegistrationBean logFilter(){
        FilterRegistrationBean<Filter> filterFilterRegistrationBean=new FilterRegistrationBean<>();
        filterFilterRegistrationBean.setFilter(new LogFilter());    // 사용할 필터 설정
        filterFilterRegistrationBean.setOrder(1);   // 체인의 순서 지정 , 낮을수록 먼저 실행
        filterFilterRegistrationBean.addUrlPatterns("/*");  // 필터를 적용할 URL 패턴을 지정한다. 한번에 여러 패턴을 지정할 수 있다.

        return filterFilterRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean loginCheckFilter(){
        FilterRegistrationBean<Filter> filterFilterRegistrationBean=new FilterRegistrationBean<>();
        filterFilterRegistrationBean.setFilter(new LoginCheckFilter());    // 사용할 필터 설정
        filterFilterRegistrationBean.setOrder(2);   // 체인의 순서 지정 , 낮을수록 먼저 실행
        filterFilterRegistrationBean.addUrlPatterns("/*");  // 필터를 적용할 URL 패턴을 지정한다. 한번에 여러 패턴을 지정할 수 있다.

        return filterFilterRegistrationBean;
    }
}
