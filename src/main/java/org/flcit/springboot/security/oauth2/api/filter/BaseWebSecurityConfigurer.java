/*
 * Copyright 2002-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.flcit.springboot.security.oauth2.api.filter;

import java.util.Arrays;

import org.flcit.springboot.security.oauth2.api.configuration.WebSecurityConfiguration;
import org.flcit.springboot.security.oauth2.api.entrypoint.Http401UnauthorizedEntryPoint;
import org.flcit.springboot.security.oauth2.api.token.JwtAuthenticationConverter;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.info.InfoEndpoint;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.DefaultLoginPageConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy;
import org.springframework.util.ObjectUtils;

/**
 * 
 * @since 
 * @author Florian Lestic
 */
public final class BaseWebSecurityConfigurer {

    private BaseWebSecurityConfigurer() { }

    /**
     * @param http
     * @param webSecurityConfiguration
     * @return
     * @throws Exception
     */
    public static SecurityFilterChain securityFilterChain(HttpSecurity http,
            WebSecurityConfiguration webSecurityConfiguration,
            JwtDecoder jwtDecoder,
            JwtAuthenticationConverter jwtAuthenticationConverter) throws Exception {
        return securityFilterChain(http, webSecurityConfiguration, jwtDecoder, jwtAuthenticationConverter, false);
    }

    @SuppressWarnings("unchecked")
    public static SecurityFilterChain securityFilterChain(HttpSecurity http,
            WebSecurityConfiguration webSecurityConfiguration,
            JwtDecoder jwtDecoder,
            JwtAuthenticationConverter jwtAuthenticationConverter,
            boolean apiDocs) throws Exception {
        check(webSecurityConfiguration);
        // Remove useless default configuration
        http.removeConfigurer(DefaultLoginPageConfigurer.class);
        // Erase Credentials after authentication
        http.getSharedObject(AuthenticationManagerBuilder.class).eraseCredentials(true);
        return http
            .csrf(c -> c.disable())
            .requestCache(c -> c.disable())
            .logout(c -> c.disable())
            .httpBasic(c -> c.disable())
            .anonymous(c -> c.disable())
            .rememberMe(c -> c.disable())
            .formLogin(c -> c.disable())
            .sessionManagement(c -> c.disable())

            // HTTP 401 Unauthorized when no authenticated
            .exceptionHandling(customizer -> customizer.authenticationEntryPoint(new Http401UnauthorizedEntryPoint()))

            // Headers security with Referrer and CSP protection
            .headers(customizer -> customizer
                    .referrerPolicy(c -> c.policy(ReferrerPolicy.NO_REFERRER))
                    .contentSecurityPolicy(c -> c.policyDirectives(String.format("default-src '%s'", apiDocs ? "self" : "none")))
            )
            .authorizeHttpRequests(c -> {
                // Actuator endpoints public
                c.requestMatchers(EndpointRequest.to(HealthEndpoint.class, InfoEndpoint.class)).permitAll();
                // Swagger endpoints public
                if (apiDocs) {
                    c.requestMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").permitAll();
                }
                // All Actuator endpoints must have one role
                c.requestMatchers(EndpointRequest.toAnyEndpoint()).hasAnyAuthority(convert(webSecurityConfiguration.getAuthoritiesAdmin()));
                // All others API endpoints must have one role
                c.anyRequest().hasAnyAuthority(convert(webSecurityConfiguration.getAuthoritiesApi()));
            })
            .oauth2ResourceServer(c -> c.jwt(j -> j
                    .decoder(jwtDecoder)
                    .jwtAuthenticationConverter(jwtAuthenticationConverter)))
            .build();
    }

    private static final String[] convert(GrantedAuthority[] authorities) {
        return Arrays.stream(authorities).map(auth -> auth.getAuthority()).toArray(String[]::new);
    }

    private static final void check(WebSecurityConfiguration webSecurityConfiguration) {
        // All Actuator endpoints must have one role
        if (ObjectUtils.isEmpty(webSecurityConfiguration.getAuthoritiesAdmin())) {
            throw new IllegalStateException("Une configuration de sécurité avec la liste des Roles Admin de l'API est requis ! (voir WebSecurityConfiguration.getRolesAdmin)");
        }
        if (ObjectUtils.isEmpty(webSecurityConfiguration.getAuthoritiesApi())) {
            throw new IllegalStateException("Une configuration de sécurité avec la liste des Roles de l'API est requis ! (voir WebSecurityConfiguration.getRolesApi)");
        }
    }

}
