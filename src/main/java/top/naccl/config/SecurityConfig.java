package top.naccl.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import top.naccl.service.LoginLogService;
import top.naccl.service.impl.UserServiceImpl;

/**
 * @Description: Spring Security配置类
 * @Author: Naccl
 * @Date: 2020-07-19
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    UserServiceImpl userService;
    @Autowired
    LoginLogService loginLogService;
    @Autowired
    MyAuthenticationEntryPoint myAuthenticationEntryPoint;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(HttpSecurity httpSecurity) throws Exception {
        AuthenticationManager authenticationManager = httpSecurity.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userService)
                .passwordEncoder(bCryptPasswordEncoder())
                .and()
                .build();
        return authenticationManager;
    }
    @Bean
    public SecurityFilterChain securityFilterChain(AuthenticationManager authenticationManager, HttpSecurity http) throws Exception {
        http
                //禁用 csrf 防御
                .csrf().disable()
                //开启跨域支持
                .cors().and()
                //基于Token，不创建会话
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests()
                //放行获取网页标题后缀的请求
                .requestMatchers("/admin/webTitleSuffix").permitAll()
                //任何 /admin 开头的路径下的请求都需要经过JWT验证
                .requestMatchers(HttpMethod.GET, "/admin/**").hasAnyRole("admin", "visitor")
                .requestMatchers("/admin/**").hasRole("admin")
                //其它路径全部放行
                .anyRequest().permitAll()
                .and()
                //自定义JWT过滤器
                .addFilterBefore(new JwtLoginFilter("/admin/login", authenticationManager, loginLogService), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtFilter(), UsernamePasswordAuthenticationFilter.class)
                //未登录时，返回json，在前端执行重定向
                .exceptionHandling().authenticationEntryPoint(myAuthenticationEntryPoint);
        return http.build();
    }
}
