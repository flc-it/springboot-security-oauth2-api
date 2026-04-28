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

package org.flcit.springboot.security.oauth2.api;

import org.flcit.springboot.commons.core.util.EnvironmentUtils;
import org.flcit.springboot.security.oauth2.api.configuration.BaseWebSecurityConfigurer;
import org.flcit.springboot.security.oauth2.api.configuration.WebSecurityConfiguration;
import org.flcit.springboot.security.oauth2.api.filter.DefaultWebSecurityConfiguration;
import org.flcit.springboot.security.oauth2.api.token.JwtAuthenticationConverter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 
 * @since 
 * @author Florian Lestic
 */
@AutoConfiguration(afterName = "org.flcit.springboot.web.openapi.OpenApiAutoConfiguration")
@EnableMethodSecurity(jsr250Enabled = true, securedEnabled = true)
public class SecurityOAuth2ApiAutoConfiguration {

    private static final String OPEN_API_BEAN_TYPE = "org.springdoc.core.models.GroupedOpenApi";

    @Bean
    @ConditionalOnMissingBean(WebSecurityConfiguration.class)
    protected WebSecurityConfiguration getDefaultWebSecurityConfiguration() {
        return new DefaultWebSecurityConfiguration();
    }

    @Bean
    @ConditionalOnBean(type = OPEN_API_BEAN_TYPE)
    protected SecurityFilterChain getBaseWebSecurityConfigurerWithOpenApi(HttpSecurity http,
            Environment environment,
            WebSecurityConfiguration webSecurityConfiguration,
            JwtDecoder jwtDecoder,
            JwtAuthenticationConverter jwtAuthenticationConverter
            ) throws Exception {
        if (EnvironmentUtils.isEnvironmentPrd(environment)) {
            return getDefaultBaseWebSecurityConfigurer(http, webSecurityConfiguration, jwtDecoder, jwtAuthenticationConverter);
        }
        return BaseWebSecurityConfigurer.securityFilterChain(http, webSecurityConfiguration, jwtDecoder, jwtAuthenticationConverter, true);
    }

    @Bean
    @ConditionalOnMissingBean(type = OPEN_API_BEAN_TYPE)
    protected SecurityFilterChain getDefaultBaseWebSecurityConfigurer(HttpSecurity http,
            WebSecurityConfiguration webSecurityConfiguration,
            JwtDecoder jwtDecoder,
            JwtAuthenticationConverter jwtAuthenticationConverter) throws Exception {
        return BaseWebSecurityConfigurer.securityFilterChain(http, webSecurityConfiguration, jwtDecoder, jwtAuthenticationConverter);
    }

}
