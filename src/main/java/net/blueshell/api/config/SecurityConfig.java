package net.blueshell.api.config;

import net.blueshell.api.auth.JwtAuthenticationEntryPoint;
import net.blueshell.common.enums.Role;
import net.blueshell.common.filter.HeaderAuthFilter;
import net.blueshell.db.permission.CompositePermissionEvaluator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;

import static org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl.fromHierarchy;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
    private final HeaderAuthFilter headerAuthFilter;

    public SecurityConfig(JwtAuthenticationEntryPoint authenticationEntryPoint, HeaderAuthFilter headerAuthFilter) {
        this.headerAuthFilter = headerAuthFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        String hierarchyString = (String) Arrays.stream(Role.values())
                .sorted((a, b) -> b.getAuthorities().size() - a.getAuthorities().size())
                .map(Role::getName)
                .reduce((a, b) -> a + " > " + b)
                .orElse("");
        return fromHierarchy(hierarchyString);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/authenticate",
                                "/user-details",
                                "/users/activate",
                                "/users/password"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/events/**",
                                "/download/**",
                                "/committees**",
                                "/contributionPeriods",
                                "/health"
                        ).permitAll()
                        .requestMatchers(HttpMethod.POST,
                                "/events/signups/*/guest", "/users").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint))
                .addFilterAfter(headerAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler(CompositePermissionEvaluator permissionEvaluator) {
        DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
        handler.setPermissionEvaluator(permissionEvaluator);
        return handler;
    }
}