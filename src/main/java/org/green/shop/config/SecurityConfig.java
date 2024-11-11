package org.green.shop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        //loginPage("") : 사용자로그인페이지
        //defaultSuccessUrl("/") : 로그인시 이동할 페이지 지정
        //loginProcessingUrl("/loginProc") : 사용자로그인페이지 form action속성값
        http.formLogin(login->login.loginPage("/login_page")
                .defaultSuccessUrl("/",true)
                .loginProcessingUrl("/loginProc")
                .failureUrl("/login/error")
        );
        http.logout((auth)->auth.logoutUrl("/logout")
                .logoutSuccessUrl("/")
        );
        //경로 권한지정
        http.authorizeHttpRequests((auth)->auth
                .requestMatchers("/","/login_page","/login/error","/join_page","/emailcheck","/join","/item/**","/images/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/js/**","/css/**","/img/**").permitAll()
                .anyRequest().authenticated()
        );

        // CSRF 토큰 비활성화
        http.csrf(cs -> cs.disable());

        // 세션 관리 설정: 세션을 유지하도록 설정
        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
        );

        // 쿠키 설정: SameSite 설정을 None으로 설정하고, HTTPS 요구 사항 비활성화
        http.requiresChannel(channel -> channel.anyRequest().requiresInsecure());

        // CORS 설정 추가
        http.cors(withDefaults());
        return http.build();
    }
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return web -> web.ignoring().requestMatchers(
                new AntPathRequestMatcher("/css/**"),
                new AntPathRequestMatcher("/img/**")
        );
    }
}
