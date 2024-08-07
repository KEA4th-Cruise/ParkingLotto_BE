package com.cruise.parkinglotto.global.config;

import com.cruise.parkinglotto.global.config.webConfig.CorsConfig;
import com.cruise.parkinglotto.global.filter.JwtAuthenticationFilter;
import com.cruise.parkinglotto.global.jwt.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private final JwtUtils jwtUtils;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .httpBasic(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(CorsConfig.corsConfigurationSource()))
                .sessionManagement(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-resources/**").permitAll()
                        .requestMatchers("/api/members/login", "/api/members/logout", "/api/members/refresh").permitAll()
                        .requestMatchers("/api/draws",
                                "/api/draws/{drawId}",
                                "/api/draws/{drawId}/parking-spaces",
                                "/api/draws/{drawId}/applicants",
                                "/api/draws/{drawId}/applicants/{applicantId}/admin-cancel",
                                "/api/draws/{drawId}/priority-applicants",
                                "/api/draws/{drawId}/priority-applicants/{priorityApplicantId}/approval",
                                "/api/draws/{drawId}/applicants/search",
                                "/api/draws/{drawId}/winners/search",
                                "/api/draws/{drawId}/execution",
                                "/api/register/info/{accountId}",
                                "/api/register/info/{accountId}/approval",
                                "/api/register/info/{accountId}/refusal",
                                "/api/register/members",
                                "/api/register/members/search",
                                "/{drawId}/priority-applicants/approved/assignment",
                                "/{drawId}/priority-applicants/{priorityApplicantId}/cancel").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtils), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}