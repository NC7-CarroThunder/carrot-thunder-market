package bitcamp.carrot_thunder.config;

import bitcamp.carrot_thunder.jwt.JwtAuthFilter;
import bitcamp.carrot_thunder.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity // 스프링 Security 지원을 가능하게 함
@EnableGlobalMethodSecurity(securedEnabled = true) // @Secured 어노테이션 활성화
public class SecurityConfig {

    @Autowired
    private JwtUtil jwtUtil;


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // 이 설정을 해주지 않으면 밑의 corsConfigurationSource가 적용되지 않습니다!
        http.cors();

        return http
                .csrf().disable()
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                        .and().addFilterBefore(new JwtAuthFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)
                )
                .build();

        //TODO : 단군마켓에서 설정한 securiy설정 - 참고후 커스터마이징해서 추가해야함
        // http.csrf().disable();
        //

//        http.authorizeRequests()
////                .antMatchers("/api/user/signup").permitAll() // 회원가입과 로그인은 허용
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

    @Bean
    public CorsConfigurationSource corsConfigurationSource(){

        CorsConfiguration config = new CorsConfiguration();

        // 사전에 약속된 출처를 명시
        config.addAllowedOrigin("http://localhost:3000");
        //config.addAllowedOrigin("http://localhost:3001");
        //config.addAllowedOrigin("http://carrothunder.store/");
        //config.addAllowedOrigin("https://carrothunder.store/");
        //채팅 테스트용 origin
        // config.addAllowedOrigin("http://localhost:8080");


        // 특정 헤더를 클라이언트 측에서 사용할 수 있게 지정
        // 만약 지정하지 않는다면, Authorization 헤더 내의 토큰 값을 사용할 수 없음
        config.addExposedHeader(JwtUtil.AUTHORIZATION_HEADER);


        // 본 요청에 허용할 HTTP method(예비 요청에 대한 응답 헤더에 추가됨)
        config.addAllowedMethod("*");

        // 본 요청에 허용할 HTTP header(예비 요청에 대한 응답 헤더에 추가됨)
        config.addAllowedHeader("*");

        // 기본적으로 브라우저에서 인증 관련 정보들을 요청 헤더에 담지 않음
        // 이 설정을 통해서 브라우저에서 인증 관련 정보들을 요청 헤더에 담을 수 있도록 해줍니다.
        config.setAllowCredentials(true);

        // allowCredentials 를 true로 하였을 때,
        // allowedOrigin의 값이 * (즉, 모두 허용)이 설정될 수 없도록 검증합니다.
        config.validateAllowCredentials();

        // 어떤 경로에 이 설정을 적용할 지 명시합니다. (여기서는 전체 경로)
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }


}