package bitcamp.carrot_thunder.config;

import bitcamp.carrot_thunder.jwt.JwtAuthFilter;
import bitcamp.carrot_thunder.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity // 스프링 Security 지원을 가능하게 함
@EnableGlobalMethodSecurity(securedEnabled = true) // @Secured 어노테이션 활성화
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )
                .build();

        //TODO : 단군마켓에서 설정한 securiy설정 - 참고후 커스터마이징해서 추가해야함
       // http.csrf().disable();
        //

//        http.authorizeRequests()
////                .antMatchers("/api/user/signup").permitAll()
////                .antMatchers("/api/user/login").permitAll()
//                .antMatchers("**").permitAll()
//                .antMatchers().authenticated()
//                .and().addFilterBefore(new JwtAuthFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);
//
//        // 특정 게시글 조회, 삭제, 수정 : "api/memos/{id}"
//        // 게시글 전체 조회, 작성 : "/api/memos"
//
//        http.authorizeRequests().anyRequest().authenticated();
//
//        // 이 설정을 해주지 않으면 밑의 corsConfigurationSource가 적용되지 않습니다!
//        http.cors();
//
//        return http.build();
    }


}
