package com.omega.config.securiry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.cloud.CloudPlatform;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.security.web.access.AccessDeniedHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

//    private static final Logger log = LogManager.getLogger(SecurityConfig.class);

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;

    private final OpaqueTokenIntrospector opaqueTokenIntrospector;

    private final Environment env;

    private final AccessDeniedHandler accessDeniedHandler;

    @Autowired
    public SecurityConfig(
        OpaqueTokenIntrospector opaqueTokenIntrospector,
        Environment env,
        AccessDeniedHandler accessDeniedHandler) {
        this.opaqueTokenIntrospector = opaqueTokenIntrospector;
        this.env = env;
        this.accessDeniedHandler = accessDeniedHandler;
    }

//    @Autowired(required = false)
//    public void setOpaqueTokenIntrospector(
//        OpaqueTokenIntrospector opaqueTokenIntrospector) {
//        this.opaqueTokenIntrospector = opaqueTokenIntrospector;
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .requiresChannel()
            // Heroku https config
            .requestMatchers(r -> r.getHeader("X-Forwarded-Proto") != null)
            .requiresSecure().and()
            .authorizeRequests()
            .antMatchers("/api/admin/**").access("hasRole('ADMIN')")
            .antMatchers("/oauth/token", "/api/login", "/api/register", "/api/song/download/**",
                "/api/song/upload", "/api/album/upload", "/api/album/download/**", "/api/**")
            .permitAll()
            .and()
            .csrf().disable()
            .cors()
            .and()
            .oauth2ResourceServer(httpSecurityOAuth2ResourceServerConfigurer -> {
                DefaultBearerTokenResolver bearerTokenResolver = new DefaultBearerTokenResolver();
                bearerTokenResolver.setAllowUriQueryParameter(true);
                if (CloudPlatform.HEROKU.isActive(this.env)) {
//                    log.info();
                    System.out.println("Heroku config JWT");
                    httpSecurityOAuth2ResourceServerConfigurer.jwt(jwtConfigurer ->
                        jwtConfigurer.jwkSetUri(this.jwkSetUri));
                }
                else {
//                    log.info();
                    System.out.println("Non-Heroku config Opaque Token");
                    httpSecurityOAuth2ResourceServerConfigurer.opaqueToken(opaqueTokenConfigurer ->
                        opaqueTokenConfigurer.introspector(opaqueTokenIntrospector));
                }
                httpSecurityOAuth2ResourceServerConfigurer
                    .bearerTokenResolver(bearerTokenResolver)
                    .accessDeniedHandler(this.accessDeniedHandler);
            })
            .headers()
            .frameOptions().sameOrigin().disable()
            .authorizeRequests().anyRequest().permitAll().and();
    }
}
