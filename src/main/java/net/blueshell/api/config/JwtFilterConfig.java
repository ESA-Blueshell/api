//package net.blueshell.api.config;
//
//import net.blueshell.api.auth.JwtAuthFilter;
//import org.springframework.boot.web.servlet.FilterRegistrationBean;
//import org.springframework.context.annotation.Bean;
//
//public class JwtFilterConfig {
//    @Bean
//    public FilterRegistrationBean<JwtAuthFilter> jwtFilterRegistration(JwtAuthFilter f) {
//        FilterRegistrationBean<JwtAuthFilter> reg = new FilterRegistrationBean<>(f);
//        reg.setEnabled(false);
//        return reg;
//    }
//}
