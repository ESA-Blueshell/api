package net.blueshell.api.auth;

import net.blueshell.api.business.user.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.sql.DataSource;
import java.util.Arrays;

/**
 * https://www.javacodegeeks.com/2019/03/centralized_-authorization_-oauth2_jwt.html
 */
@Configuration
public class WebSecurityConfiguration {

    private final DataSource dataSource;

    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private UserDetailsService jwtUserDetailsService;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    public WebSecurityConfiguration(DataSource dataSource) {
        // done via @Bean in DatabaseManager.
        this.dataSource = dataSource;
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        StringBuilder sb = new StringBuilder();
        for (int i = Role.values().length - 1; i >= 0; i--) {
            sb.append(Role.values()[i]).append(" > ");
        }
        hierarchy.setHierarchy(sb.substring(0, sb.toString().length() - 3));
        return hierarchy;
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        // configure AuthenticationManager so that it knows from where to load
        // user for matching credentials
        // Use BCryptPasswordEncoder
        auth.userDetailsService(jwtUserDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        if (passwordEncoder == null) {
            passwordEncoder = new BCryptPasswordEncoder();
        }
        return passwordEncoder;
    }


    protected WebSecurityCustomizer configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return (web) -> web.cors().and()
                .csrf().disable()
                // dont authenticate this particular request
                .authorizeRequests().antMatchers("/authenticate").permitAll().and()
                .authorizeRequests().antMatchers("/createMember").permitAll().and()
                .authorizeRequests().antMatchers("/createAccount").permitAll().and()
                .authorizeRequests().antMatchers("/enableAccount").permitAll().and()
                .authorizeRequests().antMatchers(HttpMethod.GET, "/news/**").permitAll().and()
                .authorizeRequests().antMatchers(HttpMethod.GET, "/events/**").permitAll().and()
                .authorizeRequests().antMatchers(HttpMethod.GET, "/download/**").permitAll().and()
                .authorizeRequests().antMatchers(HttpMethod.GET, "/newsPageable**").permitAll().and()
                .authorizeRequests().antMatchers("/users/password").permitAll().and()
                .authorizeRequests().antMatchers(HttpMethod.POST, "/events/signups/*/guest").permitAll().and()
                .authorizeRequests().antMatchers(HttpMethod.PUT, "/events/*/signups/*").permitAll().and()
                .authorizeRequests().antMatchers(HttpMethod.DELETE, "/events/*/signups/*").permitAll().and()

                // all other requests need to be authenticated
                .authorizeRequests().anyRequest().authenticated()
                .and()
                .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint);

        // Add a filter to validate the tokens with every request
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token"));
        configuration.setExposedHeaders(Arrays.asList("x-auth-token"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /*
    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication().dataSource(dataSource)
                .usersByUsernameQuery("SELECT username, password, enabled FROM users where username = ?")
                .authoritiesByUsernameQuery("SELECT username, authority FROM authorities WHERE username = ?");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .authorizeRequests()
//                .antMatchers("/", "/login", "/oauth", "/api/oauth/token").permitAll() // Change back once we decide to go from basic auth to something more proper
                .anyRequest().authenticated()
                .anyRequest().permitAll()
                .and()
                .httpBasic()
        ;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList("*"));
        configuration.setAllowedMethods(Collections.singletonList("*"));
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
*/

}
